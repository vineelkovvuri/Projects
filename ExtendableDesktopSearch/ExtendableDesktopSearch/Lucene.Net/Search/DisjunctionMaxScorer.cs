/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

using System;

namespace Lucene.Net.Search
{
	
	/// <summary> The Scorer for DisjunctionMaxQuery's.  The union of all documents generated by the the subquery scorers
	/// is generated in document number order.  The score for each document is the maximum of the scores computed
	/// by the subquery scorers that generate that document, plus tieBreakerMultiplier times the sum of the scores
	/// for the other subqueries that generate the document.
	/// </summary>
	/// <author>  Chuck Williams
	/// </author>
	class DisjunctionMaxScorer : Scorer
	{
		
		/* The scorers for subqueries that have remaining docs, kept as a min heap by number of next doc. */
		private System.Collections.ArrayList subScorers = new System.Collections.ArrayList();
		
		/* Multiplier applied to non-maximum-scoring subqueries for a document as they are summed into the result. */
		private float tieBreakerMultiplier;
		
		private bool more = false; // True iff there is a next document
		private bool firstTime = true; // True iff next() has not yet been called
		
		/// <summary>Creates a new instance of DisjunctionMaxScorer</summary>
		/// <param name="tieBreakerMultiplier">Multiplier applied to non-maximum-scoring subqueries for a document as they are summed into the result.
		/// </param>
		/// <param name="similarity">-- not used since our definition involves neither coord nor terms directly 
		/// </param>
		public DisjunctionMaxScorer(float tieBreakerMultiplier, Similarity similarity) : base(similarity)
		{
			this.tieBreakerMultiplier = tieBreakerMultiplier;
		}
		
		/// <summary>Add the scorer for a subquery</summary>
		/// <param name="scorer">the scorer of a subquery of our associated DisjunctionMaxQuery
		/// </param>
		public virtual void  Add(Scorer scorer)
		{
			if (scorer.Next())
			{
				// Initialize and retain only if it produces docs
				subScorers.Add(scorer);
				more = true;
			}
		}
		
		/// <summary>Generate the next document matching our associated DisjunctionMaxQuery.</summary>
		/// <returns> true iff there is a next document
		/// </returns>
		public override bool Next()
		{
			if (!more)
				return false;
			if (firstTime)
			{
				Heapify();
				firstTime = false;
				return true; // more would have been false if no subScorers had any docs
			}
			// Increment all generators that generated the last doc and adjust the heap.
			int lastdoc = ((Scorer) subScorers[0]).Doc();
			do 
			{
				if (((Scorer) subScorers[0]).Next())
					HeapAdjust(0);
				else
				{
					HeapRemoveRoot();
					if ((subScorers.Count == 0))
						return (more = false);
				}
			}
			while (((Scorer) subScorers[0]).Doc() == lastdoc);
			return true;
		}
		
		/// <summary>Determine the current document number.  Initially invalid, until {@link #Next()} is called the first time.</summary>
		/// <returns> the document number of the currently generated document
		/// </returns>
		public override int Doc()
		{
			return ((Scorer) subScorers[0]).Doc();
		}
		
		/// <summary>Determine the current document score.  Initially invalid, until {@link #Next()} is called the first time.</summary>
		/// <returns> the score of the current generated document
		/// </returns>
		public override float Score()
		{
			int doc = ((Scorer) subScorers[0]).Doc();
			float[] sum = new float[]{((Scorer) subScorers[0]).Score()}, max = new float[]{sum[0]};
			int size = subScorers.Count;
			ScoreAll(1, size, doc, sum, max);
			ScoreAll(2, size, doc, sum, max);
			return max[0] + (sum[0] - max[0]) * tieBreakerMultiplier;
		}
		
		// Recursively iterate all subScorers that generated last doc computing sum and max
		private void  ScoreAll(int root, int size, int doc, float[] sum, float[] max)
		{
			if (root < size && ((Scorer) subScorers[root]).Doc() == doc)
			{
				float sub = ((Scorer) subScorers[root]).Score();
				sum[0] += sub;
				max[0] = System.Math.Max(max[0], sub);
				ScoreAll((root << 1) + 1, size, doc, sum, max);
				ScoreAll((root << 1) + 2, size, doc, sum, max);
			}
		}
		
		/// <summary>Advance to the first document beyond the current whose number is greater than or equal to target.</summary>
		/// <param name="target">the minimum number of the next desired document
		/// </param>
		/// <returns> true iff there is a document to be generated whose number is at least target
		/// </returns>
		public override bool SkipTo(int target)
		{
			while (subScorers.Count > 0 && ((Scorer) subScorers[0]).Doc() < target)
			{
				if (((Scorer) subScorers[0]).SkipTo(target))
					HeapAdjust(0);
				else
					HeapRemoveRoot();
			}
			if ((subScorers.Count == 0))
				return (more = false);
			return true;
		}
		
		/// <summary>Explain a score that we computed.  UNSUPPORTED -- see explanation capability in DisjunctionMaxQuery.</summary>
		/// <param name="doc">the number of a document we scored
		/// </param>
		/// <returns> the Explanation for our score
		/// </returns>
		public override Explanation Explain(int doc)
		{
			throw new System.NotSupportedException();
		}
		
		// Organize subScorers into a min heap with scorers generating the earlest document on top.
		private void  Heapify()
		{
			int size = subScorers.Count;
			for (int i = (size >> 1) - 1; i >= 0; i--)
				HeapAdjust(i);
		}
		
		/* The subtree of subScorers at root is a min heap except possibly for its root element.
		* Bubble the root down as required to make the subtree a heap.
		*/
		private void  HeapAdjust(int root)
		{
			Scorer scorer = (Scorer) subScorers[root];
			int doc = scorer.Doc();
			int i = root, size = subScorers.Count;
			while (i <= (size >> 1) - 1)
			{
				int lchild = (i << 1) + 1;
				Scorer lscorer = (Scorer) subScorers[lchild];
				int ldoc = lscorer.Doc();
				int rdoc = System.Int32.MaxValue, rchild = (i << 1) + 2;
				Scorer rscorer = null;
				if (rchild < size)
				{
					rscorer = (Scorer) subScorers[rchild];
					rdoc = rscorer.Doc();
				}
				if (ldoc < doc)
				{
					if (rdoc < ldoc)
					{
						subScorers[i] = rscorer;
						subScorers[rchild] = scorer;
						i = rchild;
					}
					else
					{
						subScorers[i] = lscorer;
						subScorers[lchild] = scorer;
						i = lchild;
					}
				}
				else if (rdoc < doc)
				{
					subScorers[i] = rscorer;
					subScorers[rchild] = scorer;
					i = rchild;
				}
				else
					return ;
			}
		}
		
		// Remove the root Scorer from subScorers and re-establish it as a heap
		private void  HeapRemoveRoot()
		{
			int size = subScorers.Count;
			if (size == 1)
				subScorers.RemoveAt(0);
			else
			{
				subScorers[0] = subScorers[size - 1];
				subScorers.RemoveAt(size - 1);
				HeapAdjust(0);
			}
		}
	}
}
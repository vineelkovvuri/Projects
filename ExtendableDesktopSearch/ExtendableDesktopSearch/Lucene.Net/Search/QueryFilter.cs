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
using System.Runtime.InteropServices;
using IndexReader = Lucene.Net.Index.IndexReader;

namespace Lucene.Net.Search
{
	
	/// <summary>Constrains search results to only match those which also match a provided
	/// query.  Results are cached, so that searches after the first on the same
	/// index using this filter are much faster.
	/// 
	/// <p> This could be used, for example, with a {@link RangeQuery} on a suitably
	/// formatted date field to implement date filtering.  One could re-use a single
	/// QueryFilter that matches, e.g., only documents modified within the last
	/// week.  The QueryFilter and RangeQuery would only need to be reconstructed
	/// once per day.
	/// 
	/// </summary>
	/// <version>  $Id: QueryFilter.java 328729 2005-10-26 21:05:35Z yonik $
	/// </version>
	[Serializable]
	public class QueryFilter : Filter
	{
		private class AnonymousClassHitCollector : HitCollector
		{
			public AnonymousClassHitCollector(System.Collections.BitArray bits, QueryFilter enclosingInstance)
			{
				InitBlock(bits, enclosingInstance);
			}
			private void  InitBlock(System.Collections.BitArray bits, QueryFilter enclosingInstance)
			{
				this.bits = bits;
				this.enclosingInstance = enclosingInstance;
			}
			private System.Collections.BitArray bits;
			private QueryFilter enclosingInstance;
			public QueryFilter Enclosing_Instance
			{
				get
				{
					return enclosingInstance;
				}
				
			}
			public override void  Collect(int doc, float score)
			{
				bits.Set(doc, true); // set bit for hit
			}
		}
		private Query query;
		[NonSerialized]
		private System.Collections.Hashtable cache = null;
		
		/// <summary>Constructs a filter which only matches documents matching
		/// <code>query</code>.
		/// </summary>
		public QueryFilter(Query query)
		{
			this.query = query;
		}
		
		public override System.Collections.BitArray Bits(IndexReader reader)
		{
			
			if (cache == null)
			{
				cache = new System.Collections.Hashtable();
			}
			
			lock (cache.SyncRoot)
			{
				// check cache
				System.Collections.BitArray cached = (System.Collections.BitArray) cache[reader];
				if (cached != null)
				{
					return cached;
				}
			}
			
			System.Collections.BitArray bits = new System.Collections.BitArray((reader.MaxDoc() % 64 == 0?reader.MaxDoc() / 64:reader.MaxDoc() / 64 + 1) * 64);
			
			new IndexSearcher(reader).Search(query, new AnonymousClassHitCollector(bits, this));
			
			lock (cache.SyncRoot)
			{
				// update cache
				cache[reader] = bits;
			}
			
			return bits;
		}
		
		public override System.String ToString()
		{
			return "QueryFilter(" + query + ")";
		}
		
		public  override bool Equals(System.Object o)
		{
			if (!(o is QueryFilter))
				return false;
			return this.query.Equals(((QueryFilter) o).query);
		}
		
		public override int GetHashCode()
		{
			return query.GetHashCode() ^ (unchecked((int) 0x923F64B9L));     // {{Aroush-1.9}} Is this OK?!
		}
	}
}
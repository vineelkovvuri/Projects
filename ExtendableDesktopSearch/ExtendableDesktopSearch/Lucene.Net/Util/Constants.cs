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

namespace Lucene.Net.Util
{
	
	/// <summary> Some useful constants.
	/// 
	/// </summary>
	/// <author>   Doug Cutting
	/// </author>
	/// <version>  $Id: Constants.java 189792 2005-06-09 18:58:30Z bmesser $
	/// 
	/// </version>
	
	public sealed class Constants
	{
		private Constants()
		{
		} // can't construct
		
		/// <summary>The value of <tt>System.getProperty("java.version")<tt>. *</summary>
		public static readonly System.String JAVA_VERSION = System.Configuration.ConfigurationSettings.AppSettings.Get("java.version");     // {{Aroush-1.9}}
		/// <summary>True iff this is Java version 1.1. </summary>
		public static readonly bool JAVA_1_1 = JAVA_VERSION.StartsWith("1.1.");
		/// <summary>True iff this is Java version 1.2. </summary>
		public static readonly bool JAVA_1_2 = JAVA_VERSION.StartsWith("1.2.");
		/// <summary>True iff this is Java version 1.3. </summary>
		public static readonly bool JAVA_1_3 = JAVA_VERSION.StartsWith("1.3.");
		
		/// <summary>The value of <tt>System.getProperty("os.name")<tt>. *</summary>
		public static readonly System.String OS_NAME = System.Environment.GetEnvironmentVariable("OS");
		/// <summary>True iff running on Linux. </summary>
		public static readonly bool LINUX = OS_NAME.StartsWith("Linux");
		/// <summary>True iff running on Windows. </summary>
		public static readonly bool WINDOWS = OS_NAME.StartsWith("Windows");
		/// <summary>True iff running on SunOS. </summary>
		public static readonly bool SUN_OS = OS_NAME.StartsWith("SunOS");
	}
}
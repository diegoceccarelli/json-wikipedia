/**
 *  Copyright 2012 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.isti.cnr.hpc.wikipedia.reader.filter;

import it.cnr.isti.hpc.io.reader.Filter;
import it.isti.cnr.hpc.wikipedia.article.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * TypeFilter filters the articles base on their type. Types are:
 * <ul>
 * <li>"C" - Category</li>
 * <li>"T" - Template</li>
 * <li>"P" - Project</li>
 * <li>"F" - File</li>
 * <li>"M" - Main (normal article)</li>
 * </ul>
 * 
 * the constructor accepts a string with the type to keep, types that are not in
 * the string will be filter out.
 * 
 * @see Article.getType()
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class TitleFilter implements Filter<Article> {
	
	
	public static final TitleFilter ITALIAN_TITLE_FILTER = new TitleFilter("utente:","discussione:","discussioni utente:");

	List<String> patternsToFilter;

	public TitleFilter(String... patterns) {
		patternsToFilter = new ArrayList<String>();
		for (String p : patterns) {
			patternsToFilter.add(p);
		}
	}

	public boolean isFilter(Article a) {
		String t = a.getTitle().toLowerCase();
		for (String p : patternsToFilter) {
			if (t.contains(p)){
				return true;
			}
		}
		return false;
	}
}

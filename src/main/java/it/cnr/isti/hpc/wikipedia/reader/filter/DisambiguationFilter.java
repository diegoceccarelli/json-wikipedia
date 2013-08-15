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
package it.cnr.isti.hpc.wikipedia.reader.filter;

import it.cnr.isti.hpc.io.reader.Filter;
import it.cnr.isti.hpc.wikipedia.article.Article;

/**
 * Filters out/only Disambiguations
 * 
 * @see Article.isDisambiguation();
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 05/lug/2012
 */
public class DisambiguationFilter implements Filter<Article> {

	public final static DisambiguationFilter KEEP_DISAMBIGUATIONS = new DisambiguationFilter(true);
	public final static DisambiguationFilter FILTER_OUT_DISAMBIGUATIONS = new DisambiguationFilter(false);
	
	boolean keepDisambigation = true;
	
	public DisambiguationFilter(boolean keepDisambigation){
		this.keepDisambigation = keepDisambigation;
	}

	public boolean isFilter(Article a) {
		boolean isDisambiguation = a.isDisambiguation();
		return (keepDisambigation)? !isDisambiguation : isDisambiguation;
	}
	
	

}

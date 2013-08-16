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
 * Filters in/out Redirects
 * 
 * @see Article#isRedirect()
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 05/lug/2012
 */
public class RedirectFilter implements Filter<Article> {
	/** Keeps only the redirects **/
	public final static RedirectFilter KEEP_REDIRECTS = new RedirectFilter(true);
	/** Keeps only the non-redirects **/
	public final static RedirectFilter FILTER_OUT_REDIRECTS = new RedirectFilter(false);
	
	boolean keepRedirects = true;
	
	public RedirectFilter(boolean keepRedirects){
		this.keepRedirects = keepRedirects;
	}

	public boolean isFilter(Article a) {
		return (keepRedirects)? !a.isRedirect() : a.isRedirect();
	}
	
	

}

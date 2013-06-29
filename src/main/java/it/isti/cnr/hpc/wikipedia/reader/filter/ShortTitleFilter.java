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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * article is filtered if its length is fewer then a certain length (default is
 * 3)
 */
public class ShortTitleFilter implements Filter<Article> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ShortTitleFilter.class);

	public final static ShortTitleFilter FEWER_THAN_THREE = new ShortTitleFilter();

	private int minLength;

	public ShortTitleFilter() {
		this.minLength = 3;
	}

	public ShortTitleFilter(int minLength) {
		this.minLength = minLength;
	}

	public boolean isFilter(Article a) {
		String title = a.getTitle();
		if (title == null) return true;
		if (title.length() < minLength) {
			logger.debug("{} filtered (title length < {})", title, minLength);
			return true;
		}
		return false;

	}

}

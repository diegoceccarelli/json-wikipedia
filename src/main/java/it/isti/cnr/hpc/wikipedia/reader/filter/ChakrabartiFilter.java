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
 * article is filtered if it is composed purely of verbs, adverbs, conjunctions 
 * or prepositions or if it conformed to certain lexical patterns 
 * (e.g., fewer than three characters)
 * 
 * FIXME at the moment filters only title < 3, i'll need to add title black list! pos tagger 
 * sucks on titles	
 */
public class ChakrabartiFilter implements Filter<Article> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ChakrabartiFilter.class);
	
	//private static PosTagger posTagger = PosTagger.getInstance();
	public final static ChakrabartiFilter INSTANCE = new ChakrabartiFilter();
	
	
	public boolean isFilter(Article a) {
		String title = a.getTitle();
		if (title.length() < 3) {
			logger.debug("{} filtered (title length < 3)",title);
			return true;
		}
		//TODO does not work property... must use fixed lists
//		List<PosToken> tokens = posTagger.tag(title);
//		for (PosToken token : tokens){
//			if ( !   (token.isVerb() || token.isAdverb() || token.isConjunction() || token.isAbjective())) return false;
//		}
//		logger.info("{} filtered, it is composed purely of verbs, adverbs, conjunctions ",title);
		return false;
		
		
	}
	
	

}

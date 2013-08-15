/**
 *  Copyright 2013 Diego Ceccarelli
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
package it.cnr.isti.hpc.wikipedia.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;

/**
 * Generates the MediaWikiParser given a language.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 14, 2013
 */
public class MediaWikiParserFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(MediaWikiParserFactory.class);

	public MediaWikiParser getParser(String lang) {
		Locale locale = new Locale(lang);
		LocalizedMediaWikiParserFactory parserFactory = new LocalizedMediaWikiParserFactory(locale);
		return parserFactory.createParser();
	}

}

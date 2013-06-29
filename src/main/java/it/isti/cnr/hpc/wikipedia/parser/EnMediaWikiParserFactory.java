/**
 *  Copyright 2011 Diego Ceccarelli
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
package it.isti.cnr.hpc.wikipedia.parser;

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * ItMediaWikiParserFactory.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/nov/2011
 */
public class EnMediaWikiParserFactory extends MediaWikiParserFactory {
	public EnMediaWikiParserFactory() {
		super();
		initEnVariables();

	}

	private void initEnVariables() {
		getCategoryIdentifers().add("Category");
		getCategoryIdentifers().add("category");	
		getImageIdentifers().add("Image");
		getImageIdentifers().add("File");
		getImageIdentifers().add("media");	
		//setTemplateParserClass(FlushTemplates.class);
	

	}

	/**
	 * Creates a MediaWikiParser with the configurations which has been set.
	 */
	
}

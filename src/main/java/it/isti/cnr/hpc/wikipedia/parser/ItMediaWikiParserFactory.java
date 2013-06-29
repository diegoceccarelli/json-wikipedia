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

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiTemplateParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.ModularParser;

/**
 * ItMediaWikiParserFactory.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/nov/2011
 */
public class ItMediaWikiParserFactory extends MediaWikiParserFactory {
	public ItMediaWikiParserFactory() {
		super();
		initItalianVariables();

	}

	private void initItalianVariables() {
		setTemplateParserClass(ItalianTemplateParser.class);
		getImageIdentifers().add("Image");
		getImageIdentifers().add("File");
		getImageIdentifers().add("media");
		getCategoryIdentifers().add("Categoria");
		//addDeleteTemplate("nota_disambigua");
		addDeleteTemplate("tmp");
		getLanguageIdentifers().remove("it");
	}

	/**
	 * Creates a MediaWikiParser with the configurations which has been set.
	 */
	public MediaWikiParser createParser() {
		if (getParserClass() == ModularParser.class) {
			ModularParser mwgp = new ModularParser(
					// resolveLineSeparator(),
					"\n", getLanguageIdentifers(), getCategoryIdentifers(),
					getImageIdentifers(), getShowImageText(), getDeleteTags(),
					getShowMathTagContent(), getCalculateSrcSpans(), null);

			StringBuilder sb = new StringBuilder();
			sb.append(getLineSeparator() + "languageIdentifers: ");
			for (String s : getLanguageIdentifers()) {
				sb.append(s + " ");
			}
			sb.append(getLineSeparator() + "categoryIdentifers: ");
			for (String s : getCategoryIdentifers()) {
				sb.append(s + " ");
			}
			sb.append(getLineSeparator() + "imageIdentifers: ");
			for (String s : getImageIdentifers()) {
				sb.append(s + " ");
			}

			MediaWikiTemplateParser mwtp;

			if (getTemplateParserClass() == ItalianTemplateParser.class) {

				mwtp = new ItalianTemplateParser(getDeleteTemplates(),
						getParseTemplates());
			} else
				return super.createParser();
			mwgp.setTemplateParser(mwtp);
			return mwgp;
		}
		return super.createParser();

	}
}

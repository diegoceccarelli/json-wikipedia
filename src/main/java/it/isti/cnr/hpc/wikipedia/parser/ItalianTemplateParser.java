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

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiTemplateParser;

import java.util.List;

import de.tudarmstadt.ukp.wikipedia.parser.Link;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Template;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.ResolvedTemplate;

/**
 * ItalianTemplateParser.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/nov/2011
 */
public class ItalianTemplateParser implements  MediaWikiTemplateParser {
	
	private final String templatePrefix = "TEMPLATE[";
	private final String templatePostfix = "]";
	private final String parameterDivisor = ", ";
	private final String templateNotImplementedPrefix = "TEMPLATE NOT IMPLEMENTED[";
	private final String templateNotImplementedPostfix = "]";
	private final String emptyLinkText = "[ ]";
	
	
	private List<String> deleteTemplates;
	private List<String> parseTemplates;

	public ItalianTemplateParser(List<String> deleteTemplates,
			List<String> parseTemplates) {
		this.deleteTemplates = deleteTemplates;
		this.parseTemplates = parseTemplates;

	}

	public String configurationInfo() {
		StringBuilder result = new StringBuilder();
		result.append("Standard Template treatment: ShowNameAndParameters");
		result.append("\nDelete Templates: ");
		for (String s : deleteTemplates) {
			result.append("\"" + s + "\" ");
		}
		result.append("\nParse Templates: ");
		for (String s : parseTemplates) {
			result.append("\"" + s + "\" ");
		}
		return result.toString();
	}

	public ResolvedTemplate parseTemplate(Template t, ParsedPage pp) {
		// case insensitive diego
		final String templateName = t.getName().toLowerCase();

		//Show Name and Parameters as Standart treatment.
		ResolvedTemplate result = new ResolvedTemplate( t );
		result.setPreParseReplacement( ResolvedTemplate.TEMPLATESPACER );
		StringBuilder sb = new StringBuilder();
		sb.append(templatePrefix);
		sb.append( t.getName() + parameterDivisor );
		for( String s: t.getParameters()) {
			sb.append( s + parameterDivisor );
		}
		sb.delete( sb.length() - parameterDivisor.length(), sb.length() );
		sb.append(templatePostfix);
		result.setPostParseReplacement( sb.toString() );

		result.setParsedObject( t );

		//Delete Template if it is in the List
		for( String s: deleteTemplates ){
			if( s.equals(templateName) ){
				result.setPostParseReplacement( "" );
				result.setParsedObject( null );
				return result;
			}
		}

		//Parse Template if it is in the List
		for( String s: parseTemplates ){
			List<String> templateParameters = t.getParameters();

			if( s.equals(templateName)){
				if( templateName.equals("Dieser Artikel")){

// I removed that from the core API, as it is not likely to be present in most non-German articles. (TZ)
//					pp.setAboutArticle( parser.parseContentElement( templateParameters.get(0) ));

					result.setPostParseReplacement("");
					result.setParsedObject( null );
					return result;
				}
				else if( templateName.equals("Audio") || templateName.equals("Audio genau")){
					if( templateParameters.size() == 0 ) {
						break;
					}
					if( templateParameters.size() == 1 ) {
						templateParameters.add( emptyLinkText );
					}
					result.setPostParseReplacement( t.getParameters().get(1) );
					result.setParsedObject( new Link(null, t.getPos() , templateParameters.get(0), Link.type.AUDIO, null ) );

					return result;
				}
				else if( templateName.equals("Video")){
					if( templateParameters.size() == 0 ) {
						break;
					}
					if( templateParameters.size() == 1 ) {
						templateParameters.add( emptyLinkText );
					}
					result.setPostParseReplacement(t.getParameters().get(1));
					result.setParsedObject( new Link(null, t.getPos(), t.getParameters().get(0), Link.type.VIDEO, null ) );
					return result;
				}
				else{
					result.setPostParseReplacement( templateNotImplementedPrefix+  templateName + templateNotImplementedPostfix );
					return result;
				}
			}
		}

		return result;
	}
}

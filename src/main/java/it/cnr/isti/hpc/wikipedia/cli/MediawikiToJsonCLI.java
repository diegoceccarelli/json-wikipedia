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
package it.cnr.isti.hpc.wikipedia.cli;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.reader.WikipediaArticleReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MediawikiToJsonCLI converts a Wikipedia Dump in Json. <br/>
 * <br/>
 * <code>MediawikiToJsonCLI  wikipedia-dump.xml.bz -output wikipedia-dump.json[.gz] -lang [en|it] </code>
 * <br/>
 * <br/>
 * produces in wikipedia-dump.json the JSON version of the dump. Each line of
 * the file contains an article of dump encoded in JSON. Each JSON line can be
 * deserialized in an Article object, which represents an <b> enriched </b>
 * version of the wikitext page. The Article object contains:
 * 
 * <ul>
 * <li>the title (e.g., Leonardo Da Vinci);</li>
 * <li>the wikititle (used in Wikipedia as key, e.g., Leonardo_Da_Vinci);</li>
 * <li>the namespace and the integer namespace in the dump;</li>
 * <li>the timestamp of the article;</li>
 * <li>the type, if it is a standard article, a redirection, a category and so
 * on;</li>
 * <li>if it is not in English the title of the corrispondent English Article;</li>
 * <li>a list of tables that appear in the article ;</li>
 * <li>a list of lists that that appear in the article ;</li>
 * <li>a list of internal links that appear in the article;</li>
 * <li>if the article is a redirect, the pointed article;</li>
 * <li>a list of section titles in the article;</li>
 * <li>the text of the article, divided in paragraphs;</li>
 * <li>the categories and the templates of the articles;</li>
 * <li>the list of attributes found in the templates;</li>
 * <li>a list of terms highlighted in the article;</li>
 * <li>if present the infobox.</li>
 * </ul>
 * 
 * Once you have created (or downloaded) the JSON dump (say
 * <code>wikipedia.json</code>), you can iterate over the articles of the
 * collection easily using this snippet: <br/>
 * <br/>
 * <br/>
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	RecordReader&lt;Article&gt; reader = new RecordReader&lt;Article&gt;(&quot;wikipedia.json&quot;,
 * 			new JsonRecordParser&lt;Article&gt;(Article.class))
 * 			.filter(TypeFilter.STD_FILTER);
 * 
 * 	for (Article a : reader) {
 * 		// do what you want with your articles
 * 	}
 * 
 * }
 * </pre>
 * 
 * <br/>
 * <br/>
 * 
 * You can also add some filters in order to iterate on only certain articles
 * (in the example we used only the standard type filter, which excludes meta
 * pages e.g., Portal: or User: pages.
 * 
 * @see Article
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class MediawikiToJsonCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(MediawikiToJsonCLI.class);

	private static String[] params = new String[] { INPUT, OUTPUT, "lang" };

	private static final String USAGE = "java -cp $jar "
			+ MediawikiToJsonCLI.class
			+ " -input wikipedia-dump.xml.bz -output wikipedia-dump.json -lang [en|it]";

	public MediawikiToJsonCLI(String[] args) {
		super(args, params, USAGE);
	}

	public static void main(String[] args) {
		MediawikiToJsonCLI cli = new MediawikiToJsonCLI(args);
		String input = cli.getInput();
		String output = cli.getOutput();
		String lang = cli.getParam("lang");
		WikipediaArticleReader wap = new WikipediaArticleReader(input, output,
				lang);
		try {
			wap.start();
		} catch (Exception e) {
			logger.error("parsing the mediawiki {}", e.toString());
			System.exit(-1);
		}
	}

}

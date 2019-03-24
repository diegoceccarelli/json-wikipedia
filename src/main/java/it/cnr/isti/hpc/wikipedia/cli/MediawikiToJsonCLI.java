/**
 * Copyright 2011 Diego Ceccarelli
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.cnr.isti.hpc.wikipedia.cli;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.wikipedia.reader.WikipediaArticleReader;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MediawikiToJsonCLI converts a Wikipedia Dump in Json. <br>
 * <br>
 * <code>MediawikiToJsonCLI  wikipedia-dump.xml.bz -output wikipedia-dump.json[.gz] -lang [en|it]
 * </code> <br>
 * <br>
 * produces in wikipedia-dump.json the JSON version of the dump. Each line of the file contains an
 * article of dump encoded in JSON. Each JSON line can be deserialized in an Article object, which
 * represents an <b> enriched </b> version of the wikitext page. The Article object contains:
 *
 * <ul>
 *   <li>the title (e.g., Leonardo Da Vinci);
 *   <li>the wikititle (used in Wikipedia as key, e.g., Leonardo_Da_Vinci);
 *   <li>the namespace and the integer namespace in the dump;
 *   <li>the timestamp of the article;
 *   <li>the type, if it is a standard article, a redirection, a category and so on;
 *   <li>if it is not in English the title of the corrispondent English Article;
 *   <li>a list of tables that appear in the article ;
 *   <li>a list of lists that that appear in the article ;
 *   <li>a list of internal links that appear in the article;
 *   <li>if the article is a redirect, the pointed article;
 *   <li>a list of section titles in the article;
 *   <li>the text of the article, divided in paragraphs;
 *   <li>the categories and the templates of the articles;
 *   <li>the list of attributes found in the templates;
 *   <li>a list of terms highlighted in the article;
 *   <li>if present the infobox.
 * </ul>
 *
 * Once you have created (or downloaded) the JSON dump (say <code>wikipedia.json</code>), you can
 * iterate over the articles of the collection easily using this snippet: <br>
 * <br>
 * <br>
 *
 * <pre>{@code
 * RecordReader<Article> reader = new RecordReader<Article>(
 * 			"wikipedia.json",new JsonRecordParser<Article>(Article.class)
 * )
 *
 * for (Article a : reader) {
 * 	 // do what you want with your articles
 * }
 *
 * }</pre>
 *
 * <br>
 * <br>
 */
public class MediawikiToJsonCLI extends AbstractCommandLineInterface {
  /** Logger for this class */
  private static final Logger logger = LoggerFactory.getLogger(MediawikiToJsonCLI.class);

  private static String[] params = new String[] {INPUT, OUTPUT, "lang", "threads"};

  private static final String USAGE =
      "java -cp $jar "
          + MediawikiToJsonCLI.class
          + " -input wikipedia-dump.xml.bz -output wikipedia-dump.json -lang [en|it]";

  public MediawikiToJsonCLI(String[] args) {
    super(args, params, USAGE);
  }

  public static void main(String[] args) {
    MediawikiToJsonCLI cli = new MediawikiToJsonCLI(args);
    File input = new File(cli.getInput());
    File output = new File(cli.getOutput());
    String lang = cli.getParam("lang");
    String threads = cli.getParam("threads");
    try {
      WikipediaArticleReader wap = new WikipediaArticleReader(input, output, lang);
      wap.start();
    } catch (Exception e) {
      logger.error("Parsing the mediawiki", e);
      System.exit(-1);
    }
  }
}

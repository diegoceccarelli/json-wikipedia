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
package it.isti.cnr.hpc.wikipedia.cli;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.isti.cnr.hpc.wikipedia.article.Language;
import it.isti.cnr.hpc.wikipedia.reader.WikipediaArticleReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MediawikiToJsonCLI.java
 * 
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

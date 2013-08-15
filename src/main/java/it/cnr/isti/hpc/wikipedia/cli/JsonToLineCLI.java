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
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Output wikipedia dump in a particular format given as input string
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class JsonToLineCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(JsonToLineCLI.class);

	private static String[] params = new String[] { INPUT, OUTPUT, "format" };

	private static final String USAGE = "java -cp $jar "
			+ JsonToLineCLI.class
			+ " -input wikipedia-dump.json -output wikipedia-dump.txt -format es. \"i <tab> t\"";

	public JsonToLineCLI(String[] args) {
		super(args, params, USAGE);
	}

	public static void main(String[] args) {
		JsonToLineCLI cli = new JsonToLineCLI(args);
		String format = cli.getParam("format");
		ProgressLogger pl = new ProgressLogger("dumped {} articles ",100000);
		RecordReader<Article> reader = new RecordReader<Article>(
				cli.getInput(), new JsonRecordParser<Article>(Article.class));
		cli.openOutput();
		

		for (Article a : reader) {
			pl.up();
			for (int i = 0; i < format.length(); i++) {
				char c = format.charAt(i);
				if (c == 'w'){
					cli.writeInOutput(a.getWikiTitle());
					continue;
				}
				if (c == 'T'){
					cli.writeInOutput(a.getTitle());
					continue;
				}
				if (c == 't'){
					cli.writeInOutput(a.getText());
					continue;
				}
				if (c == 'C'){
					cli.writeInOutput(a.getTypeName());
					continue;
				}
				
				if (c == 'c'){
					StringBuilder sb = new StringBuilder();
					for (Link l : a.getCategories()) {
						sb.append(l.getCleanId()).append(" ");
					}
					if (sb.length() > 0)
						sb.setLength(sb.length() - 1);
					cli.writeInOutput(sb.toString());
					continue;

				}
				
				cli.writeInOutput(String.valueOf(c));
			}
				
			cli.writeInOutput("\n");

		}
		cli.closeOutput();

	}
}

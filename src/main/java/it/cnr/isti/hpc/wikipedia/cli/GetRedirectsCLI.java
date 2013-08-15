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
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.stream.JsonReader;

/**
 * Writes in the output file all the article that present a #REDIRECT after the
 * "text" tag. output is tsv: <br/>
 * <br/>
 * <code>
 *  title \t redirected-title
 * </code>
 * 
 * 
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 28/nov/2011
 */
public class GetRedirectsCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GetRedirectsCLI.class);

	public GetRedirectsCLI(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		GetRedirectsCLI cli = new GetRedirectsCLI(args);
		String input = cli.getInput();
		String output = cli.getOutput();
		cli.openOutput();
		RecordReader<Article> reader = new RecordReader<Article>(cli.getInput(),
				new JsonRecordParser<Article>(Article.class));
		
		int count = 0;
		for (Article a : reader) {
			for (Link l : a.getLinks()) {
				if (!l.isEmpty()) {
					if (a.isRedirect()) {
						cli.writeInOutput(a.getTitleInWikistyle().toLowerCase());
						cli.writeInOutput("\t");
						cli.writeInOutput(a.getRedirect().toLowerCase());
						cli.writeInOutput("\n");
					}
				}
			}
			count++;
			if (count % 100000 == 0) {
				logger.info("parsed {} articles", count);
			}
		}

		cli.closeOutput();

	}

}

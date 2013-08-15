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

/**
 * Given a json file containing the articles generates a file containing 2
 * columns:
 * <ul>
 * <li>the title of the redirected rticle in mediawiki (or the title if there is
 * no redirect)</li>
 * <li>the json describing the article</li>
 * </ul>
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class AddRedirectTitlesCLI extends AbstractCommandLineInterface {

	public AddRedirectTitlesCLI(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		AddRedirectTitlesCLI cli = new AddRedirectTitlesCLI(args);
		ProgressLogger pl = new ProgressLogger("readed {} articles", 100000);
		JsonRecordParser<Article> parser = new JsonRecordParser<Article>(
				Article.class);
		RecordReader<Article> reader = new RecordReader<Article>(
				cli.getInput(), new JsonRecordParser<Article>(Article.class));
		cli.openOutput();

		for (Article a : reader) {
			pl.up();
			if (a.isRedirect()) {
				cli.writeInOutput(a.getRedirect().toLowerCase());
			} else
				cli.writeInOutput(a.getTitleInWikistyle());
			cli.writeInOutput("\t");
			cli.writeLineInOutput(parser.encode(a));

		}

		cli.closeOutput();

	}

}

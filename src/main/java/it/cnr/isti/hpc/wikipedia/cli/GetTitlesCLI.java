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
import it.cnr.isti.hpc.wikipedia.reader.filter.RedirectFilter;
import it.cnr.isti.hpc.wikipedia.reader.filter.ShortTitleFilter;
import it.cnr.isti.hpc.wikipedia.reader.filter.TypeFilter;

/**
 * Retrieves all the titles from the Wikipedia articles, considers only pages,
 * templates and categories. Redirect are ignored, titles with length < 3 are
 * ignored
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class GetTitlesCLI extends AbstractCommandLineInterface {

	private static String[] params = new String[] { INPUT, OUTPUT };

	private static final String USAGE = "java -cp $jar " + GetTitlesCLI.class
			+ " -input wikipedia-json-dump -output titles";

	public GetTitlesCLI(String[] args) {
		super(args, params, USAGE);
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		GetTitlesCLI cli = new GetTitlesCLI(args);
		ProgressLogger pl = new ProgressLogger("dumped {} titles", 10000);
		cli.openOutput();
		RecordReader<Article> reader = new RecordReader<Article>(
				cli.getInput(), new JsonRecordParser<Article>(Article.class));

		reader.filter(ShortTitleFilter.FEWER_THAN_THREE,
				TypeFilter.MAIN_CATEGORY_TEMPLATE,
				RedirectFilter.FILTER_OUT_REDIRECTS);

		for (Article a : reader) {
			pl.up();
			cli.writeLineInOutput(a.getTitleInWikistyle());

		}
		cli.closeOutput();
	}
}

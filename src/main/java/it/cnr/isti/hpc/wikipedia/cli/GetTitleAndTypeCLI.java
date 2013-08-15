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
import it.cnr.isti.hpc.wikipedia.article.Article.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves all the titles from the wikipedia articles.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class GetTitleAndTypeCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GetTitleAndTypeCLI.class);

	private static String[] params = new String[] { INPUT, OUTPUT };

	private static final String USAGE = "java -cp $jar "
			+ GetTitleAndTypeCLI.class
			+ " -input wikipedia-json-dump -output titleAndType.tsv ";

	public GetTitleAndTypeCLI(String[] args) {
		super(args,params, USAGE);
	}

	public static void main(String[] args) {
		GetTitleAndTypeCLI cli = new GetTitleAndTypeCLI(args);
		cli.openOutput();
		
		RecordReader<Article> reader = new RecordReader<Article>(cli.getInput(),
				new JsonRecordParser<Article>(Article.class));

		ProgressLogger pl = new ProgressLogger("processed {} articles",100000);

		for (Article a : reader) {
			pl.up();
			if (a.getTitleInWikistyle() == null) {
				logger.warn("current title is null, ignoring ",
						a.getTitleInWikistyle());
				continue;
			}

			if (a.getType() == Type.UNKNOWN) {
//				logger.warn("{} has no type, ignoring ",
//						a.getTitleInWikistyle());
				continue;
			}

			cli.writeLineInOutput(a.getType()+"\t" + a.getTitle());
			
		}
		cli.closeOutput();
	}
}

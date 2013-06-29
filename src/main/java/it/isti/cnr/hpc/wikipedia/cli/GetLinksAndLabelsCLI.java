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
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.isti.cnr.hpc.wikipedia.article.Article;
import it.isti.cnr.hpc.wikipedia.article.Link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get Links and Labels extract all the internal links together with them anchor
 * text (labels). Takes in input the json dump and outputs a set of lines
 * formatted in this way: <code> idPageDest \t label \t idPageSrc </code>
 * 
 * where
 * <ul>
 * <li><b> idPageDest </b> is the id of the page pointed by the link</li>
 * <li><b> label </b> is the anchor text</li>
 * <li><b> idPageDest </b> is the id of the page containing the link</li>
 * </ul>
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class GetLinksAndLabelsCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GetLinksAndLabelsCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ GetLinksAndLabelsCLI.class
			+ " -input fileinput -output fileoutput types=\"C|T|P|F|M\" \n  To filter by multiple types concatenate them:\n e.g., to filter Categories Templates and Mains <code> -types \"CTM\"";

	public GetLinksAndLabelsCLI(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		GetLinksAndLabelsCLI cli = new GetLinksAndLabelsCLI(args);
		String input = cli.getInput();
		String output = cli.getOutput();
		cli.openOutput();
		RecordReader<Article> reader = new RecordReader<Article>(cli.getInput(),
				new JsonRecordParser<Article>(Article.class));
		int count = 0;
		for (Article a : reader) {
			for (Link l : a.getLinks()) {
				if (!l.isEmpty()) {
					cli.writeInOutput(l.getCleanId());
					cli.writeInOutput("\t");
					cli.writeInOutput(l.getDescription());
					cli.writeInOutput("\t");
					cli.writeInOutput(a.getTitleInWikistyle());
					cli.writeInOutput("\n");
				}
			}
			count++;
			if (count % 100000 == 0)
				logger.info("parsed {} articles", count);
		}

		cli.closeOutput();

	}

}

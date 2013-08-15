package it.cnr.isti.hpc.wikipedia.cli;
///**
// *  Copyright 2011 Diego Ceccarelli
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package it.isti.cnr.hpc.wikipedia.cli;
//
//import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
//import it.cnr.isti.hpc.io.reader.JsonRecordParser;
//import it.cnr.isti.hpc.io.reader.RecordReader;
//import it.isti.cnr.hpc.wikipedia.domain.Article;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.gson.stream.JsonReader;
//
///**
// * FilterArticleByTypeCLI filters the json article extracted from the wikipedia
// * dump by their type ( @see
// * it.isti.cnr.hpc.wikipedia.domain.Article.getType()). Types are:
// * <ul>
// * <li>"C" - Category</li>
// * <li>"T" - Template</li>
// * <li>"P" - Project</li>
// * <li>"F" - File</li>
// * <li>"M" - Main (normal article)</li>
// * </ul>
// * 
// * This command line interface accept 3 parameters: input, output and types. To
// * filter by multiple types concatenate them: e.g., to filter Categories
// * Templates and Mains <code> -types "CTM"  </code>
// * 
// * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
// */
// 
//public class FilterArticlesByTypeCLI extends AbstractCommandLineInterface {
//	/**
//	 * Logger for this class
//	 */
//	private static final Logger logger = LoggerFactory
//			.getLogger(FilterArticlesByTypeCLI.class);
//	private static String[] params = new String[] { INPUT, OUTPUT, "types" };
//	private static final String USAGE = "java -cp $jar "
//			+ FilterArticlesByTypeCLI.class
//			+ " -input fileinput -output fileoutput types=\"C|T|P|F|M\" \n  To filter by multiple types concatenate them:\n e.g., to filter Categories Templates and Mains <code> -types \"CTM\"";
//
//	public FilterArticlesByTypeCLI(String[] args) {
//		super(args, params, USAGE);
//	}
//
//	public static void main(String[] args) {
//		FilterArticlesByTypeCLI cli = new FilterArticlesByTypeCLI(args);
//		String input = cli.getInput();
//		String typesToFilter = cli.getParam("types");
//		cli.openOutput();
//		int count = 0;
//		RecordReader<Article> reader = new RecordReader<Article>(cli.getInput(),
//				new JsonRecordParser<Article>(Article.class));
//		for (Article a : reader) {
//			String type = a.getType();
//			if (type.isEmpty())
//				continue;
//			if (typesToFilter.contains(type)) {
//				if (count % 100000 == 0)
//					logger.info("adding page {} type {}", a.getTitle(),
//							a.getType());
//				cli.writeLineInOutput(a.toJson());
//				count++;
//			}
//
//		}
//
//		cli.closeOutput();
//
//	}
//
//}

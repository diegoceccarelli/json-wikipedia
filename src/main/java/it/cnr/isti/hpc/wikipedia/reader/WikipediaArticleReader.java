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

package it.cnr.isti.hpc.wikipedia.reader;

import com.google.gson.Gson;
import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.ArticleType;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * A reader that converts a Wikipedia dump in its json dump. The json dump will
 * contain all the article in the XML dump, one article per line. Each line will
 * be compose by the json serialization of the object Article.
 * 
 * @see Article
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 18/nov/2011
 */
public class WikipediaArticleReader {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(WikipediaArticleReader.class);

	private static final Gson GSON = new Gson();

	private WikiXMLParser wxp;
	private Handler handler;


	private ArticleParser parser;
	// private JsonRecordParser<Article> encoder;

	private static ProgressLogger pl = new ProgressLogger("parsed {} articles",
			10000);
	private static Stopwatch sw = new Stopwatch();

	/**
	 * Generates a converter from the xml to json dump.
	 *
	 * @param inputFile
	 *            - the xml file (compressed)
	 * @param outputFile
	 *            - the json output file, containing one article per line (if
	 *            the filename ends with <tt>.gz </tt> the output will be
	 *            compressed).
	 *
	 * @param lang
	 *            - the language of the dump
	 *
	 *
	 */
	public WikipediaArticleReader(String inputFile, String outputFile,
			String lang) throws IOException {
		this(new File(inputFile), new File(outputFile), lang);
	}

	/**
	 * Generates a converter from the xml to json dump.
	 *
	 * @param inputFile
	 *            - the xml file (compressed)
	 * @param outputFile
	 *            - the json output file, containing one article per line (if
	 *            the filename ends with <tt>.gz </tt> the output will be
	 *            compressed).
	 *
	 * @param lang
	 *            - the language of the dump
	 *
	 *
	 */
	public WikipediaArticleReader(File inputFile, File outputFile, String lang) throws IOException {
		handler = new JsonConverter(outputFile);
		if (outputFile.getName().contains("json")) {
			handler = new JsonConverter(outputFile);
		}
		if (outputFile.getName().contains("avro")){
			handler = new AvroConverter(outputFile);
		}
		parser = new ArticleParser(lang);
		try {
			wxp = new WikiXMLParser(new File(inputFile.getAbsolutePath()), handler);
		} catch (Exception e) {
			logger.error("creating the parser", e);
			System.exit(-1);
		}



	}

	/**
	 * Starts the parsing
	 */
	public void start() throws IOException, SAXException {

		wxp.parse();
		handler.close();
		//logger.info(sw.stat("articles"));
	}

	private abstract class Handler implements IArticleFilter, Closeable {
		public abstract void write(final Article a) throws IOException;
	}

	public class JsonConverter extends Handler {
		private final BufferedWriter out;

		private JsonConverter(){
			out = null;
		}


		public JsonConverter(final File outputFile) throws IOException {
			out = IOUtils.getPlainOrCompressedUTF8Writer(outputFile
				.getAbsolutePath());
		}

		public void process(WikiArticle page, Siteinfo si) throws IOException {
			pl.up();
			sw.start("articles");
			String title = page.getTitle();
			String id = page.getId();
			String namespace = page.getNamespace();
			Integer integerNamespace = page.getIntegerNamespace();
			String timestamp = page.getTimeStamp();

			ArticleType type = ArticleType.UNKNOWN;
			if (page.isCategory())
				type = ArticleType.CATEGORY;
			if (page.isTemplate()) {
				type = ArticleType.TEMPLATE;
				// FIXME just to go fast;
				sw.stop("articles");
				return;
			}

			if (page.isProject()) {
				type = ArticleType.PROJECT;
				// FIXME just to go fast;
				sw.stop("articles");
				return;
			}
			if (page.isFile()) {
				type = ArticleType.FILE;
				// FIXME just to go fast;
				sw.stop("articles");
				return;
			}
			if (page.isMain())
				type = ArticleType.ARTICLE;

			Article.Builder articleBuilder = Article.newBuilder();
			articleBuilder.setTitle(title);
			articleBuilder.setWid(Integer.parseInt(id));
			articleBuilder.setNamespace(namespace);
			articleBuilder.setIntegerNamespace(integerNamespace);
			articleBuilder.setTimestamp(timestamp);
			articleBuilder.setType(type);
			parser.parse(articleBuilder, page.getText());

			try {
				write(articleBuilder.build());
			} catch (IOException e) {
				logger.error("writing the output file {}", e.toString());
				throw e;
			}

			sw.stop("articles");

			return;
		}

		public void write(Article a) throws IOException {
			out.write(GSON.toJson(a));
			out.write('\n');
		}

		@Override
		public void close() throws IOException {
			out.close();
		}
	}

	public class AvroConverter extends JsonConverter {

		private final DataFileWriter<Article> dataFileWriter;

		public AvroConverter(File output) throws IOException {
			DatumWriter<Article> userDatumWriter = new SpecificDatumWriter<Article>(Article.class);
			dataFileWriter = new DataFileWriter<Article>(userDatumWriter).setCodec(CodecFactory.snappyCodec());
			dataFileWriter.create(new Article().getSchema(), output);
		}

		public void write(Article a) throws IOException {
			dataFileWriter.append(a);
		}

		@Override
		public void close() throws IOException {
			dataFileWriter.close();
		}


	}



}

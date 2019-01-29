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

import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Article.Type;
import it.cnr.isti.hpc.wikipedia.article.AvroArticle;
import it.cnr.isti.hpc.wikipedia.article.AvroLink;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			logger.error("creating the parser {}", e.toString());
			System.exit(-1);
		}



	}

	/**
	 * Starts the parsing
	 */
	public void start() throws IOException, SAXException {

		wxp.parse();
		handler.close();
		logger.info(sw.stat("articles"));
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

		public void process(WikiArticle page, Siteinfo si) {
			pl.up();
			sw.start("articles");
			String title = page.getTitle();
			String id = page.getId();
			String namespace = page.getNamespace();
			Integer integerNamespace = page.getIntegerNamespace();
			String timestamp = page.getTimeStamp();

			Type type = Type.UNKNOWN;
			if (page.isCategory())
				type = Type.CATEGORY;
			if (page.isTemplate()) {
				type = Type.TEMPLATE;
				// FIXME just to go fast;
				sw.stop("articles");
				return;
			}

			if (page.isProject()) {
				type = Type.PROJECT;
				// FIXME just to go fast;
				sw.stop("articles");
				return;
			}
			if (page.isFile()) {
				type = Type.FILE;
				// FIXME just to go fast;
				sw.stop("articles");
				return;
			}
			if (page.isMain())
				type = Type.ARTICLE;

			Article article = new Article();
			article.setTitle(title);
			article.setWikiId(Integer.parseInt(id));
			article.setNamespace(namespace);
			article.setIntegerNamespace(integerNamespace);
			article.setTimestamp(timestamp);
			article.setType(type);
			parser.parse(article, page.getText());

			try {
				write(article);
			} catch (IOException e) {
				logger.error("writing the output file {}", e.toString());
				System.exit(-1);
			}

			sw.stop("articles");

			return;
		}

		public void write(Article a) throws IOException {
			out.write(a.toJson());
			out.write('\n');
		}

		@Override
		public void close() throws IOException {
			out.close();
		}
	}

	public class AvroConverter extends JsonConverter {

		private final DataFileWriter<AvroArticle> dataFileWriter;

		public AvroConverter(File output) throws IOException {
			DatumWriter<AvroArticle> userDatumWriter = new SpecificDatumWriter<AvroArticle>(AvroArticle.class);
			dataFileWriter = new DataFileWriter<AvroArticle>(userDatumWriter).setCodec(CodecFactory.deflateCodec(6));
			dataFileWriter.create(new AvroArticle().getSchema(), output);
		}

		public void write(Article a) throws IOException {
			List<AvroLink> links = new ArrayList<>(a.getLinks().size());
			for (Link l : a.getLinks()){
				AvroLink.Builder builder = AvroLink.newBuilder().setId(l.getId())
					.setAnchor(l.getAnchor())
					.setColumnId(l.getColumnId())
					.setStart(l.getStart())
					.setEnd(l.getEnd())
					.setParagraphId(l.getParagraphId())
					.setListId(l.getListId())
					.setListItem(l.getListItem())
					.setTableId(l.getTableId())
					.setRowId(l.getRowId())
					.setColumnId(l.getColumnId())
					.setType(it.cnr.isti.hpc.wikipedia.article.Type.values()[l.getType().ordinal()]);
				links.add(builder.build());
			}
			AvroArticle avroArticle = AvroArticle.newBuilder()
				.setTitle(a.getTitle())
				.setWikiTitle(a.getWikiTitle())
				.setWid(a.getWid())
				.setIntegerNamespace(a.getIntegerNamespace())
				.setLang(a.getLang())
				.setNamespace(a.getNamespace())
				.setTimestamp(a.getTimestamp())
				.setRedirect(a.getRedirect())
				.setEnWikiTitle(a.getEnWikiTitle())
				.setParagraphs(a.getParagraphs())
				.setLinks(links)
				.build();

			dataFileWriter.append(avroArticle);
		}

		@Override
		public void close() throws IOException {
			dataFileWriter.close();
		}


	}



}

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
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Article.Type;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
public class ParallelWikipediaArticleReader {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ParallelWikipediaArticleReader.class);

	private WikiXMLParser wxp;
	private final BufferedWriter out;
	private final String lang;

	private static ProgressLogger pl = new ProgressLogger("parsed {} articles",
			10000);

	private final Object lock = new Object();

	private int gc_counter = 0;

	// preference
	// private static int pool_size = Runtime.getRuntime().availableProcessors()
	// - 1;
	private int pool_size = Runtime.getRuntime().availableProcessors();
	private int req_pool_size = (int) (Math.ceil(pool_size * 0.8));
	private int max_queue_size = pool_size * 256;

	private ThreadPoolExecutor texecutor;
	private ArrayBlockingQueue<Runnable> tqueue;

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
	public ParallelWikipediaArticleReader(String inputFile, String outputFile,
			String lang) {
		this(new File(inputFile), new File(outputFile), lang);
	}

	/**
	 * Generates a converter from the xml to json dump.
	 * 
	 * @param outputFile
	 *            - the json output file, containing one article per line (if
	 *            the filename ends with <tt>.gz </tt> the output will be
	 *            compressed).
	 * 
	 * @param lang
	 *            - the language of the dump
	 * @param numThreads
	 *            - the number of max threads in the pool
	 * 
	 * 
	 */
	public ParallelWikipediaArticleReader(String outputPath, String l,
			int numThreads) {

		File outputFile = new File(outputPath);
		JsonConverter handler = new JsonConverter();
		lang = l;

		pool_size = numThreads;
		// req_pool_size = (int) (Math.ceil(pool_size / 2));
		req_pool_size = (int) (Math.ceil(pool_size * 0.8));

		max_queue_size = pool_size * 256;

		tqueue = new ArrayBlockingQueue<Runnable>(max_queue_size);

		texecutor = new ThreadPoolExecutor(req_pool_size, pool_size, 2,
				TimeUnit.SECONDS, tqueue);
		texecutor.allowCoreThreadTimeOut(true);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				try { // close if still open somehow because of threading
					out.close();
				} catch (IOException e) {
					logger.error("closing the stream {}", e.toString());
				}

			}
		});

		try {
			wxp = new WikiXMLParser(System.in, handler);
		} catch (Exception e) {
			logger.error("creating the parser {}", e.toString());
			System.exit(-1);
		}

		out = IOUtils.getPlainOrCompressedUTF8Writer(outputFile
				.getAbsolutePath());

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
	public ParallelWikipediaArticleReader(File inputFile, File outputFile,
			String l) {
		JsonConverter handler = new JsonConverter();
		// encoder = new JsonRecordParser<Article>(Article.class);
		// parser = new ArticleParser(lang);
		lang = l;

		// if(texecutor == null){
		tqueue = new ArrayBlockingQueue<Runnable>(max_queue_size);

		texecutor = new ThreadPoolExecutor(req_pool_size, pool_size, 2,
				TimeUnit.SECONDS, tqueue);
		texecutor.allowCoreThreadTimeOut(true);
		// }

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				try { // close if still open somehow because of threading
					out.close();
				} catch (IOException e) {
					logger.error("closing the stream {}", e.toString());
				}

			}
		});

		try {
			wxp = new WikiXMLParser(inputFile.getAbsolutePath(), handler);
		} catch (Exception e) {
			logger.error("creating the parser {}", e.toString());
			System.exit(-1);
		}

		out = IOUtils.getPlainOrCompressedUTF8Writer(outputFile
				.getAbsolutePath());

	}

	/**
	 * Starts the parsing
	 */
	public void start() throws IOException, SAXException {

		wxp.parse();

		texecutor.shutdown();

		// try{
		// if (!executor.awaitTermination(1,
		// java.util.concurrent.TimeUnit.MINUTES)){
		// System.out.println("Threads didn't finish in 1 minutes!");
		// }else{

		// try{
		// out.close();
		// } catch (IOException e) {
		// logger.error("closing the stream {}", e.toString());
		// }

		// }

		// }catch(InterruptedException e2){
		// System.out.println("Error waiting for pool to close");
		// }

	}

	private synchronized void writeJson(String artjson) {

		// may not be necessary, but not taking any chances since multiple
		// threads writing
		synchronized (lock) {
			try {
				out.write(artjson);
				out.write("\n");
			} catch (IOException e) {
				logger.error("writing the output file {}", e.toString());
				System.exit(-1);
			}

		}

	}

	private class JsonThread implements Runnable {

		private WikiArticle page;

		JsonThread(WikiArticle p) {
			page = p;
		}

		@Override
		public void run() {

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
			}

			if (page.isProject()) {
				type = Type.PROJECT;
			}
			if (page.isFile()) {
				type = Type.FILE;
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

			ArticleParser parser = new ArticleParser(lang);

			parser.parse(article, page.getText());
			String jres = article.toJson();

			// //REMOVE TESTING
			// String jres = "";

			writeJson(jres);

			// just to be sure for garbage collection
			article = null;
			parser = null;
			jres = null;

			// just to be sure for garbage collection
			page = null;
			title = null;
			namespace = null;
			id = null;
			integerNamespace = null;
			timestamp = null;

			// increment progress count here
			pl.up();

		}

	}

	private class JsonConverter implements IArticleFilter {
		@Override
		public void process(WikiArticle page, Siteinfo si) {

			// garbage collection is terrible for threadpools
			// create threadpool if necessary

			// this is bullshit! why is this nec. to avoid overflows?
			// does not seem to function on big servers without
			// /first let's garbage collect periodically

			synchronized (lock) {
				gc_counter = gc_counter + 1;

				// maybe do this less freq.
				if (gc_counter % (pool_size * 1000) == 0) {
					// System.out.println("garbage collect here");

					// System.out.println("##### Heap utilization statistics [MB] #####");
					// int mb = 1024*1024;
					// System.out.println("Used Memory:" +
					// (Runtime.getRuntime().totalMemory() -
					// Runtime.getRuntime().freeMemory()) / mb);
					// System.out.println("Free Memory:" +
					// Runtime.getRuntime().freeMemory() / mb);
					// System.out.println("Total Memory:" +
					// Runtime.getRuntime().totalMemory() / mb);
					// System.out.println("Max Memory:" +
					// Runtime.getRuntime().maxMemory() / mb);

					// System.out.println("%% shutdown threadpool");
					texecutor.shutdown();
					texecutor.purge();

					// System.out.println("%% waiting for cleanup");
					while (texecutor.getActiveCount() > req_pool_size) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException ex2) {
							Thread.currentThread().interrupt();
						}

					}

					// delete all the things!
					// seems to be required for gc to act correctly
					// System.out.println("%% renewing everything");
					tqueue = null;
					texecutor = null;
					System.gc(); // reming gc that we have nulled

					// new setup
					tqueue = new ArrayBlockingQueue<Runnable>(max_queue_size);
					texecutor = new ThreadPoolExecutor(req_pool_size,
							pool_size, 2, TimeUnit.SECONDS, tqueue);
					texecutor.allowCoreThreadTimeOut(true);

				}

			}

			// if everything is cool proceed if possible

			while (tqueue.size() == max_queue_size) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex2) {
					Thread.currentThread().interrupt();
				}

			}

			// add thread
			Runnable worker = new JsonThread(page);
			texecutor.execute(worker);

			return;
		}
	}
}

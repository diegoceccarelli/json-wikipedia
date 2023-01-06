/**
 * Copyright 2011 Diego Ceccarelli
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.cnr.isti.hpc.wikipedia.reader;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.ArticleType;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * A reader that converts a Wikipedia dump in its json dump. The json dump will contain all the
 * article in the XML dump, one article per line. Each line will be compose by the json
 * serialization of the object Article.
 *
 * @see Article
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 18/nov/2011
 */
public class WikipediaArticleReader {
  /** Logger for this class */
  private static final Logger logger = LoggerFactory.getLogger(WikipediaArticleReader.class);

  private static final Gson GSON = new Gson();

  private int processedArticles;
  private WikiXMLParser wxp;
  private Handler handler;

  // Article type that we don't want to process
  // in order to speed up the parsing of a file
  private static Set<ArticleType> DEFAULT_SKIP_ARTICLE_TYPES =
      ImmutableSet.of(ArticleType.PROJECT, ArticleType.FILE, ArticleType.TEMPLATE);
  private final Set<ArticleType> skipArticleTypes;

  private ArticleParser parser;

  /**
   * Generates a converter from the xml to json/avro dump.
   *
   * @param inputFile - the xml file (compressed)
   * @param outputFile - the json output file, containing one article per line (if the filename ends
   *     with <tt>.gz </tt> the output will be compressed).
   * @param lang - the language of the dump
   */
  public WikipediaArticleReader(File inputFile, File outputFile, String lang)
      throws IOException, SAXException {
    handler = new JsonConverter(outputFile);
    processedArticles = 0;
    skipArticleTypes = DEFAULT_SKIP_ARTICLE_TYPES;
    if (outputFile.getName().contains("json")) {
      handler = new JsonConverter(outputFile);
    }
    if (outputFile.getName().contains("avro")) {
      handler = new AvroConverter(outputFile);
    }
    parser = new ArticleParser(lang);
    final InputStream stream;
    logger.info("input filename={}", inputFile.getName());
    if (inputFile.getName().equals("-")) {
      stream = System.in;
    } else {
      ProgressBarBuilder pbb =
          new ProgressBarBuilder()
              .setTaskName("Parsing Wikipedia XML")
              .setUnit("MB", 1048576)
              .setStyle(ProgressBarStyle.ASCII);
      stream = ProgressBar.wrap(new FileInputStream(inputFile.getAbsolutePath()), pbb);
    }
    wxp = new WikiXMLParser(getPlainOrCompressedReader(stream, inputFile.getName()), handler);
  }

  public void start() throws IOException, SAXException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    wxp.parse();
    handler.close();
    stopwatch.stop();
    long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    logger.info(
        "Parsed {} articles. Avg time per article {} millis",
        processedArticles,
        String.format("%.2f", millis / (float) processedArticles));
  }

  private static BufferedReader getPlainOrCompressedReader(InputStream stream, String filename)
      throws IOException {
    if (filename.endsWith(".gz")) {
      return new BufferedReader(new InputStreamReader(new GZIPInputStream(stream)));
    }
    if (filename.endsWith(".bz2")) {
      return new BufferedReader(new InputStreamReader(new BZip2CompressorInputStream(stream)));
    }
    return new BufferedReader(new InputStreamReader(stream));
  }

  private abstract class Handler implements IArticleFilter, Closeable {

    private ArticleType getArticleType(WikiArticle page) {
      if (page.isCategory()) return ArticleType.CATEGORY;
      if (page.isTemplate()) {
        return ArticleType.TEMPLATE;
      }
      if (page.isProject()) {
        return ArticleType.PROJECT;
      }
      if (page.isFile()) {
        return ArticleType.FILE;
      }
      if (page.isMain()) {
        return ArticleType.ARTICLE;
      }
      return ArticleType.UNKNOWN;
    }

    public void process(WikiArticle page, Siteinfo si) throws IOException {
      String title = page.getTitle();
      String id = page.getId();
      String namespace = page.getNamespace();
      Integer integerNamespace = page.getIntegerNamespace();
      String timestamp = page.getTimeStamp();
      final ArticleType articleType = getArticleType(page);
      if (skipArticleTypes.contains(articleType)) {
        return;
      }
      processedArticles++;

      Article.Builder articleBuilder = Article.newBuilder();
      articleBuilder.setTitle(title);
      articleBuilder.setWid(Integer.parseInt(id));
      articleBuilder.setNamespace(namespace);
      articleBuilder.setIntegerNamespace(integerNamespace);
      articleBuilder.setTimestamp(timestamp);
      articleBuilder.setType(articleType);
      parser.parse(articleBuilder, page.getText());
      try {
        write(articleBuilder.build());
      } catch (IOException e) {
        logger.error("writing the output file", e);
      }
      return;
    }

    public abstract void write(final Article a) throws IOException;
  }

  public class JsonConverter extends Handler {
    private final BufferedWriter out;

    private JsonConverter() {
      out = null;
    }

    public JsonConverter(final File outputFile) throws IOException {
      out = IOUtils.getPlainOrCompressedUTF8Writer(outputFile.getAbsolutePath());
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
      dataFileWriter =
          new DataFileWriter<Article>(userDatumWriter).setCodec(CodecFactory.snappyCodec());
      dataFileWriter.create(Article.getClassSchema(), output);
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

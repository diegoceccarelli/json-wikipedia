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
package it.cnr.isti.hpc.wikipedia.cli;

import it.cnr.isti.hpc.wikipedia.reader.WikipediaArticleReader;
import java.io.File;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** MediawikiToJsonCLI converts a Wikipedia Dump to Json or Avro. */
@Command(
    name = "mediawiki-to-json",
    mixinStandardHelpOptions = true,
    description = "MediawikiToJsonCLI converts a Wikipedia Dump to Json or Avro.")
public class MediawikiToJsonCLI implements Callable<Integer> {
  /** Logger for this class */
  private static final Logger logger = LoggerFactory.getLogger(MediawikiToJsonCLI.class);

  @Parameters(
      index = "0",
      description = "The dump to index on wikipedia, usually ending with pages-articles.xml.bz2")
  private File input;

  @Parameters(
      index = "1",
      description =
          "Where to store the dump, use json.gz extension for compressed json, avro extension for avro. Use '-' to read plain from standard input")
  private File output;

  @Option(
      names = "-l",
      description =
          "the language of the dump (default is English), it uses the same two letters encoding of wikipedia (e.g., en for English, it for Italian)")
  private String lang = "en";

  @Override
  public Integer call() {
    try {
      WikipediaArticleReader wap = new WikipediaArticleReader(input, output, lang);
      wap.start();
      return 0;
    } catch (Exception e) {
      logger.error("Parsing the mediawiki", e);
      return 1;
    }
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new MediawikiToJsonCLI()).execute(args);
    System.exit(exitCode);
  }
}

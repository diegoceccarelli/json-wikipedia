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
package it.cnr.isti.hpc.wikipedia.article.en;

import static org.junit.Assert.assertEquals;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.AvroArticle;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.LinkType;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.IOException;

import org.junit.Test;

/**
 * MediaWikiTextParsingTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 19/nov/2011
 */
public class ArticleParseImageTest {

  ArticleParser parser = new ArticleParser(Language.EN);

  @Test
  public void testParseImage() throws IOException {
    final AvroArticle.Builder article = AvroArticle.newBuilder();
    article.setTitle("Test");
    final String mediawiki = IOUtils
        .getFileAsUTF8String("./src/test/resources/en/article-with-image.txt");
    parser.parse(article, mediawiki);

    assertEquals(LinkType.IMAGE, article.getLinks().get(0).getType());
    assertEquals(LinkType.BODY, article.getLinks().get(1).getType());
    assertEquals(LinkType.IMAGE, article.getLinks().get(2).getType());
    assertEquals(LinkType.BODY, article.getLinks().get(3).getType());
  }
}

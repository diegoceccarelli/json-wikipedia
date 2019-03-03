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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.ArticleType;
import it.cnr.isti.hpc.wikipedia.article.AvroArticle;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.article.LinkType;
import it.cnr.isti.hpc.wikipedia.article.ArticleHelper;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * MediaWikiTextParsingTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 19/nov/2011
 */
public class ArticleTest {

  private AvroArticle.Builder articleBuilder;
  private ArticleParser articleParser;

  private AvroArticle parseAvroArticle(String resourcePath) {
    final String text = IOUtils.getFileAsUTF8String(resourcePath);
    articleParser.parse(articleBuilder, text);
    return articleBuilder.build();
  }

  @Before
  public void runBeforeTestMethod() throws IOException {
    articleBuilder = AvroArticle.newBuilder();
    articleBuilder.setTitle("Test"); // title must always be set before parsing
    articleBuilder.setWid(42); // wikiId must always be set before parsing
    articleBuilder.setIntegerNamespace(42); // same for the namespace
    articleBuilder.setNamespace("namespace"); // same for the timestamp
    articleBuilder.setTimestamp("timestamp");
    articleBuilder.setEnWikiTitle("Test");
    articleBuilder.setType(ArticleType.ARTICLE);
    articleParser = new ArticleParser(Language.EN);
  }

	@Test
	public void testParsing() throws IOException {
    final AvroArticle article = parseAvroArticle("./src/test/resources/en/article.txt");

    assertThat(ArticleHelper.cleanText(article.getParagraphs()).trim()).startsWith("Albedo (), or reflection coefficient, is the diffuse reflectivity or reflecting power of a surface.");
		assertEquals(5, article.getCategories().size());
		assertEquals(7,article.getSections().size());
		assertEquals(77,article.getLinks().size());
	}



	@Test
	public void testMercedes() throws IOException {
    final AvroArticle article = parseAvroArticle("./src/test/resources/en/mercedes.txt");

    assertThat(ArticleHelper.cleanText(article.getParagraphs())).startsWith("Mercedes-Benz");
		assertEquals(15, article.getCategories().size());

	}


	@Test
	public void testDisambiguation() throws IOException {
    final AvroArticle article = parseAvroArticle("./src/test/resources/en/hdis.txt");

		assertTrue("Article is not a disambiguation", ArticleHelper.isDisambiguation(article));
	}


	@Test
	public void testNotRedirect() throws IOException {
    final AvroArticle article = parseAvroArticle("./src/test/resources/en/liberalism.txt");

		assertNotEquals(ArticleType.REDIRECT, article.getType());
	}

    @Test
    public void testNoEmptyAnchors() throws IOException {
      final AvroArticle article = parseAvroArticle("./src/test/resources/en/Royal_Thai_Armed_Forces.txt");

        // No anchor should be empty
        for (final Link link : article.getLinks()){
          assertThat(link.getAnchor()).isNotEmpty();
        }

        // testing an specific anchor
        for (final Link link : article.getLinks()){
            if (link.getId().equals("HTMS_Chakri_Naruebet")) {
              assertEquals("HTMS Chakri Naruebet", link.getAnchor());
            }
        }

    }

    @Test
    public void testNoEmptyWikiIds() throws IOException {
      final AvroArticle article = parseAvroArticle("./src/test/resources/en/Cenozoic");

      for(final Link l: article.getLinks()){
        assertThat(l.getId()).isNotEmpty();
      }
    }

    @Test
    public void testEmptyLinksShouldBeFiltered() throws IOException {
        // Some annotations are incomplete on wikipedia i.e: [[]] [[ ]]
        // Those should be filtered
        final AvroArticle a = parseAvroArticle("./src/test/resources/en/Phantom_kangaroo");
        for(final Link l: a.getLinks()){
            assertFalse(l.getId().isEmpty());
            assertFalse(l.getAnchor().isEmpty());
        }
        testAnchorsInParagraphs(a);
        testAnchorsInLists(a);
    }

    @Test
    public void testParagraphLinks() throws IOException {
        final AvroArticle a = parseAvroArticle("./src/test/resources/en/ParagraphLinksTest.txt");


     // testing specific links
        for (final Link link:a.getLinks()){
        	// testing a paragraph link
            if (link.getId().equals("document")){
                assertEquals(LinkType.BODY, link.getType());
                assertEquals(0, link.getParagraphId().intValue());
            }

            // testing links at the same start and end position but different paragraphs
            if(link.getParagraphId() == 1) {
            	assertEquals("link", link.getId());
            }
            if(link.getParagraphId() == 2) {
            	assertEquals("link", link.getId());
            }
        }
        testAnchorsInParagraphs(a);
    }

    private void testAnchorsInParagraphs(AvroArticle article) {
    	final List<String> paragraphs = article.getParagraphs();
    	for(final Link link: article.getLinks()){

        if(link.getType() == LinkType.BODY) {
    			final String paragraph = paragraphs.get(link.getParagraphId());
    			final String anchor = paragraph.substring(link.getStart(), link.getEnd());
    			assertEquals(anchor, link.getAnchor());
    		}
    	}
    }

    @Test
    public void testListLinks() throws IOException {
        final AvroArticle article = parseAvroArticle("./src/test/resources/en/ListLinksTest.txt");
     // testing specific links
        for (final Link link : article.getLinks()){
            if (link.getId().equals("Lists")){
                assertEquals(LinkType.LIST, link.getType());
                assertEquals(0, link.getListId().intValue());
                assertEquals(0, link.getListItem().intValue());
            }
            if (link.getId().equals("every")){
                assertEquals(LinkType.LIST, link.getType());
                assertEquals(0, link.getListId().intValue());
                assertEquals(1, link.getListItem().intValue());
            }
            if (link.getId().equals("newline")){
                assertEquals(LinkType.LIST, link.getType());
                assertEquals(1, link.getListId().intValue());
                assertEquals(0, link.getListItem().intValue());
            }
        }
        testAnchorsInLists(article);
    }

    private void testAnchorsInLists(AvroArticle article) {
    	final List<List<String>> lists = article.getLists();
    	for(final Link link: article.getLinks()){
    		if(link.getType() == LinkType.LIST) {
    			final List<String> list = lists.get(link.getListId());
    			final String item = list.get(link.getListItem());
    			final String anchor = item.substring(link.getStart(), link.getEnd());
    			assertEquals(anchor, link.getAnchor());
    		}
    	}
    }

    @Test
    public void testTableLinks() throws IOException {
        AvroArticle article = parseAvroArticle("./src/test/resources/en/International_Military_Tribunal_for_the_Far_East");

     // testing specific links
        for (final Link link: article.getLinks()){
            if (link.getId().equals("William_Webb")){
                assertEquals(LinkType.TABLE, link.getType());
                assertEquals(0, link.getTableId().intValue());
                assertEquals(2, link.getRowId().intValue());
                assertEquals(1, link.getColumnId().intValue());
            }
            if (link.getId().equals("Canada")){
                assertEquals(LinkType.TABLE, link.getType());
                assertEquals(0, link.getTableId().intValue());
                assertEquals(3, link.getRowId().intValue());
                assertEquals(0, link.getColumnId().intValue());
            }
            if (link.getId().equals("Alan_Mansfield")){
                assertEquals(LinkType.TABLE, link.getType());
                assertEquals(1, link.getTableId().intValue());
                assertEquals(3, link.getRowId().intValue());
                assertEquals(1, link.getColumnId().intValue());
            }
        }
    }
}

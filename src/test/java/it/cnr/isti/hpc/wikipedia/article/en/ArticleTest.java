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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.article.Link.Type;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

/**
 * MediaWikiTextParsingTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 19/nov/2011
 */
public class ArticleTest {

	ArticleParser parser = new ArticleParser(Language.EN);
	
	
	@Test
	public void testParsing() throws IOException {
		final Article a = new Article();
		final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/article.txt");
		parser.parse(a, mediawiki);
		assertTrue("Wrong parsed text",a.getCleanText().trim().startsWith("Albedo (), or reflection coefficient, is the diffuse reflectivity or reflecting power of a surface."));
		assertEquals(5, a.getCategories().size());
		assertEquals(7,a.getSections().size());
		assertEquals(77,a.getLinks().size());
		
	}
	

	
	@Test
	public void testMercedes() throws IOException {
		final Article a = new Article();
		final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/mercedes.txt");
		parser.parse(a, mediawiki);
		assertTrue(a.getCleanText().startsWith("Mercedes-Benz"));
		assertEquals(15, a.getCategories().size());
		
	}
	
	
	@Test
	public void testDisambiguation() throws IOException {
		final Article a = new Article();
		final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/hdis.txt");
		parser.parse(a, mediawiki);
		assertTrue(a.isDisambiguation());
		
	}
	
	
	@Test
	public void testNotRedirect() throws IOException {
		final Article a = new Article();
		final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/liberalism.txt");
		parser.parse(a, mediawiki);
		assertTrue(! a.isRedirect());
		
		
	}

    @Test
    public void testNoEmptyAnchors() throws IOException {
        final Article a = new Article();
        final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/Royal_Thai_Armed_Forces.txt");
        parser.parse(a, mediawiki);

        // No anchor should be empty
        for (final Link link:a.getLinks()){
            assertFalse(link.getAnchor().isEmpty());
        }

        // testing an specific anchor
        for (final Link link:a.getLinks()){
            if (link.getId().equals("HTMS_Chakri_Naruebet")) {
              assertEquals("HTMS Chakri Naruebet", link.getAnchor());
            }
        }

    }
    
    @Test
    public void testNoEmptyWikiIds() throws IOException {
        final Article a = new Article();
        final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/Cenozoic");
        parser.parse(a, mediawiki);

        for(final Link l: a.getLinks()){
        	assertFalse(l.getId().isEmpty());
        }
    }
    
    @Test
    public void testEmptyLinksShouldBeFiltered() throws IOException {
        // Some annotations are incomplete on wikipedia i.e: [[]] [[ ]]
        // Those should be filtered
        final Article a = new Article();
        final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/Phantom_kangaroo");
        parser.parse(a, mediawiki);
        for(final Link l: a.getLinks()){
            assertFalse(l.getId().isEmpty());
            assertFalse(l.getAnchor().isEmpty());
        }
        testAnchorsInParagraphs(a);
        testAnchorsInLists(a);
    }
	
    @Test
    public void testParagraphLinks() throws IOException {
        final Article a = new Article();
        final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/ParagraphLinksTest.txt");
        parser.parse(a, mediawiki);
        
     // testing specific links
        for (final Link link:a.getLinks()){
        	// testing a paragraph link
            if (link.getId().equals("document")){
                assertEquals(Link.Type.BODY, link.getType());
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
    
    private void testAnchorsInParagraphs(Article article) {
    	final List<String> paragraphs = article.getParagraphs();
    	for(final Link link: article.getLinks()){
    		if(link.getType() == Type.BODY) {
    			final String paragraph = paragraphs.get(link.getParagraphId());
    			final String anchor = paragraph.substring(link.getStart(), link.getEnd());
    			assertEquals(anchor, link.getAnchor());
    		}
    	}
    }
    
    @Test
    public void testListLinks() throws IOException {
        final Article a = new Article();
        final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/ListLinksTest.txt");
        parser.parse(a, mediawiki);
        
     // testing specific links
        for (final Link link:a.getLinks()){
            if (link.getId().equals("Lists")){
                assertEquals(Link.Type.LIST, link.getType());
                assertEquals(0, link.getListId());
                assertEquals(0, link.getListItem());
            }
            if (link.getId().equals("every")){
                assertEquals(Link.Type.LIST, link.getType());
                assertEquals(0, link.getListId());
                assertEquals(1, link.getListItem());
            }
            if (link.getId().equals("newline")){
                assertEquals(Link.Type.LIST, link.getType());
                assertEquals(1, link.getListId());
                assertEquals(0, link.getListItem());
            }
        }
        testAnchorsInLists(a);
    }
	
    private void testAnchorsInLists(Article article) {
    	final List<List<String>> lists = article.getLists();
    	for(final Link link: article.getLinks()){
    		if(link.getType() == Type.LIST) {
    			final List<String> list = lists.get(link.getListId());
    			final String item = list.get(link.getListItem());
    			final String anchor = item.substring(link.getStart(), link.getEnd());
    			assertEquals(anchor, link.getAnchor());
    		}
    	}
    }
    
    @Test
    public void testTableLinks() throws IOException {
        final Article a = new Article();
        final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/International_Military_Tribunal_for_the_Far_East");
        parser.parse(a, mediawiki);
        
     // testing specific links
        for (final Link link:a.getLinks()){
            if (link.getId().equals("William_Webb")){
                assertEquals(Link.Type.TABLE, link.getType());
                assertEquals(0, link.getTableId());
                assertEquals(2, link.getRowId());
                assertEquals(1, link.getColumnId());
            }
            if (link.getId().equals("Canada")){
                assertEquals(Link.Type.TABLE, link.getType());
                assertEquals(0, link.getTableId());
                assertEquals(3, link.getRowId());
                assertEquals(0, link.getColumnId());
            }
            if (link.getId().equals("Alan_Mansfield")){
                assertEquals(Link.Type.TABLE, link.getType());
                assertEquals(1, link.getTableId());
                assertEquals(3, link.getRowId());
                assertEquals(1, link.getColumnId());
            }
        }
    }
//
//  @Test
//  public void testTableTypes() throws IOException {
//    final Article a = new Article();
//    final String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/LinkInCaption");
//    parser.parse(a, mediawiki);
//    Gson gson = new Gson();
//    System.out.println(gson.toJson(a));
//  }




}

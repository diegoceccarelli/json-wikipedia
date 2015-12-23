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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.IOException;

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
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/article.txt");
		parser.parse(a, mediawiki);
		assertTrue("Wrong parsed text",a.getCleanText().trim().startsWith("Albedo (), or reflection coefficient, is the diffuse reflectivity or reflecting power of a surface."));
		assertEquals(5, a.getCategories().size());
		assertEquals(7,a.getSections().size());
		assertEquals(74,a.getLinks().size());
		
	}
	

	
	@Test
	public void testMercedes() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/mercedes.txt");
		parser.parse(a, mediawiki);
		assertTrue(a.getCleanText().startsWith("Mercedes-Benz"));
		assertEquals(15, a.getCategories().size());
		
	}
	
	
	@Test
	public void testDisambiguation() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/hdis.txt");
		parser.parse(a, mediawiki);
		assertTrue(a.isDisambiguation());
		
	}
	
	
	@Test
	public void testNotRedirect() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/liberalism.txt");
		parser.parse(a, mediawiki);
		System.out.println(a.getRedirect());
		assertTrue(! a.isRedirect());
		
		
	}

    @Test
    public void testNoEmptyAnchors() throws IOException {
        Article a = new Article();
        String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/Royal_Thai_Armed_Forces.txt");
        parser.parse(a, mediawiki);

        // No anchor should be empty
        for (Link link:a.getLinks()){
            assertFalse(link.getAnchor().isEmpty());
        }

        // testing an specific anchor
        for (Link link:a.getLinks()){
            if (link.getId().equals("HTMS_Chakri_Naruebet"))
                assertEquals(link.getAnchor(),"HTMS Chakri Naruebet");
        }


    }
	
    @Test
    public void testParagraphLinks() throws IOException {
        Article a = new Article();
        String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/ParagraphLinksTest.txt");
        parser.parse(a, mediawiki);
        
     // testing specific links
        for (Link link:a.getLinks()){
        	// testing a paragraph link
            if (link.getId().equals("document")){
                assertEquals(link.getType(), Link.Type.BODY);
                assertEquals(link.getParagraphIndex(), 0);
            }
            //TODO: test a non paragraph link
            // testing links at the same start and end position but different paragraphs
            if(link.getParagraphIndex() == 1) {
            	assertEquals(link.getId(), "link");
            }
            if(link.getParagraphIndex() == 2) {
            	assertEquals(link.getId(), "link");
            }
        }
    }
	
  
}

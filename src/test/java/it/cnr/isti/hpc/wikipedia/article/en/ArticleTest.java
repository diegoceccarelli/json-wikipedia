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

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.*;

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Link;

import it.cnr.isti.hpc.wikipedia.article.ParagraphWithLinks;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.cnr.isti.hpc.wikipedia.reader.WikipediaArticleReader;
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

        // first paragraph
        for(ParagraphWithLinks p:  a.getParagraphsWithLinks()){
            for(Link link: p.getLinks()){
                String anchorInPar = p.getParagraph().substring(link.getStart(),link.getEnd() );


                System.out.println("--------------");
                System.out.println(anchorInPar);
                System.out.println(link.getAnchor());
                System.out.println(p.getParagraph());

                assertEquals(anchorInPar, link.getAnchor());

            }
        }


		
	}
	

	
	@Test
	public void testMercedes() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/mercedes.txt");
		parser.parse(a, mediawiki);
		assertTrue(a.getCleanText().startsWith("Mercedes-Benz"));
		assertEquals(15, a.getCategories().size());
		
	}
	
	
//@Test
//public void testDisambiguation() throws IOException {
//	Article a = new Article();
//	String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/hdis.txt");
//	parser.parse(a, mediawiki);
//	assertTrue(a.isDisambiguation());
//
//}
//
	
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
        String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/Royal_Thai_Armed_Forces");
        parser.parse(a, mediawiki);

        // No anchor should be empty
        for (Link link : a.getLinks()) {
            assert (!link.getAnchor().isEmpty());
        }

        // testing an specific anchor
        for (Link link : a.getLinks()) {
            if (link.getId()=="HTMS_Chakri_Naruebet")
                assert (link.getAnchor()=="HTMS Chakri Naruebet");
        }
    }

    @Test
    public void testNoEmptyWikiIds() throws IOException {
        Article a = new Article();
        String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/Cenozoic");
        parser.parse(a, mediawiki);

        for(Link l: a.getLinks()){
            assert(!l.getId().equals(""));
        }

        for (ParagraphWithLinks p : a.getParagraphsWithLinks()) {
            for(Link link:p.getLinks()){
                assert (!link.getId().isEmpty());
            }
        }

    }


    @Test
    public void testAnnotationsInTables() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/International_Military_Tribunal_for_the_Far_East");
		parser.parse(a, mediawiki);

		List<String> uris = new ArrayList<String>();
		List<String> anchors = new ArrayList<String>();

		for (ParagraphWithLinks p : a.getParagraphsWithLinks()) {
			for(Link l:p.getLinks()){
				uris.add(l.getId());
				anchors.add(l.getAnchor());
			}

		}

		assertThat(uris, hasItems("Hsiang_Che-chun", "Arthur_Strettell_Comyns_Carr", "New_Zealand_Army"));
		assertThat(anchors, hasItems("Hsiang Che-chun", "Arthur Strettell Comyns Carr", "New Zealand Army"));
		testAnchorsInText(a);
    }

    @Test
    public void testAnnotationsInLists() throws IOException {
        Article a = new Article();
        String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/Hayami");
        parser.parse(a, mediawiki);

		List<String> uris = new ArrayList<String>();
		List<String> anchors = new ArrayList<String>();

		for (ParagraphWithLinks p : a.getParagraphsWithLinks()) {
			for(Link l:p.getLinks()){
				uris.add(l.getId());
				anchors.add(l.getAnchor());
			}
		}

		assertThat(uris, hasItems("Yū_Hayami", "Takumi_Hayami", "Hayami_District,_Ōita", "Mokomichi_Hayami"));
		assertThat(anchors, hasItems("Takumi Hayami", "Dogen Handa", "Sky Girls"));
		testAnchorsInText(a);

    }


    @Test
    public void testEmptyLinksShouldBeFiltered() throws IOException {
        // Some annotations are incomplete on wikipedia i.e: [[]] [[ ]]
        // Those should be filtered
        Article a = new Article();
        String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en/Phantom_kangaroo");
        parser.parse(a, mediawiki);

        for(Link l: a.getLinks()){
            assert(!l.getId().equals(""));
            assert(!l.getAnchor().equals(""));
        }
    }


	/*
	* Matches the extracted anchors and spans against the text
	* they have been extracted from.
	* */
	private void testAnchorsInText(Article article){
		// first paragraph
		for(ParagraphWithLinks p:  article.getParagraphsWithLinks()){
			for(Link link: p.getLinks()){
				String anchorInPar = p.getParagraph().substring(link.getStart(),link.getEnd());
				assertEquals(anchorInPar, link.getAnchor());

			}
		}
	}

}

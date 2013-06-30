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
package it.isti.cnr.hpc.wikipedia.reader;

import static org.junit.Assert.*;
import it.cnr.isti.hpc.io.IOUtils;
import it.isti.cnr.hpc.wikipedia.article.Article;
import it.isti.cnr.hpc.wikipedia.article.Language;
import it.isti.cnr.hpc.wikipedia.article.Link;
import it.isti.cnr.hpc.wikipedia.parser.ArticleParser;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * MediaWikiTextParsingTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 19/nov/2011
 */
public class MediaWikiTextParsingTest {

	ArticleParser parser = new ArticleParser(Language.EN);
	
	
	@Test
	public void test() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/en-article.txt");
		parser.parse(a, mediawiki);
		assertTrue("Wrong parsed text",a.getCleanText().trim().startsWith("Albedo (), or reflection coefficient, is the diffuse reflectivity or reflecting power of a surface."));
		assertEquals(5, a.getCategories().size());
		assertEquals(7,a.getSections().size());
		assertEquals(74,a.getLinks().size());
		
	}
	

	@Test
	public void testInfobox() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/article-with-infobox.txt");
		parser.parse(a, mediawiki);
		assertTrue(a.hasInfobox());
		assertEquals(12,a.getInfobox().getSchema().size());
	}
//	
	@Test
	public void testMercedes() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/mercedes.txt");
		parser.parse(a, mediawiki);
		assertTrue(a.getCleanText().startsWith("Mercedes-Benz"));
		assertEquals(15, a.getCategories().size());
		
	}
	
	
	
  
}

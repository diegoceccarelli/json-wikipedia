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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.reader.WikipediaArticleReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * WikipediaArticleReaderTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 18/nov/2011
 */
public class WikipediaArticleReaderTest {

	@Test
	public void testParsing() throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
		URL u = this.getClass().getResource("/en/mercedes.xml");
		WikipediaArticleReader wap = new WikipediaArticleReader(u.getFile(),"/tmp/mercedes.json.gz", Language.EN);
		wap.start();
		String json = IOUtils.getFileAsUTF8String("/tmp/mercedes.json.gz");
		Article a = Article.fromJson(json);
		assertTrue(a.getCleanText().startsWith("Mercedes-Benz"));
		assertEquals(15, a.getCategories().size());
		
		
	}

}

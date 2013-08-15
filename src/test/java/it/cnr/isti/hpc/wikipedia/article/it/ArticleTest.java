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
package it.cnr.isti.hpc.wikipedia.article.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Template;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ArticleTest.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 19/nov/2011
 */
public class ArticleTest {
	private static Article a = new Article();
	private static ArticleParser articleParser = new ArticleParser(Language.IT);

	@BeforeClass
	public static void loadArticle() throws IOException {
		String text = readFileAsString("/it/xml-dump/article.txt");

		articleParser.parse(a, text);
	}

	@Test
	public void sections() throws IOException {

		assertTrue(a.getSections().contains("Armonium occidentale"));
		assertTrue(a.getSections().contains("Armonium indiano"));
		assertTrue(a.getSections().contains("Bibliografia"));
		assertTrue(a.getSections().contains("Collegamenti esterni"));

	}

	@Test
	public void categories() throws IOException {

		assertEquals(1, a.getCategories().size());
		assertEquals("Categoria:Aerofoni a mantice", a.getCategories().get(0)
				.getDescription());
	}

	@Test
	public void links() throws IOException {

		assertEquals("strumento musicale", a.getLinks().get(0).getDescription());
		assertEquals("Giovanni Tamburini",
				a.getLinks().get(a.getLinks().size() - 1).getDescription());

	}

	
	@Test
	public void testInfobox() throws IOException {
		Article articleWithInfobox = new Article();

		String text = readFileAsString("/it/xml-dump/article-with-infobox.txt");
		articleParser.parse(articleWithInfobox, text);
		
		assertTrue(articleWithInfobox.hasInfobox());
		Template infobox = articleWithInfobox.getInfobox();
		assertEquals(12,infobox.getSchema().size());
		assertEquals("Infobox_fiume", infobox.getName());
		assertEquals("Adige", infobox.get("nome"));
		assertEquals("12200", infobox.get("bacino"));
		

	}

	@Test
	public void table() throws IOException {
		Article articleWithTable = new Article();
		String text = readFileAsString("/it/xml-dump/table.txt");
		articleParser.parse(articleWithTable, text);
		assertEquals("Nome italiano", articleWithTable.getTables().get(0)
				.getColumn(1).get(0));
		assertEquals("15 agosto", articleWithTable.getTables().get(0)
				.getColumn(0).get(10));

	}

	@Test
	public void list() throws IOException {

		String text = readFileAsString("/it/xml-dump/list.txt");
		Article articleWithList = new Article();
		articleParser.parse(articleWithList, text);
		List<String> list = articleWithList.getLists().get(2);
		assertEquals("Antropologia culturale e Antropologia dei simboli", list.get(0));
		
	}

	//
	// @Test
	// public void testLists2() throws IOException {
	//
	// String text = readFileAsString("/list2.txt");
	// Article a = new Article();
	// articleParser.parse(a, text);
	// System.out.println(a);
	// }

	// @Test
	// public void testMercedes() throws IOException {
	//
	// String text = readFileAsString("/mercedes.txt");
	// Article a = new Article();
	// articleParser = new ArticleParser(Language.EN);
	// articleParser.parse(a, text);
	// System.out.println(a);
	// }
	//
	// @Test
	// public void testRedirect() throws IOException {
	//
	// String text = readFileAsString("/redirect.txt");
	// Article a = new Article();
	// articleParser = new ArticleParser(Language.EN);
	// articleParser.parse(a, text);
	// assertTrue(a.isRedirect());
	// System.out.println(a.getRedirect());
	// }

	private static String readFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				ArticleTest.class.getResourceAsStream(filePath), "UTF-8"));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
}

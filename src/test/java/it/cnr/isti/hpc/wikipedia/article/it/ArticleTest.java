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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import it.cnr.isti.hpc.wikipedia.ArticleType;
import it.cnr.isti.hpc.wikipedia.AvroArticle;

import it.cnr.isti.hpc.wikipedia.Language;
import it.cnr.isti.hpc.wikipedia.Template;
import it.cnr.isti.hpc.wikipedia.article.TemplateHelper;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * ArticleTest.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 19/nov/2011
 */
public class ArticleTest {


	private AvroArticle.Builder articleBuilder;
	private ArticleParser articleParser;

  @Before
  public void runBeforeTestMethod() throws IOException {
    System.out.println("@Before - runBeforeTestMethod");
    articleBuilder = AvroArticle.newBuilder();
    articleBuilder.setTitle("Test"); // title must always be set before parsing
		articleBuilder.setWid(42); // wikiId must always be set before parsing
		articleBuilder.setIntegerNamespace(42); // same for the namespace
		articleBuilder.setNamespace("namespace"); // same for the timestamp
		articleBuilder.setTimestamp("timestamp");
		articleBuilder.setEnWikiTitle("Test");
		articleBuilder.setType(ArticleType.ARTICLE);


		articleParser = new ArticleParser(Language.IT);

  }

	@Test
	public void sections() throws IOException {
    String text = readFileAsString("/it/xml-dump/article.txt");
    articleParser.parse(articleBuilder, text);
    AvroArticle article = articleBuilder.build();
    List<String> sections = article.getSections();
    assertThat(sections).contains("Armonium occidentale");
		assertThat(sections).contains("Armonium indiano");
		assertThat(sections).contains("Bibliografia");
		assertThat(sections).contains("Collegamenti esterni");
	}

	@Test
	public void categories() throws IOException {
		String text = readFileAsString("/it/xml-dump/article.txt");
		articleParser.parse(articleBuilder, text);
		AvroArticle article = articleBuilder.build();
		assertEquals(1, article.getCategories().size());
		assertEquals("Categoria:Aerofoni a mantice", article.getCategories().get(0)
				.getAnchor());
	}

	@Test
	public void links() throws IOException {
		String text = readFileAsString("/it/xml-dump/article.txt");
		articleParser.parse(articleBuilder, text);
		AvroArticle article = articleBuilder.build();
		assertEquals("strumento musicale", article.getLinks().get(0).getAnchor());
		assertEquals("Giovanni Tamburini",
			article.getLinks().get(article.getLinks().size() - 1).getAnchor());

	}

	
	@Test
	public void testInfobox() throws IOException {
		String text = readFileAsString("/it/xml-dump/article-with-infobox.txt");
		articleParser.parse(articleBuilder, text);
		AvroArticle article = articleBuilder.build();
		Template infobox = article.getInfobox();
		assertEquals(12, TemplateHelper.getSchema(infobox).size());
		assertEquals("Infobox_fiume", infobox.getName());
		assertEquals("Adige", TemplateHelper.getTemplateAsMap(infobox).get("nome"));
		assertEquals("12200", TemplateHelper.getTemplateAsMap(infobox).get("bacino"));
	}

	@Test
	public void table() throws IOException {
		String text = readFileAsString("/it/xml-dump/table.txt");
		articleParser.parse(articleBuilder, text);
		AvroArticle article = articleBuilder.build();

		assertEquals("Nome italiano", article.getTables().get(0).getTable()
				.get(0).get(1));
		assertEquals("15 agosto", article.getTables().get(0).getTable()
				.get(10).get(0));

	}

	@Test
	public void testThatListsAreParsedProperly() throws IOException {
		String text = readFileAsString("/it/xml-dump/list.txt");
		articleParser.parse(articleBuilder, text);
		AvroArticle article = articleBuilder.build();

		List<String> list = article.getLists().get(2);
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

	private String readFileAsString(String filePath)
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

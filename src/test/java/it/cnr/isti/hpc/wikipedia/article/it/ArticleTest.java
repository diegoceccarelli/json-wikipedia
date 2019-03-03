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

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.ArticleType;
import it.cnr.isti.hpc.wikipedia.article.AvroArticle;

import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Template;
import it.cnr.isti.hpc.wikipedia.article.TemplateHelper;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

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
		articleParser = new ArticleParser(Language.IT);
  }

	@Test
	public void testParseSections() throws IOException {
		AvroArticle article = parseAvroArticle("./src/test/resources/it/article.txt");

    List<String> sections = article.getSections();
    assertThat(sections).contains("Armonium occidentale");
		assertThat(sections).contains("Armonium indiano");
		assertThat(sections).contains("Bibliografia");
		assertThat(sections).contains("Collegamenti esterni");
	}

	@Test
	public void testParseCategories() throws IOException {
		AvroArticle article = parseAvroArticle("./src/test/resources/it/article.txt");

		assertEquals(1, article.getCategories().size());
		assertEquals("Categoria:Aerofoni a mantice", article.getCategories().get(0)
				.getAnchor());
	}

	@Test
	public void testParseLinks() throws IOException {
		AvroArticle article = parseAvroArticle("./src/test/resources/it/article.txt");

		assertEquals("strumento musicale", article.getLinks().get(0).getAnchor());
		assertEquals("Giovanni Tamburini",
			article.getLinks().get(article.getLinks().size() - 1).getAnchor());

	}

	
	@Test
	public void testParseInfobox() throws IOException {
		AvroArticle article = parseAvroArticle("./src/test/resources/it/article-with-infobox.txt");

		Template infobox = article.getInfobox();
		assertEquals(12, TemplateHelper.getSchema(infobox).size());
		assertEquals("Infobox_fiume", infobox.getName());
		assertEquals("Adige", TemplateHelper.getTemplateAsMap(infobox).get("nome"));
		assertEquals("12200", TemplateHelper.getTemplateAsMap(infobox).get("bacino"));
	}

	@Test
	public void testParseTable() throws IOException {
		AvroArticle article = parseAvroArticle("./src/test/resources/it/table.txt");

		assertEquals("Nome italiano", article.getTables().get(0).getTable()
				.get(0).get(1));
		assertEquals("15 agosto", article.getTables().get(0).getTable()
				.get(10).get(0));

	}

	@Test
	public void testThatListsAreParsedProperly() throws IOException {
		AvroArticle article = parseAvroArticle("./src/test/resources/it/list.txt");

		List<String> list = article.getLists().get(2);
		assertEquals("Antropologia culturale e Antropologia dei simboli", list.get(0));
	}
}

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
package it.isti.cnr.hpc.wikipedia.article;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.isti.cnr.hpc.wikipedia.article.Article;
import it.isti.cnr.hpc.wikipedia.article.Language;
import it.isti.cnr.hpc.wikipedia.parser.ArticleParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * ArticleTest.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 19/nov/2011
 */
public class ArticleTest {
	ArticleParser articleParser = new ArticleParser(Language.IT);
	JsonRecordParser<Article> recordParser = new JsonRecordParser<Article>(
			Article.class);
	
	

//	@Test
//	public void test() throws IOException {
//
//		String text = readFileAsString("/article.txt");
//		Article a = new Article();
//		articleParser.parse(a, text);
//		System.out.println(a);
//	}
//
//	@Test
//	public void testJson() throws IOException {
//
//		String text = readFileAsString("/article.txt");
//		Article a = new Article();
//		articleParser.parse(a, text);
//		System.out.print("JSON = ");
//		String json = a.toJson();
//		System.out.println(json);
//		Article b = Article.fromJson(json);
//		assertEquals(a, b);
//	}
//
//	@Test
//	public void testSandbox() throws IOException {
//
//		String text = readFileAsString("/article-with-infobox.txt");
//		Article a = new Article();
//		articleParser.parse(a, text);
//
//		System.out.println(a);
//	}
//
//	@Test
//	public void testTable() throws IOException {
//
//		String text = readFileAsString("/table.txt");
//		Article a = new Article();
//		articleParser.parse(a, text);
//		System.out.println(a);
//		System.out.println(a.getTables().get(0).getColumn(1));
//	}
//
//	@Test
//	public void testTable2() throws IOException {
//
//		String text = readFileAsString("/table2.txt");
//		Article a = new Article();
//		articleParser.parse(a, text);
//		System.out.println(a);
//	}
//
//	@Test
//	public void testLists() throws IOException {
//
//		String text = readFileAsString("/list.txt");
//		Article a = new Article();
//		articleParser.parse(a, text);
//		System.out.println(a);
//	}
//
//	@Test
//	public void testLists2() throws IOException {
//
//		String text = readFileAsString("/list2.txt");
//		Article a = new Article();
//		articleParser.parse(a, text);
//		System.out.println(a);
//	}
	
//	@Test
//	public void testMercedes() throws IOException {
//
//		String text = readFileAsString("/mercedes.txt");
//		Article a = new Article();
//		 articleParser = new ArticleParser(Language.EN);
//		articleParser.parse(a, text);
//		System.out.println(a);
//	}
//	
	@Test
	public void testRedirect() throws IOException {

		String text = readFileAsString("/redirect.txt");
		Article a = new Article();
		articleParser = new ArticleParser(Language.EN);
		articleParser.parse(a, text);
		assertTrue(a.isRedirect());
		System.out.println(a.getRedirect());
	}

	private String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(this
				.getClass().getResourceAsStream(filePath), "UTF-8"));
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

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

import it.isti.cnr.hpc.wikipedia.article.Article;
import it.isti.cnr.hpc.wikipedia.article.Language;
import it.isti.cnr.hpc.wikipedia.parser.ArticleParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 * ArticleTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 19/nov/2011
 */
public class EnArticleTest {
	
	ArticleParser articleParser = new ArticleParser(Language.EN);
	
	@Test
	public void test() throws IOException {

		String text = readFileAsString("/en-article.txt");
		
		Article a = new Article();
		articleParser.parse(a, text);		
		System.out.println(a);
		
		ArticleSummarizer summarizer = new ArticleSummarizer();
		System.out.println("*********\n"+summarizer.getSummary(a));
	}
	
	
//	@Test
//	public void testJson() throws IOException {
//		MediaWikiParserFactory factory = new MediaWikiParserFactory();
//		MediaWikiParser parser = factory.createParser();
//		String text = readFileAsString("/article.txt");
//		Article a = Article.fromMediaWiki(text);
//		System.out.print("JSON = ");
//		String json = a.toJson();
//		System.out.println(json);
//		Article b = Article.fromJson(json);
//		assertEquals(a,b);
//	}
//	
//	@Test
//	public void testSandbox() throws IOException {
//		MediaWikiParserFactory factory = new MediaWikiParserFactory();
//		MediaWikiParser parser = factory.createParser();
//		String text = readFileAsString("/article-with-infobox.txt");
//		Article a = Article.fromMediaWiki(text);
//		
//		System.out.println(a);
//	}
//	
//	@Test
//	public void testTable() throws IOException {
//		MediaWikiParserFactory factory = new MediaWikiParserFactory();
//		MediaWikiParser parser = factory.createParser();
//		String text = readFileAsString("/table.txt");
//		Article a = Article.fromMediaWiki(text);
//		
//		System.out.println(a);
//		System.out.println(a.getTables().get(0).getColumn(1));
//	}
//	
//	@Test
//	public void testTable2() throws IOException {
//		MediaWikiParserFactory factory = new MediaWikiParserFactory();
//		MediaWikiParser parser = factory.createParser();
//		String text = readFileAsString("/table2.txt");
//		Article a = Article.fromMediaWiki(text);
//		
//		System.out.println(a);
//	}
//	
//	@Test
//	public void testLists() throws IOException {
//		MediaWikiParserFactory factory = new MediaWikiParserFactory();
//		MediaWikiParser parser = factory.createParser();
//		String text = readFileAsString("/list.txt");
//		Article a = Article.fromMediaWiki(text);
//		
//		System.out.println(a);
//	}
//	
//	@Test
//	public void testLists2() throws IOException {
//		MediaWikiParserFactory factory = new MediaWikiParserFactory();
//		MediaWikiParser parser = factory.createParser();
//		String text = readFileAsString("/list2.txt");
//		Article a = Article.fromMediaWiki(text);
//		
//		System.out.println(a);
//	}
//	
//	
    private String readFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(this
            .getClass().getResourceAsStream(filePath),"UTF-8"));
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

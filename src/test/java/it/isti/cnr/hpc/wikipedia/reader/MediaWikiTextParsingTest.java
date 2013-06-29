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

import it.isti.cnr.hpc.wikipedia.article.Article;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * MediaWikiTextParsingTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 19/nov/2011
 */
public class MediaWikiTextParsingTest {

	@Ignore
	@Test
	public void test() throws IOException {
		MediaWikiParserFactory factory = new MediaWikiParserFactory();
		MediaWikiParser parser = factory.createParser();
		String text = readFileAsString("/article.txt");
		ParsedPage article = parser.parse(text);
		System.out.println(article.getText());
		System.out.println(article.getFirstParagraph());
	}
	
	@Ignore
	@Test
	public void testInfobox() throws IOException {
		MediaWikiParserFactory factory = new MediaWikiParserFactory();
		MediaWikiParser parser = factory.createParser();
		String text = readFileAsString("/article-with-infobox.txt");
		ParsedPage article = parser.parse(text);
		System.out.println(article.getText());
		System.out.println(article.getFirstParagraph());
	}
	
	@Test
	public void testMercedes() throws IOException {
		MediaWikiParserFactory factory = new MediaWikiParserFactory();
		MediaWikiParser parser = factory.createParser();
		String text = readFileAsString("/mercedes.txt");
		ParsedPage article = parser.parse(text);
		System.out.println(article.getText());
		System.out.println(article.getFirstParagraph());
		Article a = new Article();
		
	}
	
	
	
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

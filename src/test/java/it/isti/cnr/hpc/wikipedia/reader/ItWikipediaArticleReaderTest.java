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
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.isti.cnr.hpc.wikipedia.article.Article;
import it.isti.cnr.hpc.wikipedia.article.Language;
import it.isti.cnr.hpc.wikipedia.article.Link;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Iterator;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * WikipediaArticleReaderTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 18/nov/2011
 */
public class ItWikipediaArticleReaderTest {

//	@Test
//	public void test() throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
//		URL u = this.getClass().getResource("/it/xml-dump/dump.xml");
//		WikipediaArticleReader wap = new WikipediaArticleReader(u.getFile(),"/tmp/dump.json.gz", Language.IT);
//		wap.start();
//		RecordReader<Article> reader = new RecordReader<Article>("/tmp/dump.json.gz", new JsonRecordParser<Article>(Article.class));
//		Iterator<Article> iterator = reader.iterator();
//		Article a = iterator.next();
//		assertEquals(34, a.getLinks().size());
//		assertEquals(1, a.getCategories().size());
//	}
	
//	@Test
//	public void bigTest() throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
//		URL u = this.getClass().getResource("/it/xml-dump/test.xml.gz");
//		WikipediaArticleReader wap = new WikipediaArticleReader(u.getFile(),"/tmp/dump.json.gz", Language.IT);
//		wap.start();
//		RecordReader<Article> reader = new RecordReader<Article>("/tmp/dump.json.gz", new JsonRecordParser<Article>(Article.class));
//		for (Article a : reader){
//			StringBuilder sb = new StringBuilder();
//			for (Link l : a.getCategories()){
//				sb.append(l.getId()).append(" ");
//			}
//			System.out.println(a.getTitle()+"\t"+a.getType()+"\t"+sb.toString());
//		}
//	}
	
	@Test
	public void categories() throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
		URL u = this.getClass().getResource("/it/xml-dump/categories.xml.gz");
		WikipediaArticleReader wap = new WikipediaArticleReader(u.getFile(),"/tmp/dump.json.gz", Language.IT);
		wap.start();
		RecordReader<Article> reader = new RecordReader<Article>("/tmp/dump.json.gz", new JsonRecordParser<Article>(Article.class));
		for (Article a : reader){
			StringBuilder sb = new StringBuilder();
			for (Link l : a.getCategories()){
				sb.append(l.getId()).append(" ");
			}
			System.out.println(a.getTitle()+"\t"+a.getType()+"\t"+sb.toString());
		}
	}

}

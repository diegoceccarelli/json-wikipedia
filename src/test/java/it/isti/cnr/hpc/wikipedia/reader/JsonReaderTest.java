/**
 *  Copyright 2012 Diego Ceccarelli
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

import static org.junit.Assert.assertEquals;
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.isti.cnr.hpc.wikipedia.article.Article;
import it.isti.cnr.hpc.wikipedia.article.Article.Type;
import it.isti.cnr.hpc.wikipedia.reader.filter.RedirectFilter;
import it.isti.cnr.hpc.wikipedia.reader.filter.TypeFilter;

import org.junit.Ignore;
import org.junit.Test;
//import it.isti.cnr.hpc.wikipedia.reader.filter.DisambiguationFilter;

/**
 * JsonReaderTest.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class JsonReaderTest {

	@Ignore
	@Test
	public void test() {
		int i = 0;
		RecordReader<Article> reader = new RecordReader<Article>(
				"./src/test/resources/enwiki-top500-pages-articles.json.gz",
				new JsonRecordParser<Article>(Article.class));
		for (Article a : reader) {
			System.out.println(i + "\t type:" + a.getType() + " \t "
					+ a.getTitle());
			i++;
		}
		assertEquals(500, i);
	}

	@Ignore
	@Test
	
	public void testFilter() {
		int i = 0;
		RecordReader<Article> reader = new RecordReader<Article>(
				"./src/test/resources/enwiki-top500-pages-articles.json.gz",
				new JsonRecordParser<Article>(Article.class))
				.filter(TypeFilter.MAIN);
		for (Article a : reader) {
			System.out.println(i + "\t type:" + a.getType() + " \t "
					+ a.getTitle());
			i++;
		}
		assertEquals(499, i);
	}

	@Ignore
	@Test
	public void testFilter2() {
		int i = 0;
		RecordReader<Article> reader = new RecordReader<Article>(
				"./src/test/resources/enwiki-top500-pages-articles.json.gz",
				new JsonRecordParser<Article>(Article.class))
				.filter(new TypeFilter(Type.ARTICLE));
		for (Article a : reader) {
			System.out.println(i + "\t type:" + a.getType() + " \t "
					+ a.getTitle());
			i++;
		}
		assertEquals(1, i);
	}

	@Ignore
	@Test
	public void testRedirect() {
		int i = 0;
		RecordReader<Article> reader = new RecordReader<Article>(
				"./src/test/resources/enwiki-top500-pages-articles.json.gz",
				new JsonRecordParser<Article>(Article.class))
				.filter(RedirectFilter.KEEP_REDIRECTS);
		for (Article a : reader) {
			System.out.println(i + "\t " + a.getTitle() + "\t"
					+ a.getRedirect());
			i++;
		}
	}


}

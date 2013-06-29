/**
 *  Copyright 2013 Diego Ceccarelli
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

import java.io.BufferedWriter;
import java.io.IOException;

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.isti.cnr.hpc.wikipedia.article.Article.Type;
import it.isti.cnr.hpc.wikipedia.reader.filter.TypeFilter;

import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Feb 20, 2013
 */
public class ArticleSummarizerTest {
	

	@Test
	public void removeThumbs() {
		ArticleSummarizer summarizer = new ArticleSummarizer();
		String cleaned = summarizer.removeThumbs("thumb|Leaves ");
		assertEquals("Leaves ",cleaned);
		
	}
	
	@Test
	public void removeParanthesis() {
		ArticleSummarizer summarizer = new ArticleSummarizer();
		String cleaned = summarizer.removeParanthesis("asd (asdasda)");
		assertEquals("asd ",cleaned);
		
	}
	@Test
	public void removeParanthesis2() {
		ArticleSummarizer summarizer = new ArticleSummarizer();
		String cleaned = summarizer.removeParanthesis("asd [asdasda]");
		assertEquals("asd ",cleaned);
		
	}
	

	@Test
	public void test() throws IOException {
		RecordReader<Article> reader = new RecordReader<Article>("./src/test/resources/en-wikipedia-articles-1000-2.json.gz", new JsonRecordParser<Article>(Article.class));
		reader.filter(new TypeFilter(Type.ARTICLE));
		ArticleSummarizer summarizer = new ArticleSummarizer();
		BufferedWriter writer = IOUtils.getPlainOrCompressedUTF8Writer("/tmp/test");
		for (Article a : reader ){
			String text = a.getText();
			if (text.length() > 1000) {
				text = text.substring(0, 1000);
			}
			writer.write(a.getTypeName());
			writer.newLine();
			writer.write(a.getTitle());
			writer.newLine();
			writer.newLine();
			writer.write("** "+text);
			writer.newLine();
			writer.newLine();

			writer.write("> "+summarizer.getSummary(a));
			writer.newLine();
			writer.newLine();
			writer.write("> "+a.getCategories());
		}
		
		writer.close();
		
		
	}

}

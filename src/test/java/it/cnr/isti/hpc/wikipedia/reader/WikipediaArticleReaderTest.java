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
import it.cnr.isti.hpc.wikipedia.article.AvroArticle;
import it.cnr.isti.hpc.wikipedia.article.ArticleHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import it.cnr.isti.hpc.wikipedia.article.Language;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
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
		final File file = File.createTempFile("jsonwikipedia-mercedes", ".json.gz");
		WikipediaArticleReader wap = new WikipediaArticleReader(u.getFile(), file.getAbsolutePath(), "en");
		wap.start();
		String json = IOUtils.getFileAsUTF8String(file.getAbsolutePath());
		AvroArticle a = ArticleHelper.fromJson(json);
		assertTrue(ArticleHelper.cleanText(a.getParagraphs()).startsWith("Mercedes-Benz"));
		assertEquals(15, a.getCategories().size());
	}

	@Test
	public void testAvroParsing() throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
		URL u = this.getClass().getResource("/en/mercedes.xml");
		final File file = File.createTempFile("jsonwikipedia-mercedes", ".avro");
		WikipediaArticleReader wap = new WikipediaArticleReader(u.getFile(),file.getAbsolutePath(), "en");
		wap.start();


		DatumReader<AvroArticle> datumReader = new GenericDatumReader<>();
		DataFileReader<AvroArticle> dataFileReader = new DataFileReader<AvroArticle>(file, datumReader);
		while (dataFileReader.hasNext()){
			System.out.println(dataFileReader.next());
		}
	}
}

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
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.ArticleHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.junit.Test;
import org.xml.sax.SAXException;

public class WikipediaArticleReaderTest {

	@Test
	public void testParsing() throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
		File input = new File("src/test/resources/en/mercedes.xml");
		final File file = File.createTempFile("jsonwikipedia-mercedes", ".json.gz");
		WikipediaArticleReader wap = new WikipediaArticleReader(input, file, "en");
		wap.start();
		String json = IOUtils.getFileAsUTF8String(file.getAbsolutePath());
		Article a = ArticleHelper.fromJson(json);
		assertTrue(ArticleHelper.cleanText(a.getParagraphs()).startsWith("Mercedes-Benz"));
		assertEquals(15, a.getCategories().size());
	}

	@Test
	public void testAvroParsing() throws IOException, SAXException {
		File input = new File("src/test/resources/en/mercedes.xml");
		final File output = File.createTempFile("jsonwikipedia-mercedes", ".avro");
		output.deleteOnExit();

		WikipediaArticleReader wap = new WikipediaArticleReader(input, output, "en");
		wap.start();

		// reading the encoded avro and checking that it is correct
		DatumReader<Article> userDatumReader = new SpecificDatumReader<>(Article.getClassSchema());
		DataFileReader<Article> dataFileReader = new DataFileReader<>(output, userDatumReader);
		assertTrue(dataFileReader.hasNext());
		Article article = new Article();
		dataFileReader.next(article);
		assertEquals("Mercedes-Benz", article.getTitle());
		assertEquals("Mercedes-Benz", article.getWikiTitle());
	}


	@Test
	public void testAvroAndJsonProduceSameObject() throws IOException, SAXException {
		// get json article
		File input = new File("src/test/resources/en/mercedes.xml");
		final File jsonOuput = File.createTempFile("jsonwikipedia-mercedes", ".json.gz");
		jsonOuput.deleteOnExit();
		WikipediaArticleReader wap = new WikipediaArticleReader(input, jsonOuput, "en");
		wap.start();
		String json = IOUtils.getFileAsUTF8String(jsonOuput.getAbsolutePath());
		Article jsonArticle  = ArticleHelper.fromJson(json);
		// get avro article


		final File avroOutput = File.createTempFile("jsonwikipedia-mercedes", ".avro");
		avroOutput.deleteOnExit();

		WikipediaArticleReader avroParser = new WikipediaArticleReader(input, avroOutput, "en");
		avroParser.start();

		// reading the encoded avro and checking that it is correct
		DatumReader<Article> userDatumReader = new SpecificDatumReader<>(Article.getClassSchema());
		DataFileReader<Article> dataFileReader = new DataFileReader<>(avroOutput, userDatumReader);
		assertTrue(dataFileReader.hasNext());
		Article avroArticle = new Article();
		dataFileReader.next(avroArticle);

		assertEquals(avroArticle, jsonArticle);

	}


}

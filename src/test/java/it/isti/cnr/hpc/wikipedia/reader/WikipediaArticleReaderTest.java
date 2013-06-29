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

import it.isti.cnr.hpc.wikipedia.article.Language;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

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
	public void test() throws UnsupportedEncodingException, FileNotFoundException, IOException, SAXException {
		URL u = this.getClass().getResource("/mercedes.xml");
		WikipediaArticleReader wap = new WikipediaArticleReader(u.getFile(),"/tmp/mercedes.json.gz", Language.EN);
		wap.start();
		
	}

}

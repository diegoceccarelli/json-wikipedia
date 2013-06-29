//package it.isti.cnr.hpc.wikipedia.lucene;
//
///**
// *  Copyright 2012 Salvatore Trani
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import it.cnr.isti.hpc.io.reader.BaseItemReader;
//import it.cnr.isti.hpc.io.reader.ItemReader;
//import it.isti.cnr.hpc.wikipedia.domain.Article;
//import it.isti.cnr.hpc.wikipedia.domain.Link;
//import it.isti.cnr.hpc.wikipedia.reader.filter.RedirectFilter;
//import it.isti.cnr.hpc.wikipedia.reader.filter.TypeFilter;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.net.URL;
//import java.util.List;
//
//import org.apache.lucene.document.Document;
//import org.junit.Test;
//
//public class ArticleIndexerTest {
//	/**
//	 * Logger for this class
//	 */
//	private static final Logger logger = LoggerFactory
//			.getLogger(ArticleIndexerTest.class);
//
//	@Test
//	public void testIndexing() {
//		// Clean the index
//		LuceneHelper indexer = new LuceneHelper("/tmp/lucene-index/");
//		try {
//			BufferedWriter w = new BufferedWriter(new FileWriter(""));
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//		indexer.clearIndex();
//
//		URL u = this.getClass().getResource(
//				"/enwiki-top100-pages-articles.json.gz");
//
//		ItemReader<Article> reader = 
//				new BaseItemReader<Article>(u.getFile(), new Article());
//		// TODO: gestire redirect...
//		reader.filter(new TypeFilter("M"), new RedirectFilter(false));
//		for (Article a : reader) {
//			logger.info("Adding document {} ", a.getTitle());
//			indexer.addDocument(a);
//		}
//		indexer.commit();
//
//		List<Integer> res = indexer.query("Ascii");
//		for (int i = 0; i < res.size(); i++) {
//			int docId = res.get(i);
//			Document d = indexer.getDoc(docId);
//			System.out.println((i + 1) + ". " + d.get("id") + " "
//					+ d.get("title"));
//		}
//	}
//}

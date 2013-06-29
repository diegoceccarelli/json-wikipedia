package it.isti.cnr.hpc.wikipedia.lucene;
///**
// *  Copyright 2011 Diego Ceccarelli
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
//package it.isti.cnr.hpc.wikipedia.solr;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import static org.junit.Assert.*;
//import it.isti.cnr.hpc.wikipedia.domain.Article;
//import it.isti.cnr.hpc.wikipedia.util.JsonReader;
//
//import java.io.IOException;
//import java.net.URL;
//
//import org.apache.solr.client.solrj.SolrQuery;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.response.QueryResponse;
//import org.apache.solr.common.SolrDocument;
//import org.junit.Test;
//
///**
// * ArticleIndexerTest.java
// * 
// * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 23/nov/2011
// */
//public class SearcherTest  {
//	/**
//	 * Logger for this class
//	 */
//	private static final Logger logger = LoggerFactory.getLogger(SearcherTest.class);
//
//
//
//	@Test
//    public void testSearcher() throws SolrServerException, IOException {
//		Searcher s = new Searcher();
//		logger.info("performing query *:*");
//		for (Article a : s.getResults("*:*", 10)){
//			logger.info("title: {}",a.getTitle());
//			System.out.println(a);
//		}
//    }
//
//}

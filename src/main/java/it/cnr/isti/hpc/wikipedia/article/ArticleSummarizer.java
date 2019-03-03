///**
// *  Copyright 2013 Diego Ceccarelli
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
//package it.cnr.isti.hpc.wikipedia.article;
//
//
///**
// * Given an article returns a string summarizing
// * (cleaning and enriching its content) using for displaying the
// * entity.
// *
// * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
// *
// * Created on Feb 20, 2013
// */
//public class ArticleSummarizer {
//
//	private int maxLength;
//	private int minLength;
//
//	public ArticleSummarizer(){
//		this(500);
//	}
//
//	public ArticleSummarizer(int maxLength){
//		this.maxLength = maxLength;
//		minLength = maxLength -100;
//	}
//
//
//
//
//
//	private final static String TRIM_CHARS="[ ,.:-_;^]+$";
//
//	public String getSummary(Article article){
//		StringBuilder sb = new StringBuilder(maxLength);
//		for (String paragraph : article.getParagraphs()){
//			paragraph = cleanWikiText(paragraph);
//			sb.append(paragraph);
//			if (!paragraph.trim().isEmpty())
//				sb.append(" ");
//			if (sb.length() > maxLength) {
//				break;
//			}
//		}
//		int pos = sb.lastIndexOf("[a-z0-9]. ");
//		if (pos > minLength)
//			sb.setLength(pos);
//
//
//
//		return sb.toString().replaceAll(TRIM_CHARS, ".");
//	}
//
//	public String cleanWikiText(String text){
//		text = removeTemplates(text);
//		//text = removeThumbs(text);
//		text = removeParanthesis(text);
//		text = removingUrls(text);
//		text = doubleSpaces(text);
//		text = text.trim();
//		return text;
//	}
//
//	private static String THUMB_PATTERN = "[^ ]+[|]";
//
//	protected String removeThumbs(String text){
//		return text.replaceAll(THUMB_PATTERN, "");
//	}
//
//	private static String URL_PATTERN = "https?://[^ ]+";
//	private static String URL2_PATTERN = "www://[^ ]+";
//
//	protected String removingUrls(String text){
//		text = text.replaceAll(URL_PATTERN, "");
//		return text.replaceAll(URL2_PATTERN, "");
//	}
//
//	protected String doubleSpaces(String text){
//		return text.replaceAll("  +", " ");
//	}
//
//	private static String PAR1_PATTERN = "\\[[^]]*\\]";
//	private static String PAR2_PATTERN = "[(][^)]*[)]";
//
//
//	protected String removeParanthesis(String text){
//		text = text.replaceAll(PAR1_PATTERN, "");
//		return text.replaceAll(PAR2_PATTERN, "");
//	}
//
//
//
//
//	//private static String TEMPLATE_PATTERN = "TEMPLATE\\[[^]]*\\]";
//	private String removeTemplates(String text) {
//		//return text.replaceAll(TEMPLATE_PATTERN, "");
//
//
//		// dirty code, i'm sure there is a better way to exclude
//		// template code from getText() (setting ignores..)
//		while (text.contains("TEMPLATE[")) {
//			int pos = text.indexOf("TEMPLATE[");
//			int start = pos + "TEMPLATE".length() + 1;
//			int end = start;
//			int c = 1;
//			while (c > 0) {
//				if (end >= text.length())
//					break;
//				if (text.charAt(end) == '[')
//					c++;
//				if (text.charAt(end) == ']')
//					c--;
//				end++;
//			}
//			text = text.substring(0, pos) + text.substring(end);
//		}
//		return text;
//
//	}
//
//
//}

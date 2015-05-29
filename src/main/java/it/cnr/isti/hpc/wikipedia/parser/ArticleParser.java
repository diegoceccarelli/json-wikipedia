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
package it.cnr.isti.hpc.wikipedia.parser;

import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Article.Type;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.article.Table;
import it.cnr.isti.hpc.wikipedia.article.Template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.wikipedia.parser.Content;
import de.tudarmstadt.ukp.wikipedia.parser.ContentElement;
import de.tudarmstadt.ukp.wikipedia.parser.DefinitionList;
import de.tudarmstadt.ukp.wikipedia.parser.NestedList;
import de.tudarmstadt.ukp.wikipedia.parser.NestedListContainer;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.Span;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;

/**
 * Generates a Mediawiki parser given a language, (it will expect to find a
 * locale file in <tt>src/main/resources/</tt>).
 *
 * @see Locale
 *
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 *         Created on Feb 14, 2013
 */
public class ArticleParser {

	static MediaWikiParserFactory parserFactory = new MediaWikiParserFactory();

	private static final Logger logger = LoggerFactory
			.getLogger(ArticleParser.class);

	/** the language (used for the locale) default is English **/
	private String lang = Language.EN;

	static int shortDescriptionLength = 500;
	private List<String> redirects;

	private MediaWikiParser parser;
	private Locale locale;

	public ArticleParser(String lang) {
		this.lang = lang;
		parser = parserFactory.getParser(lang);
		locale = new Locale(lang);
		redirects = locale.getRedirectIdentifiers();

	}

	public ArticleParser() {
		parser = parserFactory.getParser(lang);
		locale = new Locale(lang);
		redirects = locale.getRedirectIdentifiers();

	}

	public void parse(Article article, String mediawiki) {
		ParsedPage page = parser.parse(mediawiki);
		setRedirect(article, mediawiki);

		parse(article, page);

	}

	private void parse(Article article, ParsedPage page) {
		article.setLang(lang);
		setWikiTitle(article);
		if (page == null) {
			logger.warn("page is null for article {}", article.getTitle());
		} else {
			setParagraphs(article, page);
			// setShortDescription(article);
			setTemplates(article, page);
			setLinks(article, page);
			setCategories(article, page);
			setHighlights(article, page);
			setSections(article, page);
			setTables(article, page);
			setEnWikiTitle(article, page);
			setLists(article, page);
		}
		setRedirect(article);
		setDisambiguation(article);
		setIsList(article);
	}

	// /**
	// * @param article
	// */
	// private void setShortDescription(Article article) {
	// StringBuilder sb = new StringBuilder();
	// for (String paragraph : article.getParagraphs()) {
	// paragraph = removeTemplates(paragraph);
	// sb.append(paragraph);
	// if (sb.length() > shortDescriptionLength) {
	// break;
	// }
	// }
	// if (sb.length() > shortDescriptionLength) {
	// sb.setLength(shortDescriptionLength);
	// int pos = sb.lastIndexOf(" ");
	// sb.setLength(pos);
	// }
	// article.setShortDescription(sb.toString());
	//
	// }

	// private final static String templatePattern = "TEMPLATE\\[[^]]+\\]";
	//
	// private static String removeTemplates(String paragraph) {
	// paragraph = paragraph.replaceAll(templatePattern, " ");
	//
	// return paragraph;
	// }

	/**
	 * @param article
	 */
	private void setWikiTitle(Article article) {
		article.setWikiTitle(Article.getTitleInWikistyle(article.getTitle()));

	}

	/**
	 * @param article
	 */
	private void setIsList(Article article) {
		for (String list : locale.getListIdentifiers()) {
			if (StringUtils.startsWithIgnoreCase(article.getTitle(), list)) {
				article.setType(Type.LIST);
			}
		}

	}

	private void setRedirect(Article article) {
		if (!article.getRedirect().isEmpty())
			return;
		List<List<String>> lists = article.getLists();
		if ((!lists.isEmpty()) && (! lists.get(0).isEmpty())) {
			// checking only first item in first list
			String line = lists.get(0).get(0);

			for (String redirect : redirects) {
				if (StringUtils.startsWithIgnoreCase(line, redirect)) {
					int pos = line.indexOf(' ');
					if (pos < 0)
						return;
					String red = line.substring(pos).trim();
					red = Article.getTitleInWikistyle(red);
					article.setRedirect(red);
					article.setType(Type.REDIRECT);
					return;

				}
			}
		}
	}

	// for (List<String> lists : article.getLists()) {
	// for (String line : lists) {
	// for (String redirect : redirects) {
	// if (StringUtils.startsWithIgnoreCase(line, redirect)) {
	// int pos = line.indexOf(' ');
	// if (pos < 0)
	// return;
	// String red = line.substring(pos).trim();
	// red = Article.getTitleInWikistyle(red);
	// article.setRedirect(red);
	// article.setType(Type.REDIRECT);
	// return;
	//
	// }
	// }
	// }
	// }

	/**
	 * @param article
	 * @param page
	 */
	private void setRedirect(Article article, String mediawiki) {
		for (String redirect : redirects)
			if (StringUtils.startsWithIgnoreCase(mediawiki, redirect)) {
				int start = mediawiki.indexOf("[[") + 2;
				int end = mediawiki.indexOf("]]");
				if (start < 0 || end < 0) {
					logger.warn("cannot find the redirect {}\n mediawiki: {}",
							article.getTitle(), mediawiki);
					continue;
				}
				String r = Article.getTitleInWikistyle(mediawiki.substring(
						start, end));
				article.setRedirect(r);
				article.setType(Type.REDIRECT);
			}

	}

	/**
	 * @param page
	 */
	private void setTables(Article article, ParsedPage page) {
		List<Table> tables = new ArrayList<Table>();

		for (de.tudarmstadt.ukp.wikipedia.parser.Table t : page.getTables()) {
			// System.out.println(t);

			int i = 0;
			String title = "";
			if (t.getTitleElement() != null) {
				title = t.getTitleElement().getText();
				if (title == null)
					title = "";
			}
			Table table = new Table(title);
			List<String> currentRow = new ArrayList<String>();
			List<Content> contentList = t.getContentList();
			for (@SuppressWarnings("unused")
			Content c : contentList) {

				int row, col;
				String elem = "";

				try {

					col = t.getTableElement(i).getCol();
					row = t.getTableElement(i).getRow();
					elem = t.getTableElement(i).getText();

				} catch (IndexOutOfBoundsException e) {
					// logger.(
					// "Error creating table {}, Index out of bound - content = {}",
					// table.getName(), c.getText());
					break;

				}
				if (row > 0 && col == 0) {
					if ((currentRow.size() == 1)
							&& (currentRow.get(0).equals(table.getName()))) {
						currentRow = new ArrayList<String>();
					} else {
						if (!currentRow.isEmpty())
							table.addRow(currentRow);
						currentRow = new ArrayList<String>();
					}

				}
				currentRow.add(elem);
				i++;
			}
			table.addRow(currentRow);
			tables.add(table);
		}

		article.setTables(tables);

	}

	protected void setEnWikiTitle(Article article, ParsedPage page) {
		if (article.isLang(Language.EN)) {
			return;
		}
		try {
			if (page.getLanguages() == null) {
				article.setEnWikiTitle("");
				return;
			}
		} catch (NullPointerException e) {
			// FIXME title is always null!
			logger.warn("no languages for page {} ", article.getTitle());
			return;
		}
		for (de.tudarmstadt.ukp.wikipedia.parser.Link l : page.getLanguages())
			if (l.getText().startsWith("en:")) {
				article.setEnWikiTitle(l.getTarget().substring(3));
				break;
			}

	}

	/**
	 * @param page
	 */
	private void setSections(Article article, ParsedPage page) {
		List<String> sections = new ArrayList<String>(10);
		for (Section s : page.getSections()) {

			if (s == null || s.getTitle() == null)
				continue;
			sections.add(s.getTitle());
		}
		article.setSections(sections);

	}

	private void setLinks(Article article, ParsedPage page) {

		List<Link> links = new ArrayList<Link>(10);
		List<Link> elinks = new ArrayList<Link>(10);

		for (de.tudarmstadt.ukp.wikipedia.parser.Link t : page.getLinks()) {
			if (t.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL) {

				links.add(new Link(t.getTarget(), t.getText(), t.getPos().getStart(), t.getPos().getEnd()));

			}
			if (t.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.EXTERNAL) {

				elinks.add(new Link(t.getTarget(), t.getText(),t.getPos().getStart(), t.getPos().getEnd()));

			}
		}
		article.setLinks(links);
		article.setExternalLinks(elinks);
	}

	private void setTemplates(Article article, ParsedPage page) {
		List<Template> templates = new ArrayList<Template>(10);

		for (de.tudarmstadt.ukp.wikipedia.parser.Template t : page
				.getTemplates()) {
			List<String> templateParameters = t.getParameters();
			parseTemplatesSchema(article, templateParameters);

			if (t.getName().toLowerCase().startsWith("infobox")) {
				article.setInfobox(new Template(t.getName(), templateParameters));
			} else {
				templates.add(new Template(t.getName(), templateParameters));
			}
		}
		article.setTemplates(templates);

	}

	/**
	 *
	 * @param templateParameters
	 */
	private void parseTemplatesSchema(Article article,
			List<String> templateParameters) {
		List<String> schema = new ArrayList<String>(10);

		for (String s : templateParameters) {
			try {
				if (s.contains("=")) {
					String attributeName = s.split("=")[0].trim().toLowerCase();
					schema.add(attributeName);
				}

			} catch (Exception e) {
				continue;
			}
		}
		article.addTemplatesSchema(schema);

	}

	private void setCategories(Article article, ParsedPage page) {
		ArrayList<Link> categories = new ArrayList<Link>(10);

		for (de.tudarmstadt.ukp.wikipedia.parser.Link c : page.getCategories()) {

			categories.add(new Link(c.getTarget(), c.getText(), c.getPos().getStart(), c.getPos().getEnd()));
		}
		article.setCategories(categories);

	}

	private void setHighlights(Article article, ParsedPage page) {
		List<String> highlights = new ArrayList<String>(20);

		for (Paragraph p : page.getParagraphs()) {
			for (Span t : p.getFormatSpans(Content.FormatType.BOLD)) {
				highlights.add(t.getText(p.getText()));
			}
			for (Span t : p.getFormatSpans(Content.FormatType.ITALIC)) {
				highlights.add(t.getText(p.getText()));
			}

		}
		article.setHighlights(highlights);

	}

	private void setParagraphs(Article article, ParsedPage page) {
		List<String> paragraphs = new ArrayList<String>(page.nrOfParagraphs());
		Map<String, List<Link>> paraLinks 
					= new LinkedHashMap<String, List<Link>>();
		for (Paragraph p : page.getParagraphs()) {
			String text = p.getText();
			List<Link> links = new ArrayList<Link>();
			// text = removeTemplates(text);
			text = text.replace("\n", " ").trim();
			if (!text.isEmpty()){
				paragraphs.add(text);
				for(de.tudarmstadt.ukp.wikipedia.parser.Link t: p.getLinks()){
					if (t.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL)
						links.add(new Link(t.getTarget(), t.getText(), t.getPos().getStart(), t.getPos().getEnd()));
				}
				paraLinks.put(text, links);
			}
		}
		article.setParagraphs(paragraphs);
		article.setParagraphsLink(paraLinks);
	}

	private void setLists(Article article, ParsedPage page) {
		List<List<String>> lists = new LinkedList<List<String>>();
		for (DefinitionList dl : page.getDefinitionLists()) {
			List<String> l = new ArrayList<String>();
			for (ContentElement c : dl.getDefinitions()) {
				l.add(c.getText());
			}
			lists.add(l);
		}
		for (NestedListContainer dl : page.getNestedLists()) {
			List<String> l = new ArrayList<String>();
			for (NestedList nl : dl.getNestedLists())
				l.add(nl.getText());
			lists.add(l);
		}
		article.setLists(lists);

	}

	private void setDisambiguation(Article a) {

		for (String disambiguation : locale.getDisambigutionIdentifiers()) {
			if (StringUtils.containsIgnoreCase(a.getTitle(), disambiguation)) {
				a.setType(Type.DISAMBIGUATION);
				return;
			}
			for (Template t : a.getTemplates()) {
				if (StringUtils.equalsIgnoreCase(t.getName(), disambiguation)) {
					a.setType(Type.DISAMBIGUATION);
					return;

				}
			}

		}
	}

}

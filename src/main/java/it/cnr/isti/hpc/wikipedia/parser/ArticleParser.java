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
import java.util.LinkedList;
import java.util.List;

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
	private final List<String> redirects;

	private final MediaWikiParser parser;
	private final Locale locale;

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
		final ParsedPage page = parser.parse(mediawiki);
		setRedirect(article, mediawiki);

		parse(article, page);

	}

	private void parse(Article article, ParsedPage page) {
		article.setLang(lang);
		setWikiTitle(article);
		if (page == null) {
			logger.warn("page is null for article {}", article.getTitle());
		} else {
			setLinks(article, page);
			setParagraphs(article, page);
			// setShortDescription(article);
			setTemplates(article, page);
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
		for (final String list : locale.getListIdentifiers()) {
			if (StringUtils.startsWithIgnoreCase(article.getTitle(), list)) {
				article.setType(Type.LIST);
			}
		}

	}

	private void setRedirect(Article article) {
		if (!article.getRedirect().isEmpty()) {
      return;
    }
		final List<List<String>> lists = article.getLists();
		if ((!lists.isEmpty()) && (! lists.get(0).isEmpty())) {
			// checking only first item in first list
			final String line = lists.get(0).get(0);

			for (final String redirect : redirects) {
				if (StringUtils.startsWithIgnoreCase(line, redirect)) {
					final int pos = line.indexOf(' ');
					if (pos < 0) {
            return;
          }
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
		for (final String redirect : redirects) {
      if (StringUtils.startsWithIgnoreCase(mediawiki, redirect)) {
				final int start = mediawiki.indexOf("[[") + 2;
				final int end = mediawiki.indexOf("]]");
				if ((start < 0) || (end < 0)) {
					logger.warn("cannot find the redirect {}\n mediawiki: {}",
							article.getTitle(), mediawiki);
					continue;
				}
				final String r = Article.getTitleInWikistyle(mediawiki.substring(
						start, end));
				article.setRedirect(r);
				article.setType(Type.REDIRECT);
			}
    }

	}

	/**
	 * @param page
	 */
	private void setTables(Article article, ParsedPage page) {
		final List<Table> tables = new ArrayList<>();

		final List<Link> links = new ArrayList<>();
		int tableId = 0;
		for (final de.tudarmstadt.ukp.wikipedia.parser.Table t : page.getTables()) {
			// System.out.println(t);

			int i = 0;
			String title = "";
			if (t.getTitleElement() != null) {
				title = t.getTitleElement().getText();
				if (title == null) {
          title = "";
        }
			}
			final Table table = new Table(title);
			List<String> currentRow = new ArrayList<>();
			final List<Content> contentList = t.getContentList();
			for (@SuppressWarnings("unused") final
			Content c : contentList) {

				int row, col;
				String elem = "";

				try {

					col = t.getTableElement(i).getCol();
					row = t.getTableElement(i).getRow();
					elem = t.getTableElement(i).getText();

				} catch (final IndexOutOfBoundsException e) {
					// logger.(
					// "Error creating table {}, Index out of bound - content = {}",
					// table.getName(), c.getText());
					break;

				}
				if ((row > 0) && (col == 0)) {
					if ((currentRow.size() == 1)
							&& (currentRow.get(0).equals(table.getName()))) {
						currentRow = new ArrayList<>();
					} else {
						if (!currentRow.isEmpty()) {
              table.addRow(currentRow);
            }
						currentRow = new ArrayList<>();
					}

				}
				
				for(final de.tudarmstadt.ukp.wikipedia.parser.Link l: t.getTableElement(i).getLinks()){
					if (l.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL){
						links.add(new Link(l.getTarget(), l.getText(), l.getPos().getStart(), l.getPos().getEnd(), Link.Type.TABLE, tableId, row, col));
					}
				}
				
				currentRow.add(elem);
				i++;
			}
			table.addRow(currentRow);
			tables.add(table);
			tableId++;
		}

		article.setTables(tables);
		updateLinks(article, links);
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
		} catch (final NullPointerException e) {
			// FIXME title is always null!
			logger.warn("no languages for page {} ", article.getTitle());
			return;
		}
		for (final de.tudarmstadt.ukp.wikipedia.parser.Link l : page.getLanguages()) {
      if (l.getText().startsWith("en:")) {
				article.setEnWikiTitle(l.getTarget().substring(3));
				break;
			}
    }

	}

	/**
	 * @param page
	 */
	private void setSections(Article article, ParsedPage page) {
		final List<String> sections = new ArrayList<String>(10);
		for (final Section s : page.getSections()) {

			if ((s == null) || (s.getTitle() == null)) {
        continue;
      }
			sections.add(s.getTitle());
		}
		article.setSections(sections);

	}

	private void setLinks(Article article, ParsedPage page) {

    final List<Link> links = new ArrayList<Link>(10);
    final List<Link> elinks = new ArrayList<Link>(10);

    for (final de.tudarmstadt.ukp.wikipedia.parser.Link t : page.getLinks()) {
      
      final Span linkSpan = t.getPos();
      if (t.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.IMAGE) {
        if (!t.getTarget().isEmpty()) {
          final Link image = new Link(t.getTarget(), t.getText(),
              linkSpan.getStart(), linkSpan.getEnd());
          image.setType(Link.Type.IMAGE);
          image.setParams(t.getParameters());
          links.add(image);
        }
      }
      if (t.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL) {
        if (!t.getTarget().isEmpty()) {
          links.add(new Link(t.getTarget(), t.getText(), linkSpan.getStart(),
              linkSpan.getEnd()));
        }
      }
      if (t.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.EXTERNAL) {
        if (!t.getTarget().isEmpty()) {
          elinks.add(new Link(t.getTarget(), t.getText(), linkSpan.getStart(),
              linkSpan.getEnd()));
        }
      }
    }
    article.setLinks(links);
    article.setExternalLinks(elinks);
  }

	private void setTemplates(Article article, ParsedPage page) {
		final List<Template> templates = new ArrayList<Template>(10);

		for (final de.tudarmstadt.ukp.wikipedia.parser.Template t : page
				.getTemplates()) {
			final List<String> templateParameters = t.getParameters();
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
		final List<String> schema = new ArrayList<String>(10);

		for (final String s : templateParameters) {
			try {
				if (s.contains("=")) {
					final String attributeName = s.split("=")[0].trim().toLowerCase();
					schema.add(attributeName);
				}

			} catch (final Exception e) {
				continue;
			}
		}
		article.addTemplatesSchema(schema);

	}

	private void setCategories(Article article, ParsedPage page) {
		final ArrayList<Link> categories = new ArrayList<Link>(10);

		for (final de.tudarmstadt.ukp.wikipedia.parser.Link c : page.getCategories()) {

			categories.add(new Link(c.getTarget(), c.getText(), c.getPos().getStart(), c.getPos().getEnd()));
		}
		article.setCategories(categories);

	}

	private void setHighlights(Article article, ParsedPage page) {
		final List<String> highlights = new ArrayList<String>(20);

		for (final Paragraph p : page.getParagraphs()) {
			for (final Span t : p.getFormatSpans(Content.FormatType.BOLD)) {
				highlights.add(t.getText(p.getText()));
			}
			for (final Span t : p.getFormatSpans(Content.FormatType.ITALIC)) {
				highlights.add(t.getText(p.getText()));
			}

		}
		article.setHighlights(highlights);

	}

	private void setParagraphs(Article article, ParsedPage page) {
		final List<String> paragraphs = new ArrayList<String>(page.nrOfParagraphs());
		final List<Link> links = new ArrayList<>();
		int paragraphId = 0;
		for (final Paragraph p : page.getParagraphs()) {
			String text = p.getText();
			// text = removeTemplates(text);
			text = text.replace("\n", " ").trim();
			if (!text.isEmpty()){
				paragraphs.add(text);
				for(final de.tudarmstadt.ukp.wikipedia.parser.Link t: p.getLinks()){
					if (t.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL){
						links.add(new Link(t.getTarget(), t.getText(), t.getPos().getStart(), t.getPos().getEnd(), Link.Type.BODY, paragraphId));
					}
				}
			}
			paragraphId++;
		}
		article.setParagraphs(paragraphs);
		updateLinks(article, links);

	}
	
	/**
	 * Updates the article's links with more specific ones, 
	 * i.e, containing type information, listId, etc. 
	 *
	 * @param  article  the article being parsed
	 * @param  links the more specific links
	 */
	private void updateLinks(Article article, List<Link> links) {
		final List<Link> articleLinks = article.getLinks();
		
		for(final Link l: links){
			if(articleLinks.contains(l)){
				int index = 0; 
				for(final Link a: articleLinks){
					if(a.equals(l) && (a.getType() == null)) {
						articleLinks.set(index, l);
						break;
					}
					index++;
				}
			}
		}
	}

	private void setLists(Article article, ParsedPage page) {
		final List<List<String>> lists = new LinkedList<>();
		for (final DefinitionList dl : page.getDefinitionLists()) {
			final List<String> l = new ArrayList<>();
			for (final ContentElement c : dl.getDefinitions()) {
				l.add(c.getText());
			}
			lists.add(l);
		}
		final List<Link> links = new ArrayList<>();
		int listId = 0;
		for (final NestedListContainer dl : page.getNestedLists()) {
			int itemId = 0;
			final List<String> l = new ArrayList<>();
			for (final NestedList nl : dl.getNestedLists()){
				l.add(nl.getText());
				for(final de.tudarmstadt.ukp.wikipedia.parser.Link t: nl.getLinks()){
					if (t.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL){
						links.add(new Link(t.getTarget(), t.getText(), t.getPos().getStart(), t.getPos().getEnd(), Link.Type.LIST, listId, itemId));
					}
				}
				itemId++;
			}
			lists.add(l);
			listId++;
		}
		article.setLists(lists);
		updateLinks(article, links);
	}

	private void setDisambiguation(Article a) {

		for (final String disambiguation : locale.getDisambigutionIdentifiers()) {
			if (StringUtils.containsIgnoreCase(a.getTitle(), disambiguation)) {
				a.setType(Type.DISAMBIGUATION);
				return;
			}
			for (final Template t : a.getTemplates()) {
				if (StringUtils.equalsIgnoreCase(t.getName(), disambiguation)) {
					a.setType(Type.DISAMBIGUATION);
					return;

				}
			}

		}
	}

}

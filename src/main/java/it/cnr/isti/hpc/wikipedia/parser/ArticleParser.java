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

import de.tudarmstadt.ukp.wikipedia.parser.ContentElement;
import de.tudarmstadt.ukp.wikipedia.parser.DefinitionList;
import de.tudarmstadt.ukp.wikipedia.parser.NestedList;
import de.tudarmstadt.ukp.wikipedia.parser.NestedListContainer;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Article.Type;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.article.Table;
import it.cnr.isti.hpc.wikipedia.article.Template;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.wikipedia.parser.Content;
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

	private void setLists(Article article, ParsedPage page) {
		List<List<String>> lists = new ArrayList<List<String>>();
		for (DefinitionList dl : page.getDefinitionLists()) {
			List<String> l = new ArrayList<String>();
			for (ContentElement c : dl.getDefinitions()) {
				l.add(c.getText());
			}
			lists.add(l);
		}
		for (NestedListContainer dl : page.getNestedLists()) {
			List<String> l = new ArrayList<String>();
			for (NestedList nl : dl.getNestedLists()) {
				l.add(nl.getText());
			}
			lists.add(l);
		}
		article.setLists(lists);
	}

	/**
	 * @param article
	 */
	private void setWikiTitle(Article article) {
		article.setWikiTitle(Article.getTitleInWikistyle(article.getTitle()));

	}

	private Link addLink(final List<Link> links, final List<Link> externalLinks, final de.tudarmstadt.ukp.wikipedia.parser.Link link, final Link.Type jsonWikipediaType){
		if (link.getTarget().isEmpty()){
			return null;
		}

		final Link jsonWikipediaLink;
		if (link.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL){
			jsonWikipediaLink = new Link(link.getTarget(), link.getText(), link.getPos().getStart(), link.getPos().getEnd(), jsonWikipediaType);
			links.add(jsonWikipediaLink);
		}
		else if (link.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.EXTERNAL){
			jsonWikipediaLink = new Link(link.getTarget(), link.getText(), link.getPos().getStart(), link.getPos().getEnd(), jsonWikipediaType);
			externalLinks.add(new Link(link.getTarget(), link.getText(), link.getPos().getStart(), link.getPos().getEnd(), jsonWikipediaType));
		}
		else if (link.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.IMAGE){
			jsonWikipediaLink = new Link(link.getTarget(), link.getText(), link.getPos().getStart(), link.getPos().getEnd(), Link.Type.IMAGE);
			links.add(jsonWikipediaLink);
		}
		else {
			jsonWikipediaLink = null;
		}
		return jsonWikipediaLink;


	}

	private void setLinksInParagraphs(final List<Link> links, final List<Link> externalLinks,  ParsedPage page){
		int paragraphId = 0;
		for (Paragraph p : page.getParagraphs()){
			for (de.tudarmstadt.ukp.wikipedia.parser.Link link : p.getLinks()){
				Link linkAdded = addLink(links, externalLinks, link, Link.Type.BODY);
				if (linkAdded != null){
					linkAdded.setParagraphId(paragraphId);
				}
			}
			paragraphId++;
		}
	}

	private void setLinksInTables(final List<Link> links, final List<Link> externalLinks,  ParsedPage page){
		int tableId = 0;
		for (de.tudarmstadt.ukp.wikipedia.parser.Table p : page.getTables()){

			for (int el = 0; el <  p.nrOfTableElements(); el++) {
				int col = p.getTableElement(el).getCol();
				int row = p.getTableElement(el).getRow();
				for (de.tudarmstadt.ukp.wikipedia.parser.Link link : p.getTableElement(el).getLinks()) {
					Link linkAdded = addLink(links, externalLinks, link, Link.Type.TABLE);
					if (linkAdded != null) {
						linkAdded.setTableId(tableId);
						linkAdded.setRowId(row);
						linkAdded.setColumnId(col);
					}
				}
			}
			tableId++;
		}
	}

	private void setLinksInLists(final List<Link> links, final List<Link> externalLinks,  ParsedPage page){
		int listId = 0;
		for (NestedListContainer p : page.getNestedLists()){
			int item = 0;
			for (NestedList list : p.getNestedLists()) {
				for (de.tudarmstadt.ukp.wikipedia.parser.Link link : list.getLinks()) {
					Link linkAdded = addLink(links, externalLinks, link, Link.Type.LIST);
					if (linkAdded != null) {
						linkAdded.setListId(listId);
						linkAdded.setListItem(item);
					}
				}
				item++;
			}
			listId++;
		}
	}



	private void setLinks(Article article, ParsedPage page){
        final List<Link> links = new ArrayList<Link>(page.getLinks().size());
        final List<Link> elinks = new ArrayList<Link>(page.getLinks().size());
		setLinksInParagraphs(links, elinks, page);
		setLinksInTables(links, elinks, page);
		setLinksInLists(links, elinks, page);
		article.setLinks(links);
		article.setExternalLinks(elinks);
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
        int tableId = 0;
        for (final de.tudarmstadt.ukp.wikipedia.parser.Table t : page.getTables()) {
            String title = "";
            if (t.getTitleElement() != null) {
                title = t.getTitleElement().getText();
                if (title == null) {
                    title = "";
                }
            }
            final Table table = new Table(title);
            List<String> currentRow = new ArrayList<>();
            for (int j = 0; j < t.nrOfTableElements(); j++) {

                int col = t.getTableElement(j).getCol();
                int row = t.getTableElement(j).getRow();
                final String elem = t.getTableElement(j).getText();

                if ((row > 0) && (col == 0)) {
                    if ((currentRow.size() == 1) && (currentRow.get(0).equals(table.getName()))) {
                        // first row, we want to create a list for currentRow
                        currentRow = new ArrayList<>();
                    } else {
                        if (!currentRow.isEmpty()) {
                            // otherwise, if there was a previous row we add it
                            table.addRow(currentRow);
                        }
                        // and then we create a new one
                        currentRow = new ArrayList<>();
                    }
                }
                currentRow.add(elem);
            }
            table.addRow(currentRow);
            tables.add(table);
            tableId++;
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

            categories.add(new Link(c.getTarget(), c.getText(), c.getPos().getStart(), c.getPos().getEnd(), Link.Type.CATEGORY));
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
        int paragraphId = 0;
        for (final Paragraph p : page.getParagraphs()) {
            String text = p.getText();
            // text = removeTemplates(text);
            text = text.replace("\n", " ").trim();
            if (!text.isEmpty()){
                paragraphs.add(text);
            }
            paragraphId++;
        }
        article.setParagraphs(paragraphs);
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

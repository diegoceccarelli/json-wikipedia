/**
 * Copyright 2013 Diego Ceccarelli
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.cnr.isti.hpc.wikipedia.parser;

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
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.ArticleHelper;
import it.cnr.isti.hpc.wikipedia.article.ArticleType;
import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.article.LinkType;
import it.cnr.isti.hpc.wikipedia.article.Table;
import it.cnr.isti.hpc.wikipedia.article.Template;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a Mediawiki parser given a language, (it will expect to find a locale file in
 * <tt>src/main/resources/</tt>).
 *
 * @see Locale
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *     <p>Created on Feb 14, 2013
 */
public class ArticleParser {

  static MediaWikiParserFactory parserFactory = new MediaWikiParserFactory();

  private static final Logger logger = LoggerFactory.getLogger(ArticleParser.class);

  /** the language (used for the locale) default is English * */
  private static final Language DEFAULT_LANGUAGE = Language.EN;

  private Language lang;

  static int shortDescriptionLength = 500;
  private final List<String> redirects;

  private final MediaWikiParser parser;
  private final Locale locale;

  public ArticleParser(String lang) {
    this.lang = Language.valueOf(lang.toUpperCase());
    parser = parserFactory.getParser(lang.toLowerCase());
    locale = new Locale(lang.toLowerCase());
    redirects = locale.getRedirectIdentifiers();
  }

  public ArticleParser(Language lang) {
    this(lang.toString().toUpperCase());
  }

  public void parse(Article.Builder article, String mediawiki) {
    final ParsedPage page = parser.parse(mediawiki);
    setRedirect(article, mediawiki);
    parse(article, page);
  }

  private void parse(Article.Builder article, ParsedPage page) {
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

  private void setLists(Article.Builder article, ParsedPage page) {
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

  private void setWikiTitle(Article.Builder article) {
    article.setWikiTitle(ArticleHelper.getTitleInWikistyle(article.getTitle()));
  }

  private void addLink(
      Link.Builder linkBuilder,
      de.tudarmstadt.ukp.wikipedia.parser.Link.type linkType,
      final List<Link> links,
      final List<Link> externalLinks) {
    if (linkType == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL) {
      links.add(linkBuilder.build());
    } else if (linkType == de.tudarmstadt.ukp.wikipedia.parser.Link.type.EXTERNAL) {
      externalLinks.add(linkBuilder.build());
    } else if (linkType == de.tudarmstadt.ukp.wikipedia.parser.Link.type.IMAGE) {
      links.add(linkBuilder.build());
    }
  }

  private Link.Builder createLink(
      final de.tudarmstadt.ukp.wikipedia.parser.Link link, final LinkType jsonWikipediaType) {
    if (link.getTarget().isEmpty()) {
      logger.warn("Empty link target for link {}", link);
      return null;
    }

    Link.Builder linkBuilder = Link.newBuilder();
    if (link.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.INTERNAL
        || link.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.EXTERNAL
        || link.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.IMAGE) {
      linkBuilder.setId(link.getTarget());
      linkBuilder.setAnchor(link.getText());
      linkBuilder.setStart(link.getPos().getStart());
      linkBuilder.setEnd(link.getPos().getEnd());
      linkBuilder.setType(jsonWikipediaType);
      if (link.getText().isEmpty()) {
        linkBuilder.setAnchor(ArticleHelper.wikiStyleToText(link.getTarget()));
        if (link.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.IMAGE) {
          List<String> parameters = link.getParameters();
          if (!parameters.isEmpty()) {
            linkBuilder.setAnchor(parameters.get(parameters.size() - 1));
          }
        }
      }
      if (link.getType() == de.tudarmstadt.ukp.wikipedia.parser.Link.type.IMAGE) {
        linkBuilder.setType(LinkType.IMAGE);
      }

      return linkBuilder;
    }
    logger.debug("No link for [{}] built: link type {} ", link.getText(), link.getType());
    return null;
  }

  private void setLinksInParagraphs(
      final List<Link> links, final List<Link> externalLinks, ParsedPage page) {
    int paragraphId = 0;
    for (Paragraph p : page.getParagraphs()) {
      for (de.tudarmstadt.ukp.wikipedia.parser.Link link : p.getLinks()) {
        Link.Builder linkAdded = createLink(link, LinkType.BODY);
        if (linkAdded != null) {
          linkAdded.setParagraphId(paragraphId);
          addLink(linkAdded, link.getType(), links, externalLinks);
        }
      }
      paragraphId++;
    }
  }

  private void setLinksInTables(
      final List<Link> links, final List<Link> externalLinks, ParsedPage page) {
    int tableId = 0;
    for (de.tudarmstadt.ukp.wikipedia.parser.Table p : page.getTables()) {

      for (int el = 0; el < p.nrOfTableElements(); el++) {
        int col = p.getTableElement(el).getCol();
        int row = p.getTableElement(el).getRow();
        for (de.tudarmstadt.ukp.wikipedia.parser.Link link : p.getTableElement(el).getLinks()) {
          Link.Builder linkAdded = createLink(link, LinkType.TABLE);
          if (linkAdded != null) {
            linkAdded.setTableId(tableId);
            linkAdded.setRowId(row);
            linkAdded.setColumnId(col);
            addLink(linkAdded, link.getType(), links, externalLinks);
          }
        }
      }
      tableId++;
    }
  }

  private void setLinksInLists(
      final List<Link> links, final List<Link> externalLinks, ParsedPage page) {
    int listId = 0;
    for (NestedListContainer p : page.getNestedLists()) {
      int item = 0;
      for (NestedList list : p.getNestedLists()) {
        for (de.tudarmstadt.ukp.wikipedia.parser.Link link : list.getLinks()) {
          Link.Builder linkAdded = createLink(link, LinkType.LIST);
          if (linkAdded != null) {
            linkAdded.setListId(listId);
            linkAdded.setListItem(item);
            addLink(linkAdded, link.getType(), links, externalLinks);
          }
        }
        item++;
      }
      listId++;
    }
  }

  private void setLinks(Article.Builder article, ParsedPage page) {
    final List<Link> links = new ArrayList<Link>(page.getLinks().size());
    final List<Link> elinks = new ArrayList<Link>(page.getLinks().size());
    setLinksInParagraphs(links, elinks, page);
    setLinksInTables(links, elinks, page);
    setLinksInLists(links, elinks, page);
    article.setLinks(links);
    article.setExternalLinks(elinks);
  }

  private void setIsList(Article.Builder article) {
    for (final String list : locale.getListIdentifiers()) {
      if (StringUtils.startsWithIgnoreCase(article.getTitle(), list)) {
        article.setType(ArticleType.LIST);
      }
    }
  }

  private void setRedirect(Article.Builder article) {
    if (!article.hasRedirect() || !article.getRedirect().isEmpty()) {
      return;
    }
    final List<List<String>> lists = article.getLists();
    if ((!lists.isEmpty()) && (!lists.get(0).isEmpty())) {
      // checking only first item in first list
      final String line = lists.get(0).get(0);

      for (final String redirect : redirects) {
        if (StringUtils.startsWithIgnoreCase(line, redirect)) {
          final int pos = line.indexOf(' ');
          if (pos < 0) {
            return;
          }
          String red = line.substring(pos).trim();
          red = ArticleHelper.getTitleInWikistyle(red);
          article.setRedirect(red);
          article.setType(ArticleType.REDIRECT);
          return;
        }
      }
    }
  }

  private void setRedirect(Article.Builder article, String mediawiki) {
    for (final String redirect : redirects) {
      if (StringUtils.startsWithIgnoreCase(mediawiki, redirect)) {
        final int start = mediawiki.indexOf("[[") + 2;
        final int end = mediawiki.indexOf("]]");
        if ((start < 0) || (end < 0)) {
          logger.warn("cannot find the redirect {}\n mediawiki: {}", article.getTitle(), mediawiki);
          continue;
        }
        final String r = ArticleHelper.getTitleInWikistyle(mediawiki.substring(start, end));
        article.setRedirect(r);
        article.setType(ArticleType.REDIRECT);
      }
    }
  }

  private void ensureLength(List<List<String>> rows, int minLen) {
    while (rows.size() < minLen) {
      rows.add(new ArrayList<>());
    }
  }

  private void setTables(Article.Builder article, ParsedPage page) {
    final List<Table> tables = new ArrayList<>();

    for (final de.tudarmstadt.ukp.wikipedia.parser.Table t : page.getTables()) {
      List<List<String>> table = new ArrayList<>();

      final String title;
      if (t.getTitleElement() != null) {
        title = t.getTitleElement().getText() != null ? t.getTitleElement().getText() : "";
      } else {
        title = "";
      }
      List<String> currentRow = null;
      int maxCols = 0;
      for (int elementId = 0; elementId < t.nrOfTableElements(); elementId++) {
        int col = t.getTableElement(elementId).getCol();
        int row = t.getTableElement(elementId).getRow(); // row starts from 1
        maxCols = Math.max(maxCols, col);
        final String elementText = t.getTableElement(elementId).getText();
        logger.debug(String.format("row:%s col:%s content:%s", row, col, elementText));
        ensureLength(table, row + 1);
        // assume that columns come in order
        table.get(row).add(elementText);
      }
      if (table.size() > 0 && table.get(0).isEmpty()) {
        // first raw is empty:
        table.remove(0);
      }
      logger.debug("ParsedTable: \n {}", table);

      final Table.Builder tableBuilder = Table.newBuilder().setTitle(title);
      tableBuilder.setTable(table);
      tableBuilder.setNumCols(maxCols);
      tableBuilder.setNumRows(table.size());
      tables.add(tableBuilder.build());
    }
    article.setTables(tables);
  }

  protected void setEnWikiTitle(Article.Builder article, ParsedPage page) {
    if (article.getLang().equals(Language.EN)) {
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

  private void setSections(Article.Builder article, ParsedPage page) {
    final List<String> sections = new ArrayList<String>(10);
    for (final Section s : page.getSections()) {

      if ((s == null) || (s.getTitle() == null)) {
        continue;
      }
      sections.add(s.getTitle());
    }
    article.setSections(sections);
  }

  private void setTemplates(Article.Builder article, ParsedPage page) {
    final List<Template> templates = new ArrayList<Template>(10);

    for (final de.tudarmstadt.ukp.wikipedia.parser.Template t : page.getTemplates()) {
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

  /** @param templateParameters */
  private void parseTemplatesSchema(Article.Builder article, List<String> templateParameters) {
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
    article.setTemplatesSchema(schema);
  }

  private void setCategories(Article.Builder article, ParsedPage page) {
    final List<Link> categories = new ArrayList<>(10);
    for (final de.tudarmstadt.ukp.wikipedia.parser.Link link : page.getCategories()) {
      Link.Builder linkBuilder = Link.newBuilder();
      linkBuilder.setId(link.getTarget());
      linkBuilder.setAnchor(link.getText());
      linkBuilder.setStart(link.getPos().getStart());
      linkBuilder.setEnd(link.getPos().getEnd());
      linkBuilder.setType(LinkType.CATEGORY);
      categories.add(linkBuilder.build());
    }
    article.setCategories(categories);
  }

  private void setHighlights(Article.Builder article, ParsedPage page) {
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

  private void setParagraphs(Article.Builder article, ParsedPage page) {
    final List<String> paragraphs = new ArrayList<String>(page.nrOfParagraphs());
    for (final Paragraph p : page.getParagraphs()) {
      String text = p.getText();
      if (!text.isEmpty()) {
        paragraphs.add(text);
      }
    }
    article.setParagraphs(paragraphs);
  }

  private void setDisambiguation(Article.Builder a) {

    for (final String disambiguation : locale.getDisambigutionIdentifiers()) {
      if (StringUtils.containsIgnoreCase(a.getTitle(), disambiguation)) {
        a.setType(ArticleType.DISAMBIGUATION);
        return;
      }
      if (a.hasTemplates()) {
        for (final Template t : a.getTemplates()) {
          if (StringUtils.equalsIgnoreCase(t.getName(), disambiguation)) {
            a.setType(ArticleType.DISAMBIGUATION);
            return;
          }
        }
      }
    }
  }
}

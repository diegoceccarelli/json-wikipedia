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
package it.cnr.isti.hpc.wikipedia.article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

/**
 * Article represents an article in the Wikipedia dump.
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 19/nov/2011
 */
public class Article {
	/**
	 * Logger for this class
	 */
	// private static final Logger logger =
	// LoggerFactory.getLogger(Article.class);

	private final static String NOTITLE = "";

	private static transient Gson gson = new Gson();

	/** The possible types of an article (e.g., template, article, category) **/
	public enum Type {
		TEMPLATE, ARTICLE, CATEGORY, DISCUSSION, REDIRECT, DISAMBIGUATION, UNKNOWN, MAIN, LIST, PROJECT, FILE
	};

	protected String title = NOTITLE;
	protected String wikiTitle = NOTITLE;

	private int wid;

	private String lang;
	private String namespace;
	private Integer integerNamespace;
	private String timestamp;
	private Type type = Type.ARTICLE;
	private String enWikiTitle;
	private List<Table> tables;
	private List<Link> images;
	protected List<List<String>> lists;
	private List<Link> links;
	private List<Link> externalLinks;
	protected String redirect;
	private List<String> sections;
	private List<String> paragraphs;
	private List<Link> categories;
	private List<Template> templates;
	private List<String> templatesSchema;
	private List<String> highlights;
	private transient String summary;
	private Template infobox;

	public List<String> getTemplatesSchema() {
		if (templatesSchema == null)
			return Collections.emptyList();
		return templatesSchema;
	}

	public void setTemplatesSchema(List<String> templatesSchema) {
		this.templatesSchema = templatesSchema;
	}

	public List<String> getParagraphs() {
		if (paragraphs == null)
			return Collections.emptyList();
		return paragraphs;
	}

	public List<String> getCleanParagraphs() {
		List<String> paragraphs = getParagraphs();
		if (paragraphs.isEmpty())
			return Collections.emptyList();
		List<String> cleanParagraphs = new ArrayList<String>(paragraphs.size());
		for (String p : paragraphs) {
			cleanParagraphs.add(removeTemplates(p));
		}
		return cleanParagraphs;
	}

	public String getCleanText() {
		StringBuilder sb = new StringBuilder();
		for (String s : getCleanParagraphs()) {
			sb.append(s).append(" ");

		}
		return sb.toString();
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (String s : getParagraphs()) {
			sb.append(s).append(" ");

		}
		return sb.toString();
	}

	public void setParagraphs(List<String> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public String getRedirect() {
		if (redirect == null)
			return "";
		// 12/03/2013, in the new version of wikipedia
		// redirects sometime contain the reason of the redirect,
		// e.g.,
		// Abbey_TEMPLATE[R_from_CamelCase]

		redirect = redirect.replaceAll("_*TEMPLATE.*$", "");
		return getTitleInWikistyle(redirect);
		// return redirect; // .toLowerCase();
	}

	/**
	 * the redirect without the anchor, e.g., da_vinci#life -> da_vinci
	 *
	 * @return the redirect without the anchor
	 */
	public String getRedirectNoAnchor() {
		String redirect = getRedirect();
		int pos = redirect.indexOf('#');
		if (pos < 0)
			return redirect;
		return redirect.substring(0, pos);
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public Article() {

	}

	public String getTitleInWikistyle() {
		return getTitleInWikistyle(title);
	}

	public boolean isList() {
		return type == Type.LIST;
	}

	public static String getTitleInWikistyle(String title) {
		if (title.isEmpty())
			return title;
		title = title.replace(' ', '_'); // .toLowerCase();
		// first letter is capitalized
		title = Character.toUpperCase(title.charAt(0)) + title.substring(1);
		return title;
	}

	public boolean hasEnWikiTitle() {
		return (enWikiTitle != null) && (!enWikiTitle.isEmpty());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getHighlights() {
		if (highlights == null)
			return Collections.emptyList();
		return highlights;
	}

	public void setHighlights(List<String> highlights) {
		this.highlights = highlights;
	}

	// public static Article fromMediaWiki(String mediawiki) {
	// if (parser == null) {
	// MediaWikiParserFactory factory = new EnMediaWikiParserFactory();
	// parser = factory.createParser();
	// }
	//
	// ParsedPage page = parser.parse(mediawiki);
	// Article article = new Article();
	// article.setParagraphs(page);
	// article.setTemplates(page);
	// article.setLinks(page);
	// article.setCategories(page);
	// article.setHighlights(page);
	// article.setSections(page);
	// article.setTables(page);
	// article.setEnId(page);
	// article.setLists(page);
	//
	// if (mediawiki.startsWith(REDIRECT_UC) ||
	// mediawiki.startsWith(REDIRECT_LC)) {
	// int start = mediawiki.indexOf("[[") + 2;
	// int end = mediawiki.indexOf("]]");
	// String r = Article.getTitleInWikistyle(mediawiki.substring(start, end));
	// article.setRedirect(r);
	// }
	//
	// // StringBuilder sb = new StringBuilder();
	// // String t = article.getFirstParagraph().getText();
	// //
	// // sb.append("\t").append(t).append("\n");
	// // for (Section s : article.getSections()){
	// // sb.append(s.getTitle()).append("\n");
	// // for ( Paragraph p : s.getParagraphs()){
	// // t = p.getText();
	// // if (t.startsWith("TEMPLATE")) continue;
	// // sb.append("\t").append(t).append("\n");
	// // }
	// // }
	// // a.setText(sb.toString());
	//
	// return article;
	// }

	/**
	 * @return the images
	 */
	public List<Link> getImages() {
		return images;
	}

	/**
	 * @param images
	 *            the images to set
	 */
	public void setImages(List<Link> images) {
		this.images = images;
	}

	public boolean isRedirect() {

		return type == Type.REDIRECT;
		// if ((redirect != null) && (!redirect.isEmpty())) {
		// return true;
		// }
		// for (List<String> lists : getLists()) {
		// for (String line : lists) {
		// if (line.startsWith("redirect ")) {
		// // remove "redirect " string
		// String red = line.substring(9);
		// red = red.trim();
		// redirect = Article.getTitleInWikistyle(red);
		// return true;
		// }
		// }
		// }
		// return false;
	}

	public boolean isDisambiguation() {
		return type == Type.DISAMBIGUATION;
		// if (title.contains("disambiguation"))
		// return true;
		// for (Template t : templates) {
		// if (t.isDisambiguation())
		// return true;
		// }
		// return false;
	}

	// MOVE IN THE FACTORY
	// private void setLists(ParsedPage page) {
	// lists = new LinkedList<List<String>>();
	// for (DefinitionList dl : page.getDefinitionLists()) {
	// List<String> l = new ArrayList<String>();
	// for (ContentElement c : dl.getDefinitions()) {
	// l.add(c.getText());
	// }
	// lists.add(l);
	// }
	// for (NestedListContainer dl : page.getNestedLists()) {
	// List<String> l = new ArrayList<String>();
	// for (NestedList nl : dl.getNestedLists())
	// l.add(nl.getText());
	// lists.add(l);
	// }
	//
	// }

	public List<List<String>> getLists() {
		if (lists == null)
			return Collections.emptyList();
		return lists;
	}

	// public List<Link> getIncomingLinks() {
	// if (incomingLinks == null)
	// return Collections.emptyList();
	// return incomingLinks;
	// }

	// public void setIncomingLinks(List<Link> incomingLinks) {
	// this.incomingLinks = incomingLinks;
	// }

	public int getWikiId() {
		return wid;
	}

	public void setWikiId(int wid) {
		this.wid = wid;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Integer getIntegerNamespace() {
		return integerNamespace;
	}

	public void setIntegerNamespace(Integer integerNamespace) {
		this.integerNamespace = integerNamespace;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setLists(List<List<String>> lists) {
		this.lists = lists;
	}

	public String getLang() {
		return lang;
	}

	public boolean isLang(String lang) {
		return lang.equals(lang);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TITLE:").append(getTitle()).append("\n");
		sb.append("ENGLISH_ID:").append(getEnWikiTitle()).append("\n");
		sb.append("TYPE:").append(getTypeName()).append("\n");
		sb.append("SECTION_TITLES:").append(getSections()).append("\n");
		sb.append("TEMPLATES:").append(getTemplates()).append("\n");
		sb.append("TEMPLATEsSCHEMA:").append(getTemplatesSchema()).append("\n");
		sb.append("HIGHLIGHTS:").append(getHighlights()).append("\n");
		sb.append("TABLES:").append("\n");
		for (Table p : getTables())
			sb.append("\t").append(p).append("\n");
		sb.append("LISTS:").append("\n");
		for (List<String> l : getLists())
			sb.append("\t").append(l).append("\n");
		sb.append("INFOBOX:").append(getInfobox()).append("\n");

		sb.append("LINKS:\n");
		for (Link l : getLinks())
			sb.append("\t").append(l).append("\n");

		sb.append("EXTERNALLINKS:\n");
		for (Link l : getExternalLinks())
			sb.append("\t").append(l).append("\n");

		sb.append("CATEGORIES:\n");
		for (Link l : getCategories())
			sb.append("\t").append(l).append("\n");
		sb.append("PARAGRAPHs:\n");
		for (String p : getParagraphs())
			sb.append("\t").append(p).append("\n");
		return sb.toString();
	}

	/**
	 * Removes the TEMPLATE text from the row text of the article.
	 *
	 * @param text
	 * @return the 'cleaned' text
	 */
	@Deprecated
	private String removeTemplates(String text) {
		// dirty code, i'm sure there is a better way to exclude
		// template code from getText() (setting ignores..)
		while (text.contains("TEMPLATE[")) {
			int pos = text.indexOf("TEMPLATE[");
			int start = pos + "TEMPLATE".length() + 1;
			int end = start;
			int c = 1;
			while (c > 0) {
				if (end >= text.length())
					break;
				if (text.charAt(end) == '[')
					c++;
				if (text.charAt(end) == ']')
					c--;
				end++;
			}
			text = text.substring(0, pos) + text.substring(end);
		}
		return text;

	}

	public List<Table> getTables() {
		if (tables == null)
			return Collections.emptyList();
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public List<Template> getTemplates() {
		if (templates == null)
			return Collections.emptyList();
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public List<Link> getLinks() {
		if (links == null)
			return Collections.emptyList();
		return links;
	}

	public List<Link> getExternalLinks() {
		if (externalLinks == null)
			return Collections.emptyList();
		return externalLinks;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public void setExternalLinks(List<Link> links) {
		this.externalLinks = links;
	}

	public void addCategory(Link category) {
		if (this.getCategories() == null)
			categories = new ArrayList<Link>();
		categories.add(category);
	}

	public void addAllCategories(List<Link> categories) {
		if (this.getCategories() == null)
			this.setCategories(categories);
		else
			this.getCategories().addAll(categories);
	}

	public List<Link> getCategories() {
		if (categories == null)
			return Collections.emptyList();
		return categories;
	}

	public void setCategories(List<Link> categories) {
		this.categories = categories;
	}

	public Template getInfobox() {
		if (infobox == null)
			return Template.EMPTY_TEMPLATE;
		return infobox;
	}

	public boolean hasInfobox() {
		return infobox != null;
	}

	public void setInfobox(Template infobox) {
		this.infobox = infobox;
	}

	public List<String> getSections() {
		if (sections == null)
			return Collections.emptyList();
		return sections;
	}

	public void setSections(List<String> sections) {
		this.sections = sections;
	}

	public String getEnWikiTitle() {
		if (isLang(Language.EN)) {
			return wikiTitle;
		}
		if (enWikiTitle == null)
			return "";

		return enWikiTitle;
	}

	public void setEnWikiTitle(String enWikiTitle) {
		this.enWikiTitle = enWikiTitle;
	}

	/**
	 * @param schema
	 */
	public void addTemplatesSchema(List<String> schema) {
		if (templatesSchema == null) {
			templatesSchema = schema;
		} else {
			templatesSchema.addAll(schema);
		}
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public static Article fromJson(String json) {
		return gson.fromJson(json, Article.class);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + wid;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Article other = (Article) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (wid != other.wid)
			return false;
		return true;
	}

	/**
	 * @return the wikiTitle
	 */
	public String getWikiTitle() {
		return wikiTitle;
	}

	/**
	 * @param wikiTitle
	 *            the wikiTitle to set
	 */
	public void setWikiTitle(String wikiTitle) {
		this.wikiTitle = wikiTitle;
	}

	/**
	 * @return the wid
	 */
	public int getWid() {
		return wid;
	}

	/**
	 * @param wid
	 *            the wid to set
	 */
	public void setWid(int wid) {
		this.wid = wid;
	}

	public String getSnippet() {
		StringBuilder sb = new StringBuilder();
		sb.append("title:").append(getTitle()).append("\n");
		sb.append("wiki-id:").append(getWikiId()).append("\n");
		sb.append("description:\n");
		if (getSummary().isEmpty()) {
			String text = getCleanText();
			if (text.length() > 1000) {
				sb.append(text.substring(0, 1000) + "...");

			} else {
				sb.append(text);
			}
		} else {
			sb.append(getSummary());
		}
		sb.append("\n");
		return sb.toString();
	}

	// /**
	// * @return the shortDescription
	// */
	// public String getShortDescription() {
	// return shortDescription;
	// }
	//
	// /**
	// * @param shortDescription the shortDescription to set
	// */
	// public void setShortDescription(String shortDescription) {
	// this.shortDescription = shortDescription;
	// }

	public String getSummary() {
		if (summary == null)
			return "";
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getTypeName() {
		switch (type) {
		case TEMPLATE:
			return "Template";
		case ARTICLE:
			return "Article";
		case CATEGORY:
			return "Category";
		case DISCUSSION:
			return "Discussion";
		case REDIRECT:
			return "Redirect";
		case DISAMBIGUATION:
			return "Disambiguation";
		case UNKNOWN:
			return "Unknown";
		case MAIN:
			return "Main";
		case LIST:
			return "List";
		case PROJECT:
			return "Project";
		case FILE:
			return "File";

		}
		return "NULL";

	}

}

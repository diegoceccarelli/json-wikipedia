package it.cnr.isti.hpc.wikipedia.article;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArticleHelper {
  private final static Gson GSON = new Gson();

	public static Article fromJson(String json){
    return GSON.fromJson(json, Article.class);
  }

  public static String cleanText(List<String> paragraphs){
    return ArticleHelper.getCleanText(ArticleHelper.getCleanParagraphs(paragraphs));
  }

  	private static String getCleanText(List<String> cleanedParagraphs) {
		StringBuilder sb = new StringBuilder();
		for (String s : cleanedParagraphs) {
			sb.append(s).append(" ");

		}
		return sb.toString();
	}

	public static String wikiStyleToText(String text){
		return text.replace('_', ' ');
	}


	public static List<String> getCleanParagraphs(List<String> paragraphs) {
		if (paragraphs.isEmpty())
			return Collections.emptyList();
		List<String> cleanParagraphs = new ArrayList<String>(paragraphs.size());
		for (String p : paragraphs) {
			cleanParagraphs.add(removeTemplates(p));
		}
		return cleanParagraphs;
	}

  	/**
	 * Removes the TEMPLATE text from the row text of the article.
	 *
	 * @param text
	 * @return the 'cleaned' text
	 */
	@Deprecated
	private static String removeTemplates(String text) {
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

  	public static String getTitleInWikistyle(String title) {
		if (title.isEmpty())
			return title;
		title = title.replace(' ', '_'); // .toLowerCase();
		// first letter is capitalized
		title = Character.toUpperCase(title.charAt(0)) + title.substring(1);
		return title;
	}

  public static boolean isDisambiguation(Article a) {
    return a.getType() == ArticleType.DISAMBIGUATION;
  }
}

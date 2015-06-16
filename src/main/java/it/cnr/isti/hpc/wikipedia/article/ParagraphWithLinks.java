package it.cnr.isti.hpc.wikipedia.article;

import java.util.List;

/**
 * Created by David Przybilla
 */
public class ParagraphWithLinks {

    private String paragraph;
    private List<Link> links;

    public ParagraphWithLinks(String paragraph, List<Link> links){
        this.paragraph = paragraph;
        this.links = links;
    }

    public String getParagraph(){
        return paragraph;
    }

    public List<Link> getLinks(){
        return links;
    }

}

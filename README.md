![json-wikipedia](https://dl.dropboxusercontent.com/u/4663256/tmp/json-wikipedia.png) json-wikipedia
==============

 Json Wikipedia contains code to convert the Wikipedia XML dump in a [JSON][json] dump.

#### Setup ####

compile the project running 

    mvn assembly:assembly 
	
the command will produce a JAR file containing all the dependencies the target folder.  

#### Convert the Wikipedia XML to JSON ####

    java target/json-wikipedia-1.0.0-jar-with-dependencies.jar it.cnr.isti.hpc.wikipedia.cli.MediawikiToJsonCLI -input wikipedia-dump.xml.bz -output wikipedia-dump.json[.gz] -lang [en|it] 		

or 

	./scripts/convert-xml-dump-to-json.sh [en|it] wikipedia-dump.xml.bz wikipedia-dump.json[.gz]

produces in `wikipedia-dump.json` the JSON version of the dump. Each line of the file contains an article 
of dump encoded in JSON. Each JSON line can be deserialized in an [Article](http://sassicaia.isti.cnr.it/javadocs/json-wikipedia/it/cnr/isti/hpc/wikipedia/article/Article.html) object, 
which represents an 
_enriched_ version of the wikitext page. The Article object contains: 


  * the title (e.g., Leonardo Da Vinci);
  * the wikititle (used in Wikipedia as key, e.g., Leonardo\_Da\_Vinci);
  * the namespace and the integer namespace in the dump;
  * the timestamp of the article;
  * the type, if it is a standard article, a redirection, a category and so on;
  * if it is not in English the title of the correspondent English Article;
  * a list of  tables that appear in the article ;
  * a list of lists that  that appear in the article ;
  * a list  of internal links that appear in the article;
  * if the article  is a redirect, the pointed article;
  * a list of section titles in the article;
  * the text of the article, divided in paragraphs;
  * the categories and the templates of the articles;
  * the list of attributes found in the templates;
  * a list of terms highlighted in the article;
  * if present, the infobox.
  
#### Usage ####

Once you have created (or downloaded) the JSON dump (say `wikipedia.json`), you can iterate over the articles of the collection 
easily using this snippet: 

    RecordReader<Article> reader = new RecordReader<Article>(
			"wikipedia.json",new JsonRecordParser<Article>(Article.class)
    ).filter(TypeFilter.STD_FILTER);

    for (Article a : reader) {
	// do what you want with your articles	
    }
 
You can also add some filters in order to iterate only on certain articles (in the example 
we used only the standard type filter, which excludes meta pages e.g., Portal: or User: pages.).

The [RecordReader](http://sassicaia.isti.cnr.it/javadocs/hpc-utils/it/cnr/isti/hpc/io/reader/RecordReader.html) and 
[JsonRecordParser](http://sassicaia.isti.cnr.it/javadocs/hpc-utils/it/cnr/isti/hpc/io/reader/JsonRecordParser.html) are part
of the [hpc-utils](http://sassicaia.isti.cnr.it/javadocs/hpc-utils) package.

In order to use these classes, you will have to install `json-wikipedia` in your maven repository:

    mvn install

and import the project in your new maven project adding the dependency: 

    <dependency>
	    <groupId>it.cnr.isti.hpc</groupId>
		<artifactId>json-wikipedia</artifactId>
		<version>1.0.0</version>
	</dependency> 


[json]: http://www.json.org/fatfree.html "JSON: The Fat-Free Alternative to XML"

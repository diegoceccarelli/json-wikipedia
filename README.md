json-wikipedia ![travis-ci-badge](https://travis-ci.org/diegoceccarelli/json-wikipedia.svg?branch=master) 
==============

 Json Wikipedia contains code to convert the Wikipedia XML dump into a JSON or avro dump.

 - Please be aware that this tool does not work with the `multistream` dump.

### Setup ###

compile the project running

    mvn package

the command will produce a JAR file containing all the dependencies the target folder.

### Convert the Wikipedia XML ###


You can convert the Wikipedia dump to *JSON* format by running the commands:

    java -jar target/json-wikipedia-*.jar -input wikipedia-dump.xml.bz -output wikipedia-dump.json[.gz] -lang [en|it]

or

	./scripts/convert-xml-dump-to-json.sh [en|it] wikipedia-dump.xml.bz wikipedia-dump.json[.gz]

Or to [Apache Avro](https://avro.apache.org):


    java -jar target/json-wikipedia-*.jar -input wikipedia-dump.xml.bz -output wikipedia-dump.avro -lang [en|it]

or

	./scripts/convert-xml-dump-to-json.sh [en|it] wikipedia-dump.xml.bz wikipedia-dump.avro

### Note: convert multistream dump

Converting the wikipedia multistream dump will fail. Apparently this is due to issue reading from the bzip. You can work around by uncompressing the dump before and passing it on the standard input, using the special '-' to tell json-wikipedia to read from the stdin: 

    bzcat wikipedia-multistream-dump.xml.gz | java -jar target/json-wikipedia-*.jar - wikipedia-dump.avro [en|it]

or

	.bzcat wikipedia-multistream-dump.xml.gz | /scripts/convert-xml-dump-to-json.sh [en|it] - wikipedia-dump.avro



### Content of the output

Both the commands will produce a file contain a file containing a record for each article. In the JSON format each line of the file contains an article
of dump encoded in JSON. Each record can be deserialized in an [Article](https://github.com/diegoceccarelli/json-wikipedia/blob/master/src/main/java/it/cnr/isti/hpc/wikipedia/article/Article.java) object, which represents an 
_enriched_ version of the wikitext page. The Article object contains:


  * the title (e.g., Leonardo Da Vinci);
  * the wikititle (used in Wikipedia as key, e.g., Leonardo\_Da\_Vinci);
  * the namespace and the integer namespace in the dump;
  * the timestamp of the article;
  * the type, if it is a standard article, a redirection, a category and so on;
  * if it is not in English the title of the correspondent English Article;
  * a list of  tables that appear in the article ;
  * a list of lists that  that appear in the article ;
  * a list  of internal links that appear in the article (each link containing the type (image, link, table link etc) and the position in the page;
  * a list of external links that appear in the article;
  * if the article  is a redirect, the pointed article;
  * a list of section titles in the article;
  * the text of the article, divided in paragraphs (PLAIN, no wikitext);
  * the categories and the templates of the articles;
  * the list of attributes found in the templates;
  * a list of terms highlighted in the article;
  * if present, the infobox.

#### Usage ####

Once you have created (or downloaded) the JSON dump (say `wikipedia.json`), you can iterate over the articles of the collection
easily using this snippet:

    RecordReader<Article> reader = new RecordReader<Article>(
			"wikipedia.json",new JsonRecordParser<Article>(Article.class)
    )

    for (Article a : reader) {
	// do what you want with your articles
    }

In order to use these classes, you will have to install `json-wikipedia` in your maven repository:

    mvn install

and import the project in your new maven project adding the dependency:

    <dependency>
	    <groupId>it.cnr.isti.hpc</groupId>
		<artifactId>json-wikipedia</artifactId>
		<version>2.0.0-SNAPSHOT</version>
	</dependency>

#### Schema ####

The full schema of a record is encoded in [avro](src/main/avro/article.avsc)


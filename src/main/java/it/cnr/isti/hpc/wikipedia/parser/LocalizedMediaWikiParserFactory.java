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

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * Generates a parser from the proper Locale.
 *
 * @see Locale
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 */
public class LocalizedMediaWikiParserFactory extends MediaWikiParserFactory {

  private Locale locale;

  public LocalizedMediaWikiParserFactory(Locale locale) {
    super();
    this.locale = locale;

    init();
  }

  private void init() {

    for (String name : locale.getImageIdentifiers()) getImageIdentifers().add(name);

    for (String name : locale.getCategoryIdentifiers()) getCategoryIdentifers().add(name);
  }
}

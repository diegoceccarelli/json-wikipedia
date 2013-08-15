/**
 *  Copyright 2012 Diego Ceccarelli
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
package it.cnr.isti.hpc.wikipedia.reader.filter;

import java.util.Arrays;
import java.util.List;

import it.cnr.isti.hpc.io.reader.Filter;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Article.Type;

/**
 * TypeFilter filters the articles base on their type.
 *  
 * @see Article.Type
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 05/lug/2012
 */
public class TypeFilter implements Filter<Article> {
	
	List<Type> types;
	
	public final static TypeFilter MAIN = new TypeFilter(Type.MAIN, Type.ARTICLE, Type.LIST);
	public final static TypeFilter MAIN_CATEGORY_TEMPLATE = new TypeFilter(Type.MAIN, Type.CATEGORY, Type.TEMPLATE);

	public static final Filter<Article> STD_FILTER = new TypeFilter(Type.ARTICLE, Type.CATEGORY, Type.LIST, Type.REDIRECT, Type.DISAMBIGUATION);
	
	public TypeFilter(Type ... types) {
		this.types = Arrays.asList(types);
	}

	public boolean isFilter(Article a) {
		return !types.contains(a.getType());
	}
}

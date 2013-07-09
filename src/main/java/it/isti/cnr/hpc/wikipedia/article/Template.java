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
package it.isti.cnr.hpc.wikipedia.article;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Template represents a particular template in a article.
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 19/nov/2011
 */
public class Template {
	
	public final static Template EMPTY = new EmptyTemplate(); 
	private final static char KEY_VALUE_SEPARATOR = '=';
	
	private String name;
	private List<String> description;
	public static final Template EMPTY_TEMPLATE = new EmptyTemplate();
	
	public boolean isDisambiguation(){

		return name.equalsIgnoreCase("disambiguation") || name.startsWith("Disamb") || name.startsWith("disamb");
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Template other = (Template) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public Template(String name, List<String> description) {
		super();
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getDescription() {
		if (description == null) return Collections.emptyList();
		return description;
	}
	
	public void setDescription(List<String> description) {
		this.description = description;
	}
	
	public Set<String> getSchema(){
		return getAsMap().keySet();
	}
	
	public Map<String,String> getAsMap(){
		Map<String,String> map = new HashMap<String,String>();
		for (String desc : getDescription()){
			int pos = desc.indexOf(KEY_VALUE_SEPARATOR);
			if (pos >= 0){
				String key = desc.substring(0,pos).trim();
				String value = desc.substring(pos+1).trim();
				map.put(key, value);
			}
		}
		return map;
		
	}


	
	@Override
	public String toString() {
		return "Template [name=" + name + ", description=" + description + "]";
	}
	
	
	private static class EmptyTemplate extends Template {

		/**
		 * @param name
		 * @param description
		 */
		public EmptyTemplate() {
			super("",null);
		}
		
		

		public String toString(){
			return "";
		}
	}

	
}

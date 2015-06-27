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

/**
 * Link class models a link an internal link on Wikipedia. The class contains
 * two attributes: the target <b> id </b>: a string representing the name of the
 * page pointed by the link (e.g., <tt>FIFA_World_Cup_awards</tt>), and the <b>
 * description </b>, i.e., the anchor text used in a article for linking to the
 * target id (e.g., <tt>golden shoe</tt>).
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 19/nov/2011
 */
public class Link {

	/** a string representing the name of the page pointed by the link **/
	private String id;
	/** the anchor text used in a article for linking to the target id **/
	private String description;

	public Link(String id, String description) {
		super();
		this.id = id;
		setDescription(description);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		// Some links do not have any anchor
		// For those cases the anchor is the same wikipedia Id
		if (description.isEmpty() && this.id != null) {
			this.description = this.id.replace('_', ' ');
		} else {
			this.description = description;
		}
	}

	/**
	 * @return the wikiname of the link without anchors (e.g.
	 *         leonardo_da_vinci#life -> leonardo_da_vinci)
	 */
	public String getCleanId() {
		String cleanId = id;
		// capitalize first char
		if (!cleanId.isEmpty()) {
			cleanId = Character.toUpperCase(cleanId.charAt(0))
					+ cleanId.substring(1);
		}
		int pos = cleanId.indexOf('#');
		if (pos >= 0) {
			cleanId = cleanId.substring(0, pos);
		}
		// cleanId = cleanId.replace("\"", "");

		return cleanId; // .toLowerCase();
	}

	/**
	 * @return true, if the target id is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return getCleanId().isEmpty();
	}

	@Override
	public String toString() {
		return "Link [id=" + id + ", description=" + getDescription() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Link other = (Link) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

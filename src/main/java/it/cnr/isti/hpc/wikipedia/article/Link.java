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

import java.util.List;

/**
 * Link class models a link an internal link on Wikipedia. 
 * The class contains two attributes: the target <b> id </b>: a string representing the 
 * name of the page pointed by the link (e.g., <tt>FIFA_World_Cup_awards</tt>), and the 
 * <b> description </b>, i.e., the anchor text used in a article for linking
 * to the target id (e.g., <tt>golden shoe</tt>).
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 19/nov/2011
 */
public class Link {
	
	/** a string representing the name of the page pointed by the link **/
	private String id;
	/** the anchor text used in a article for linking to the target id **/
	private String anchor;
	
	private int start;
	private int end;
	private Integer paragraphId;
	private Integer listId;
	private Integer listItem;
	private Integer tableId;
	private Integer rowId;
	private Integer columnId;
	private Type type;
	private List<String> params;
	
	/** The possible types of a Link (e.g., body, table, list) **/
	public enum Type {
		BODY, TABLE, LIST, IMAGE
	};

	public Link(String id, String anchor, int start, int end) {
		super();
		init(id, anchor, start, end);
	}
	
	public Link(String id, String anchor, int start, int end, Type type, Integer paragraphId) {
		super();
		init(id, anchor, start, end);
		this.type = type;
		this.paragraphId = paragraphId;
	}
	
	public Link(String id, String anchor, int start, int end, Type type, Integer listId, Integer listItem) {
		super();
		init(id, anchor, start, end);
		this.type = type;
		this.listId = listId;
		this.listItem = listItem;
	}
	
	public Link(String id, String anchor, int start, int end, Type type, Integer tableId, Integer rowId, Integer columnId) {
		super();
		init(id, anchor, start, end);
		this.type = type;
		this.tableId = tableId;
		this.rowId = rowId;
		this.columnId = columnId;
	}
	
	private void init(String id, String anchor, int start, int end) {
		this.id = id;
		setAnchor(anchor);
		this.start = start;
		this.end = end;
	}
	
	public String getId() {
		return id;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getAnchor() {
		return this.anchor;
	}
	
	
	public void setAnchor(String anchor) {
        // Some links do not have any anchor
        // For those cases the anchor is the same wikipedia Id
		if (anchor.isEmpty()){
            this.anchor = this.id.replace("_", " ");
        }else{
            this.anchor = anchor;
        }
	}

	/**  
	 * @return the wikiname of the link without anchors (e.g. leonardo_da_vinci#life -> leonardo_da_vinci)
	 */
	public String getCleanId(){
		String cleanId = id;
		// capitalize first char
		if ( ! cleanId.isEmpty()){
			cleanId = Character.toUpperCase(cleanId.charAt(0))+cleanId.substring(1);
		}
		final int pos = cleanId.indexOf('#');
		if (pos >= 0){
			cleanId = cleanId.substring(0,pos);
		}
		//cleanId = cleanId.replace("\"", "");
		
		return cleanId; // .toLowerCase();
	}
	
	/**
	 * @return true, if the target id is empty, false otherwise.
	 */
	public boolean isEmpty(){
		return getCleanId().isEmpty();
	}

	@Override
	public String toString() {
		return "Link [id=" + id + ", anchor=" + anchor + ", start=" + start + ", end=" + end + ", type=" + type + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((anchor == null) ? 0 : anchor.hashCode());
		result = (prime * result) + end;
		result = (prime * result) + ((id == null) ? 0 : id.hashCode());
		result = (prime * result) + start;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
      return true;
    }
		if (obj == null) {
      return false;
    }
		if (getClass() != obj.getClass()) {
      return false;
    }
		final Link other = (Link) obj;
		if (anchor == null) {
			if (other.anchor != null) {
        return false;
      }
		} else if (!anchor.equals(other.anchor)) {
      return false;
    }
		if (end != other.end) {
      return false;
    }
		if (id == null) {
			if (other.id != null) {
        return false;
      }
		} else if (!id.equals(other.id)) {
      return false;
    }
		if (start != other.start) {
      return false;
    }
		return true;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getParagraphId() {
		return paragraphId;
	}

	public void setParagraphId(Integer paragraphId) {
		this.paragraphId = paragraphId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public int getListItem() {
		return listItem;
	}

	public void setListItem(Integer listItem) {
		this.listItem = listItem;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}

	public int getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}
	
	public void setParams(List<String> params){
	  this.params = params;
	}
}

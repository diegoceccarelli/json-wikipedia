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
	/** where the anchor starts in the text **/
	private Integer start;
	/** where the anchor ends in the text **/
	private Integer end;
	/** if it occurs in a paragraph, the ordinal of the paragraph **/
	private Integer paragraphId;
	/** if it occurs in a list, the ordinal of the list among all the lists **/
	private Integer listId;
	/** if it occurs in a  list, the item in the list **/
	private Integer listItem;
	/** if it occurs in a table, the ordinal of the table among all the tables **/
	private Integer tableId;
	/** if it occurs in a table, the ordinal of the row **/
	private Integer rowId;
	/** if it occurs in a paragraph, the ordinal of the paragraph **/
	private Integer columnId;
	private Type type;
	private List<String> params;

	/** The possible types of a Link (e.g., body, table, list) **/
	public enum Type {
		BODY, TABLE, LIST, IMAGE, UNKNOWN, CATEGORY
	};

	@Deprecated
	public Link(String id, String description) {
		super();
		this.id = id;
		setDescription(description);
	}

	public Link(String id, String anchor, int start, int end, Type type) {
		this.id = id;
		this.type = type;
		this.anchor = anchor;
		this.start = start;
		this.end = end;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@Deprecated
	public String getDescription() {
		return getAnchor();
	}

	public String getAnchor() {
		if (anchor == null || anchor.isEmpty()){
			return targetToAnchor(id);
		}
		return anchor;
	}

	public static String targetToAnchor(String target){
		return target.replace('_', ' ');
	}

	@Deprecated
	public void setDescription(String description){
		setAnchor(description);
	}
	
	public void setAnchor(String anchor) {
        // Some links do not have any anchor
        // For those cases the anchor is the same wikipedia Id
		if (anchor.isEmpty()){
			this.anchor = targetToAnchor(id);
		} else {
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
		return cleanId;
	}
	
	/**
	 * @return true, if the target id is empty, false otherwise.
	 */
	public boolean isEmpty(){
		return getCleanId().isEmpty();
	}

	@Override
	public String toString() {
		return "Link{" +
			"id='" + id + '\'' +
			", anchor='" + anchor + '\'' +
			", start=" + start +
			", end=" + end +
			", paragraphId=" + paragraphId +
			", listId=" + listId +
			", listItem=" + listItem +
			", tableId=" + tableId +
			", rowId=" + rowId +
			", columnId=" + columnId +
			", type=" + type +
			", params=" + params +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Link link = (Link) o;

		if (id != null ? !id.equals(link.id) : link.id != null) return false;
		if (anchor != null ? !anchor.equals(link.anchor) : link.anchor != null) return false;
		if (start != null ? !start.equals(link.start) : link.start != null) return false;
		if (end != null ? !end.equals(link.end) : link.end != null) return false;
		if (paragraphId != null ? !paragraphId.equals(link.paragraphId) : link.paragraphId != null) return false;
		if (listId != null ? !listId.equals(link.listId) : link.listId != null) return false;
		if (listItem != null ? !listItem.equals(link.listItem) : link.listItem != null) return false;
		if (tableId != null ? !tableId.equals(link.tableId) : link.tableId != null) return false;
		if (rowId != null ? !rowId.equals(link.rowId) : link.rowId != null) return false;
		if (columnId != null ? !columnId.equals(link.columnId) : link.columnId != null) return false;
		if (type != link.type) return false;
		return params != null ? params.equals(link.params) : link.params == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (anchor != null ? anchor.hashCode() : 0);
		result = 31 * result + (start != null ? start.hashCode() : 0);
		result = 31 * result + (end != null ? end.hashCode() : 0);
		result = 31 * result + (paragraphId != null ? paragraphId.hashCode() : 0);
		result = 31 * result + (listId != null ? listId.hashCode() : 0);
		result = 31 * result + (listItem != null ? listItem.hashCode() : 0);
		result = 31 * result + (tableId != null ? tableId.hashCode() : 0);
		result = 31 * result + (rowId != null ? rowId.hashCode() : 0);
		result = 31 * result + (columnId != null ? columnId.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (params != null ? params.hashCode() : 0);
		return result;
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

	public Integer getParagraphId() {
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

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Table models a table structure encoded in an article.
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 20/nov/2011
 */
public class Table {
	private String name;
	private List<List<String>> table;
	private int numCols;
	private int numRows;
	
	public Table(){
		numCols = 0;
		numRows = 0;
		table = new LinkedList<List<String>>();
	}

	public Table(String name){
		this();
		this.name = name;		
	}
	
	//add at the current position 
	public void addRow(List<String> row){
		table.add(row);
		numRows++;
		numCols = Math.max(numCols,row.size());
	}
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + numCols;
		result = prime * result + numRows;
		result = prime * result + ((table == null) ? 0 : table.hashCode());
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
		Table other = (Table) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (numCols != other.numCols)
			return false;
		if (numRows != other.numRows)
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<List<String>> getTable() {
		return table;
	}
	public void setTable(List<List<String>> table) {
		this.table = table;
	}
	public int getNumCols() {
		return numCols;
	}
	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}
	public int getNumRows() {
		return numRows;
	}
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("Table ").append(getName()).append("\n");
		for (List<String> r : table){
			sb.append(r).append("\n");
		}
		return sb.toString();
	}
	
	public List<String> getColumn(int i){
		List<String> col = new ArrayList<String>(numRows);
		if (i >=  numCols) return col;
		for (List<String> row : table){
			if (i >= row.size()) col.add("");
			else col.add(row.get(i));
		}
		return col;
	}
	
	
	
	
	
}

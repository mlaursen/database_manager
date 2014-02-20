package com.github.mlaursen.database.objects;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A version of a SQL ResultSet. The only difference is that this is a List of
 * MyResultRows instead of a ResultSet that must be closed.
 * 
 * @author mikkel.laursen
 * 
 */
public class MyResultSet implements Iterable<MyResultRow> {
	private List<MyResultRow> rs;
	private List<String> colNames;
	private int size;

	public MyResultSet(List<MyResultRow> rs) {
		this.rs = rs;
		this.size = rs.size();
	}

	public MyResultSet(List<MyResultRow> rs, List<String> colNames) {
		this(rs);
		this.colNames = colNames;
	}

	/**
	 * Gets a String value for the column requested
	 * 
	 * @param rowNum
	 * @param colName
	 * @return
	 */
	public String getColumn(int rowNum, String colName) {
		return this.getRow(rowNum).get(colName);
	}

	/**
	 * Attempts to return the first row in a result set Null is returned if the
	 * result set size is not at least 1
	 * 
	 * @return
	 */
	public MyResultRow getRow() {
		return size > 0 ? this.getRow(0) : null;
	}

	/**
	 * Returns a MyResultRow for the requested row number
	 * 
	 * @param rowNum
	 * @return
	 */
	public MyResultRow getRow(int rowNum) {
		if (size == 0 || rowNum > size)
			return null;
		else
			return this.rs.get(rowNum);
	}

	/**
	 * Turns a SQL ResultSet into a MyResultSet
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static MyResultSet toMyResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		List<String> colNames = new ArrayList<String>();
		for (int i = 1; i <= cols; i++)
			colNames.add(rsmd.getColumnName(i).toLowerCase());

		List<MyResultRow> rows = new ArrayList<MyResultRow>();
		while (rs.next()) {
			MyResultRow columns = new MyResultRow();
			for (int i = 1; i <= cols; i++) {
				columns.add(rsmd.getColumnName(i).toLowerCase(), rs.getString(i));
			}
			rows.add(columns);
		}
		return new MyResultSet(rows, colNames);
	}

	/**
	 * Returns a list of the column names for the result set
	 * 
	 * @return
	 */
	public List<String> getColNames() {
		return colNames;
	}

	/**
	 * Creates an iterator for the current MyResultSet. It creates an iterator
	 * for the list of result rows;
	 */
	@Override
	public Iterator<MyResultRow> iterator() {
		return this.rs.iterator();
	}

	/**
	 * Turns the result set into a List of Class type.
	 * 
	 * The generic type T *MUST* have a constructor that takes a MyResultRow
	 * only.
	 * 
	 * @param type
	 *            The class to cast the list to
	 * @return List of Class type
	 */
	public <T extends DatabaseObject> List<T> toListOf(Class<T> type) {
		List<T> list = new ArrayList<>();
		for (MyResultRow r : rs) {
			list.add(r.construct(type));
		}
		return list;
	}

	@Override
	public String toString() {
		return "MyResultSet [rs=" + rs + ", colNames=" + colNames + ", size=" + size + "]";
	}
}

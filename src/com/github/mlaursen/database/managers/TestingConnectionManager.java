/**
 * 
 */
package com.github.mlaursen.database.managers;

import static com.github.mlaursen.database.utils.SqlFormatUtil.formatSqlForTesting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.utils.ClassUtil;

/**
 * This is an extension of the ConnectionManager. It has all the same methods but includes a few additional for testing. It can create
 * tables, sequences, packages, and views from existing Tables, packages, and views. The sequences always start from 0.
 * 
 * @author mlaursen
 * 
 */
public class TestingConnectionManager extends ConnectionManager {
	
	public TestingConnectionManager() {
		super();
	}
	
	/**
	 * Prints out a debugging message if debug is set to true
	 * 
	 * @param debug
	 *            Boolean if it should be printed
	 * @param msg
	 *            The message to print
	 */
	protected void debug(boolean debug, String msg) {
		if(debug)
			System.out.println(msg);
	}
	
	/**
	 * Creates a test_tableName and a seq_test_tableName_id in the database If copyData is true, it will copy all the data from the main
	 * tables.
	 * 
	 * @param tableName
	 *            The table name
	 * @param debug
	 *            Boolean if debugging statements should be printed
	 * @param copyData
	 *            Boolean if the data should be copied to the test tables
	 */
	public void createTestingTableAndSequence(String tableName, boolean debug, boolean copyData) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "CREATE TABLE test_" + tableName;
			sql += " AS SELECT * FROM " + tableName + (copyData ? "" : " WHERE 1=0");
			debug(debug, "Executing: " + sql);
			stmt.executeUpdate(sql);
			sql = "CREATE SEQUENCE SEQ_TEST_" + tableName + "_ID START WITH 0 MINVALUE 0 INCREMENT BY 1 NOCACHE";
			debug(debug, "Executing: " + sql);
			stmt.executeUpdate(sql);
		}
		catch(SQLException e) {
			ErrorCode c = ErrorCode.getErrorCode(e.getErrorCode());
			if(c.equals(ErrorCode.NAME_IN_USE)) {
				deleteTestingTableAndSequence(tableName, debug);
				createTestingTableAndSequence(tableName, debug, copyData);
			}
			else {
				handleSqlException(e, "create table ", new String[] { tableName });
			}
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
			debug(debug, "Creating tables and sequences has been completed.");
		}
	}
	
	/**
	 * Creates a testing package and testing package body for the packageName given. The List of classes given is to help format the package
	 * declaration source and appends test_ to all the classes.
	 * 
	 * {@link #recompile(boolean)} Should be used if there are problems with copying the packages. Since this executes the package
	 * declaration and package body at the same time, a referenced package or table might not be created before this was called.
	 * 
	 * @param packageName
	 *            The package name to copy
	 * @param classes
	 *            The list of classes to format for test within the package declaration source
	 * @param debug
	 *            Boolean if debugging statements should be printed
	 */
	public void createTestingPackage(String packageName, List<String> testingClasses, boolean debug) {
		debug(debug, "Testing classes: " + testingClasses);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT TEXT FROM USER_SOURCE WHERE TYPE='PACKAGE' AND UPPER(NAME)=UPPER('" + packageName + "') ORDER BY LINE";
			String sql2 = "SELECT TEXT FROM USER_SOURCE WHERE TYPE='PACKAGE BODY' AND UPPER(NAME)=UPPER('" + packageName
					+ "') ORDER BY LINE";
			debug(debug, "Executing Pacakge declaration: " + sql);
			stmt.execute(sql);
			rs = stmt.getResultSet();
			StringBuilder pkg = new StringBuilder("");
			StringBuilder pkgBody = new StringBuilder("");
			while(rs.next()) {
				pkg.append(rs.getString(1));
			}
			debug(debug, "Package declaration has been put into a string. The resulting string is:\n" + pkg.toString());
			closeResultSet(rs);
			debug(debug, "Closed the first result set.");
			debug(debug, "Executing package body declaration: " + sql2);
			stmt.execute(sql2);
			rs = stmt.getResultSet();
			while(rs.next()) {
				pkgBody.append(rs.getString(1));
			}
			debug(debug, "Package body declaration has been put into a string. The resulting string is:\n" + pkgBody.toString());
			String packageStr = "CREATE OR REPLACE " + formatSqlForTesting(pkg.toString(), testingClasses);
			String packageBody = "CREATE OR REPLACE " + formatSqlForTesting(pkgBody.toString(), testingClasses);
			debug(debug, "Executing the creation of package declaration:\n" + packageStr.toString());
			stmt.execute(packageStr);
			debug(debug, "Executing the creation of package body declaration:\n" + packageBody.toString());
			stmt.execute(packageBody);
		}
		catch(SQLException e) {
			handleSqlException(e, "create table ", new String[] { packageName });
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
			debug(debug, "Creating procedures has completed.");
		}
	}
	
	/**
	 * Creates a testing view from the table name given.
	 * 
	 * @param tableName
	 *            The table name to look for a view for
	 * @param classes
	 *            The list of classes to format the view with test_ appened to the front
	 * @param debug
	 *            Boolean if debugging statements should be printed
	 */
	public void createTestingView(String tableName, List<String> testingClasses, boolean debug) {
		String vName = tableName.toUpperCase();
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT TEXT FROM USER_VIEWS WHERE VIEW_NAME='" + vName + "'";
			debug(debug, "Executing: " + sql);
			stmt.execute(sql);
			rs = stmt.getResultSet();
			String view = "CREATE OR REPLACE VIEW " + vName + " AS ";
			debug(debug, "Appending the results to: " + view);
			while(rs.next()) {
				view += rs.getString(1);
			}
			debug(debug, "Create view sql string is now: " + view);
			view = formatSqlForTesting(view, testingClasses);
			debug(debug, "View sql string after being formatted: " + view);
			debug(debug, "Executing...");
			stmt.executeUpdate(view);
		}
		catch(SQLException e) {
			handleSqlException(e, "drop table ", new String[] { tableName });
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
	
	/**
	 * Deletes a testing view by the table name given
	 * 
	 * @param tableName
	 *            The tablename to delete the view for
	 * @param debug
	 *            Boolean if debugging statements should be printed
	 */
	public void deleteTestingView(String tableName, boolean debug) {
		String sql = "DROP VIEW TEST_" + tableName.toUpperCase();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			debug(debug, "Executing: " + sql);
			stmt.executeUpdate(sql);
		}
		catch(SQLException e) {
			handleSqlException(e, "drop table ", new String[] { tableName });
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
	
	/**
	 * Deletes the table and sequence for a table name given
	 * 
	 * @param tableName
	 *            The tablename
	 * @param debug
	 *            Boolean if debugging should be printed
	 */
	public void deleteTestingTableAndSequence(String tableName, boolean debug) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "DROP TABLE test_" + tableName;
			debug(debug, "Executing: " + sql);
			stmt.executeUpdate(sql);
			sql = "DROP SEQUENCE SEQ_TEST_" + tableName + "_ID";
			debug(debug, "Executing: " + sql);
			stmt.executeUpdate(sql);
		}
		catch(SQLException e) {
			handleSqlException(e, "drop table ", new String[] { tableName });
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
	
	/**
	 * Deletes the packages from testing
	 * 
	 * @param packageName
	 *            The package name to delete
	 * @param debug
	 *            Boolean if debugging should be printed
	 */
	public void deleteTestingPackage(String packageName, boolean debug) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "DROP PACKAGE test_" + packageName;
			debug(debug, "Executing: " + sql);
			stmt.executeUpdate(sql);
		}
		catch(SQLException e) {
			handleSqlException(e, "drop package ", new String[] { packageName });
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
	
	/**
	 * Recompiles every package and package body where the status is invalid
	 * 
	 * @param debug
	 *            Boolean if debugging information should be printed.
	 */
	public void recompile(boolean debug) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String base = "SELECT OBJECT_NAME FROM USER_OBJECTS WHERE STATUS='INVALID' AND OBJECT_TYPE='PACKAGE";
			String sql = base + "'";
			String sql2 = base + " BODY'";
			debug(debug, "Executing: " + sql);
			stmt.execute(sql);
			rs = stmt.getResultSet();
			while(rs.next()) {
				String recompile = "ALTER PACKAGE " + rs.getString(1) + " COMPILE";
				debug(debug, "Executing: " + recompile);
				stmt.executeUpdate(recompile);
			}
			debug(debug, "Closing the result set to prepare for compiling package bodies.");
			closeResultSet(rs);
			debug(debug, "Executing: " + sql2);
			stmt.execute(sql2);
			rs = stmt.getResultSet();
			while(rs.next()) {
				String recompile = "ALTER PACKAGE " + rs.getString(1) + " COMPILE BODY";
				debug(debug, "Executing: " + recompile);
				stmt.executeUpdate(recompile);
			}
		}
		catch(SQLException e) {
			handleSqlException(e, "recompiling packages ", new String[] { "packages" });
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
}

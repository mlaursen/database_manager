/**
 * 
 */
package com.github.mlaursen.database.testing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import com.github.mlaursen.database.ClassUtil;
import com.github.mlaursen.database.ConnectionManager;
import com.github.mlaursen.database.SqlFormatUtil;
import com.github.mlaursen.database.objects.DatabaseObject;

/**
 * @author mikkel.laursen
 *
 */
public class TestingConnectionManager extends ConnectionManager {
	
	public TestingConnectionManager() {
		super();
	}
	
	public void createTestingTableAndSequence(String tableName) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "CREATE TABLE test_" + tableName;
			sql += " AS SELECT * FROM " + tableName + " WHERE 1=0";
			stmt.executeUpdate(sql);
			sql = "CREATE SEQUENCE SEQ_TEST_" + tableName + "_ID START WITH 0 MINVALUE 0 INCREMENT BY 1 NOCACHE";
			stmt.executeUpdate(sql);
		}
		catch (SQLException e) {
			handleSqlException(e, "create table ", new String[] {tableName});
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
	
	public void createTestingPackage(String packageName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT TEXT FROM USER_SOURCE WHERE TYPE='PACKAGE' AND UPPER(NAME)=UPPER('" + packageName + "') ORDER BY LINE";
			String sql2 = "SELECT TEXT FROM USER_SOURCE WHERE TYPE='PACKAGE BODY' AND UPPER(NAME)=UPPER('" + packageName + "') ORDER BY LINE";
			stmt.execute(sql);
			rs = stmt.getResultSet();
			StringBuilder pkg = new StringBuilder("");
			StringBuilder pkgBody = new StringBuilder("");
			while(rs.next()) {
				pkg.append(rs.getString(1));
			}
			stmt.execute(sql2);
			closeResultSet(rs);
			rs = stmt.getResultSet();
			while(rs.next()) {
				pkgBody.append(rs.getString(1));
			}
			String packageStr = "CREATE OR REPLACE " + SqlFormatUtil.formatPackageDeclarationForTest(pkg.toString(), packageName);
			String packageBody = "CREATE OR REPLACE " + SqlFormatUtil.formatPackageDeclarationForTest(pkgBody.toString(), packageName);
			stmt.execute(packageStr);
			stmt.execute(packageBody);
			//System.out.println("\n====================================\n" + packageBody + "\n============================\n\n");
		}
		catch (SQLException e) {
			handleSqlException(e, "create table ", new String[] {packageName});
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
	
	public void createTestingView(String tableName, List<Class<? extends DatabaseObject>> classes) {
		String[] testingClasses = new String[classes.size()];
		for(int i = 0; i < testingClasses.length; i++) {
			testingClasses[i] = ClassUtil.formatClassName(classes.get(i)).replace("_View", "");
		}
		String vName = tableName.toUpperCase();
		String sql = "SELECT TEXT FROM USER_VIEWS WHERE VIEW_NAME='" + vName + "'";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
			rs = stmt.getResultSet();
			String view = "CREATE OR REPLACE VIEW " + vName + " AS ";
			while(rs.next()) {
				view += rs.getString(1);
			}
			//System.out.println(Arrays.asList(testingClasses));
			view = SqlFormatUtil.formatViewLine(view, testingClasses);
			//System.out.println(view);
			stmt.executeUpdate(view);
			//System.out.println(stmt.getWarnings());
		}
		catch (SQLException e) {
			handleSqlException(e, "drop table ", new String[] {tableName});
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
	
	public void deleteTestingView(String tableName) {
		String sql = "DROP VIEW TEST_" + tableName.toUpperCase();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		}
		catch (SQLException e) {
			handleSqlException(e, "drop table ", new String[] {tableName});
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
	
	public void deleteTestingTableAndSequence(String tableName) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "DROP TABLE test_" + tableName;
			stmt.executeUpdate(sql);
			sql = "DROP SEQUENCE SEQ_TEST_" + tableName + "_ID";
			stmt.executeUpdate(sql);
		}
		catch (SQLException e) {
			handleSqlException(e, "drop table ", new String[] {tableName});
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}
	
	public void deleteTestingPackage(String packageName) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "DROP PACKAGE test_" + packageName;
			stmt.executeUpdate(sql);
		}
		catch (SQLException e) {
			handleSqlException(e, "drop package ", new String[] {packageName});
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

}

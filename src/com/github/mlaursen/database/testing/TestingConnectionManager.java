/**
 * 
 */
package com.github.mlaursen.database.testing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.mlaursen.database.ConnectionManager;
import com.github.mlaursen.database.DatabaseObjectClassUtil;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.Package;

/**
 * @author mikkel.laursen
 *
 */
public class TestingConnectionManager extends ConnectionManager {
	
	public TestingConnectionManager() {
		super();
	}
	
	public boolean createTestingTable(String tableName) {
		boolean success = false;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "CREATE GLOBAL TEMPORARY TABLE test_" + tableName;
			sql += " AS SELECT * FROM " + tableName;
			success = stmt.executeUpdate(sql) > 0;
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
		return success;
	}
	
	public boolean createTestingPackage(String packageName) {
		boolean success = false;
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
			rs = stmt.getResultSet();
			while(rs.next()) {
				pkgBody.append(rs.getString(1));
			}
			String packageStr = "CREATE " + packageToTest(pkg, packageName);
			String packageBody = "CREATE " + packageToTest(pkgBody, packageName);
			stmt.execute(packageStr);
			stmt.execute(packageBody);
		}
		catch (SQLException e) {
			handleSqlException(e, "create table ", new String[] {packageName});
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
		return success;
	}
	
	public String packageToTest(StringBuilder pkg, String packageName) {
		String tableName = packageName.replace("_pkg", "");
		String s = pkg.toString().replaceAll("(?i)\\b"+packageName+"\\b", "test_"+packageName);
		s = s.replace("(?i)\\b"+tableName+"\\b", "test_"+tableName);
		return s;
	}
	
	public boolean deleteTestingTable(String tableName) {
		boolean success = false;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "DROP TABLE test_" + tableName;
			success = stmt.executeUpdate(sql) > 0;
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
		return success;
	}
	
	public boolean deleteTestingPackage(String packageName) {
		boolean success = false;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "DROP PACKAGE test_" + packageName;
			success = stmt.executeUpdate(sql) > 0;
		}
		catch (SQLException e) {
			handleSqlException(e, "drop table ", new String[] {packageName});
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeStatement(stmt);
			closeConnection(conn);
		}
		return success;
	}

}

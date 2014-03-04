/**
 * 
 */
package com.github.mlaursen.database;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Properties;

import oracle.jdbc.OracleTypes;

import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyClob;
import com.github.mlaursen.database.objects.MyResultSet;
import com.github.mlaursen.database.objects.Package;
import com.github.mlaursen.database.objects.Procedure;

/**
 * Utility class for interacting with databases. A dbconfig.properties file must
 * be located in {projectHomeDirectory}/config
 * 
 * 
 * @author mikkel.laursen
 * 
 */
public class ConnectionManager {
	private static enum ErrorCode {
		UNIQUE_CONSTRAINT(1), ARGUMENT_MISMATCH(6550);
		private int code;
		private ErrorCode(int code) {
			this.code = code;
		}
		
		public static ErrorCode getErrorCode(int code) {
			for(ErrorCode c : values()) {
				if(c.code == code)
					return c;
			}
			return null;
		}
	}
	
	protected String databaseName, databaseUser, databasePswd, classForName;

	public ConnectionManager() {
		try {
			Properties localProperties = new LocalSettings().getLocalSettings();
			databaseName = localProperties.getProperty(LocalSettings.DATABASE_NAME);
			databaseUser = localProperties.getProperty(LocalSettings.USERNAME);
			databasePswd = localProperties.getProperty(LocalSettings.PASSWORD);
			classForName = localProperties.getProperty(LocalSettings.CLASS_FOR_NAME);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a database connection from the localProperties file.
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(classForName);
		return DriverManager.getConnection(databaseName, databaseUser, databasePswd);
	}

	/**
	 * Takes in a package and a procedure name to call with an array of
	 * parameters
	 * 
	 * @param pkg
	 *            Package that holds a procedure to call
	 * @param procedureName
	 *            The procedure name to call from the package
	 * @param parameters
	 *            Array of parameters to be passed to the stored procedure
	 * @return True or false depending on if the stored procedure executed
	 *         successfully without errors
	 */
	public boolean executeStoredProcedure(Package pkg, String procedureName, Object... parameters) {
		return executeStoredProcedure(pkg.call(procedureName), parameters);
	}

	/**
	 * Takes in a procedure and an arrya of parameters to be passed to the
	 * stored procedure
	 * 
	 * @param p
	 *            The procedure to call
	 * @param parameters
	 *            An array of objects to be passed to the stored procedure
	 * @return True or false depending on if the stored procedure executed
	 *         successfully without errors
	 */
	public boolean executeStoredProcedure(Procedure p, Object... parameters) {
		return executeStoredProcedure(p.toString(), parameters);
	}

	/**
	 * Main grunt work for executing a stored procedure that can be successful
	 * or fail.
	 * 
	 * @param procedureName
	 *            Full procedure to be called, including parameters that should
	 *            be bound.
	 * @param parameters
	 *            Array of parameters to be bound to the procedure
	 * @return
	 */
	protected boolean executeStoredProcedure(String procedureName, Object... parameters) {
		boolean success = false;
		Connection conn = null;
		CallableStatement cs = null;
		try {
			conn = getConnection();
			cs = conn.prepareCall("{call " + procedureName + "}");
			for (int i = 1; i < parameters.length + 1; i++) {
				Object param = parameters[i - 1];
				bindWithDatatype(param, i, conn, cs);
			}
			success = cs.executeUpdate() > 0;
		}
		catch (SQLException e) {
			handleSqlException(e, procedureName, parameters);
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeCallableStatement(cs);
			closeConnection(conn);
		}
		return success;
	}
	
	protected void handleSqlException(SQLException e, String procedureName, Object[] parameters) {
		ErrorCode c = ErrorCode.getErrorCode(e.getErrorCode());
		if(c == null) {
			e.printStackTrace();
		}
		else {
			String msg = "There was a " + c.name() + " exception when calling " + procedureName + ".\n";
			msg += "\tThe parameters being passed were: " + Arrays.toString(parameters);
			e.printStackTrace();
			System.err.println(msg);
		}
	}

	/**
	 * Public method to execute a stored procedure that has a cursor as a return
	 * type. The package looks for the procedure name given and executes it.
	 * 
	 * @param pkg
	 *            The package that the procedure is in
	 * @param procedureName
	 *            The procedure to call
	 * @param parameters
	 *            An array of objects to be passed to the procedure. They will
	 *            be bound to some data types or the toString() method will be
	 *            called on them.
	 * @return
	 */
	public MyResultSet executeCursorProcedure(Package pkg, String procedureName, Object... parameters) {
		return executeCursorProcedure(pkg.call(procedureName), parameters);
	}

	/**
	 * Public method for executing a stored procedure that has a cursor as the
	 * return type. This version is only used if you are not using packages.
	 * This method calls the main handler with a formatted procedure name
	 * 
	 * @param procedure
	 *            The procedure to execute
	 * @param parameters
	 * @return
	 */
	public MyResultSet executeCursorProcedure(Procedure procedure, Object... parameters) {
		return executeCursorProcedure(procedure.toString(), parameters);
	}

	/**
	 * 
	 * @param procedureName
	 * @param parameters
	 * @return
	 */
	protected MyResultSet executeCursorProcedure(String procedureName, Object... parameters) {
		int cursorPos = parameters.length + 1;
		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		MyResultSet results = null;
		try {
			conn = getConnection();
			cs = conn.prepareCall("{call " + procedureName + "}");
			for (int i = 1; i <= parameters.length; i++) {
				Object p = parameters[i - 1];
				bindWithDatatype(p, i, conn, cs);
			}
			cs.registerOutParameter(cursorPos, OracleTypes.CURSOR);
			cs.execute();
			rs = (ResultSet) cs.getObject(cursorPos);
			results = MyResultSet.toMyResultSet(rs);
		}
		catch (SQLException e) {
			handleSqlException(e, procedureName, parameters);
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeResultSet(rs);
			closeCallableStatement(cs);
			closeConnection(conn);
		}
		return results;
	}

	/**
	 * Closes a database connection
	 * 
	 * @param conn
	 */
	protected void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes a SQL CallableStatement
	 * 
	 * @param cs
	 */
	protected void closeCallableStatement(CallableStatement cs) {
		if (cs != null) {
			try {
				cs.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void closeStatement(Statement s) {
		if(s != null) {
			try {
				s.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes a SQL ResultSet
	 * 
	 * @param rs
	 */
	protected void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Attempts to bind an object with it's data type into an oracle callable
	 * statment. If the object is not a Date, Integer, something that can be
	 * parsed as an integer, Double, MyClob, or DatabaseObject; the toString()
	 * method is called.
	 * 
	 * If the Object is a MyClob, an oracle Clob is created and the getValue()
	 * method is called on the object.
	 * 
	 * If the Object is a Database Object, then the primary key is bound.
	 * 
	 * @param p
	 *            Parameter to bind
	 * @param i
	 *            The index to bind to
	 * @param conn
	 *            A database connection
	 * @param cs
	 *            The callable statement to bind to
	 * @throws SQLException
	 */
	protected static void bindWithDatatype(Object p, int i, Connection conn, CallableStatement cs) throws SQLException {
		if (p instanceof Date) {
			cs.setDate(i, (Date) p);
		}
		else if (p instanceof Integer) {
			cs.setInt(i, (Integer) p);
		}
		else if(ClassUtil.canParseInt(p)) {
			cs.setInt(i, Integer.parseInt((String) p));
		}
		else if (p instanceof Double) {
			cs.setDouble(i, (Double) p);
		}
		else if (p instanceof MyClob) {
			Clob c = conn.createClob();
			c.setString(1, ((MyClob) p).getValue());
			cs.setClob(i, c);
		}
		else if (p instanceof DatabaseObject) {
			cs.setString(i, ((DatabaseObject) p).getPrimaryKey());
		}
		else if(p == null) {
			cs.setNull(i, Types.VARCHAR);
		}
		else {
			cs.setString(i, p.toString());
		}
	}
}

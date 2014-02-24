database_manager
================

Java implementation of a Database handler

1. Setup
2. Useage
3. Problems/Limitations


============
1. Setup
All that is required to use this Database handler is a file named 'dbconfig.properties' in a config folder that is in the class path.

The dbconfig.properties file needs to at least have these 4 properties: className, dbpswd, database, and dbuser. This is an example dbconfig.properties

className=oracle.jdbc.OracleDriver
dbpswd=password1
database=jdbc\:oracle\:thin\:@localhost\:1521\:xe
dbuser=test

============
2. Useage
The point of this manager is to create DatabaseObjects with field names that correspond to database columns.  The manager then creates the code to access the database in packages with stored procedures for each database object. The default database object has no callable procedures until you implement the database object types (Getable, GetAllable, Createable, Updateable, Deleteable). For each database object type that was implemented, a stored procedure will be added to the database object package. Annotations were added to help with this generation process.
@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.GET, DatabaseFieldType.GETALL, DatabaseFieldType.UPDATE, DatabaseFieldType.DELETE})
If you add the DatabaseField annotation before a field in a java class, the field will be added in order from top to bottom to the stored procedure as parameters.

The database object class has default implementations for the 5 basic database object types.
get(String primaryKey), getAll(), update(), delete(), create()
If a database object does not implement the Database Object Type, the procedure call does not execute and an error message is produced saying that the database object does not have that procedure.

A Database Object for this manager MUST always have a primary key.  The default primary key is an "id" field. You can manually set the primaryKeyName if it is a different column.  The primaryKey is automatically always added first to the update() and delete() procedures and it is the lookup key for the default get(String primaryKey).

You can also create your own stored procedures within a package if there are additional Stored Procedures that need to be used.

example:

import ...
class Test extends DatabaseObject implements Getable, Updateable {
  @DatabaseField(DatabaseFieldType.UPDATE)
  protected String value
  
  public Test() { ... }
  public Test(MyResultRow r) { super(r); ... }
  
  void setValue(MyResultRow r) { value = r.get("value"); }
  ...
}

Would create:
  A database package:
    TEST_PKG
    With stored procedures:
      GET(:PRIMARYKEY, :CURSOR)
      UPDATE(:PRIMARYKEY, :VALUE)
    
Please look at the examples for more detail.


===================
3. Problems/Limitations
The database manager is created EVERY time a DatabaseObject is created. It might be better to have a separate database manager for a database object
that can be filtered or get all of them.
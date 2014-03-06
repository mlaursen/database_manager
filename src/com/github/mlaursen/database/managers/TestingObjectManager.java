/**
 * 
 */
package com.github.mlaursen.database.managers;

import java.util.ArrayList;
import java.util.Arrays;

import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.DatabaseView;
import com.github.mlaursen.database.objects.Package;
import com.github.mlaursen.database.utils.ClassUtil;

/**
 * This is an extension of the Object Manager. The main difference is that it is using a TestingConnectionManager versus the connection
 * manager.
 * 
 * 
 * @author mlaursen
 * 
 */
public class TestingObjectManager extends ObjectManager {
	
	private TestingConnectionManager connectionManager;
	private boolean delete = true;
	private boolean debug = false;
	private boolean copyData = false;
	
	/**
	 * 
	 * @param delete
	 *            Boolean if the test data should be deleted after testing is complete
	 * @param debug
	 *            Boolean if deubgging info should be printed
	 * @param copyData
	 *            Boolean if all data should be copied from prod to test
	 * @param databaseObject
	 *            A database object to generate a package for
	 */
	public TestingObjectManager(boolean delete, boolean debug, boolean copyData, Class<? extends DatabaseObject> databaseObject) {
		this(databaseObject);
		this.delete = delete;
		this.debug = debug;
		this.copyData = copyData;
	}
	
	@SafeVarargs
	public TestingObjectManager(Class<? extends DatabaseObject>... databaseObjects) {
		super();
		connectionManager = new TestingConnectionManager();
		this.databaseObjects = databaseObjects.length > 0 ? Arrays.asList(databaseObjects)
				: new ArrayList<Class<? extends DatabaseObject>>();
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			addPackage(c);
		}
	}
	
	@Override
	public void addPackageWithView(Class<? extends DatabaseObject> baseClass, Class<? extends DatabaseView> view) {
		Package pkg = new Package(baseClass, true);
		this.databaseObjects.add(baseClass);
		this.databaseObjects.add(view);
		if(packageIsAvailable(pkg.getName())) {
			Package pkgOld = getPackage(pkg.getName());
			pkgOld.mergeProcedures(pkg);
		}
		else {
			this.addPackage(pkg);
		}
		if(debug) {
			System.out.println("Creating the Tables and Sequences for " + baseClass);
			connectionManager.createTestingTableAndSequence(ClassUtil.formatClassName(baseClass), debug, copyData);
			System.out.println("Creating Database View: " + view);
			connectionManager.createTestingView(ClassUtil.formatClassName(view), databaseObjects, debug);
			System.out.println("Creating Package for " + baseClass);
			connectionManager.createTestingPackage(Package.formatClassName(baseClass), databaseObjects, debug);
		}
		else {
			connectionManager.createTestingTableAndSequence(ClassUtil.formatClassName(baseClass), debug, copyData);
			connectionManager.createTestingView(ClassUtil.formatClassName(view), databaseObjects, debug);
			connectionManager.createTestingPackage(Package.formatClassName(baseClass), databaseObjects, debug);
		}
	}
	
	@Override
	public void addPackage(Class<? extends DatabaseObject> type) {
		Package pkg = new Package(type, true);
		this.databaseObjects.add(type);
		if(packageIsAvailable(pkg.getName())) {
			Package pkgOld = getPackage(pkg.getName());
			pkgOld.mergeProcedures(pkg);
		}
		else {
			this.addPackage(pkg);
		}
		if(ClassUtil.objectAssignableFrom(type, DatabaseView.class)) {
			if(debug) {
				System.out.println("Creating the test view " + type);
			}
			connectionManager.createTestingView(ClassUtil.formatClassName(type), databaseObjects, debug);
		}
		else {
			if(debug) {
				System.out.println("Creating the Tables, Sequences and Packages for " + type);
			}
			connectionManager.createTestingTableAndSequence(ClassUtil.formatClassName(type), debug, copyData);
			connectionManager.createTestingPackage(Package.formatClassName(type), databaseObjects, debug);
		}
	}
	
	@Override
	public <T extends DatabaseObject> boolean packageIsAvailable(Class<T> type) {
		return availablePackages.contains("test_" + Package.formatClassName(type));
	}
	
	@Override
	public <T extends DatabaseObject> Package getPackage(Class<T> type) {
		return packages.get(packageMap.get("test_" + Package.formatClassName(type)));
	}
	
	/**
	 * This deletes all the temporary tables and packages created
	 */
	public void cleanUp() {
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			if(delete) {
				String cName = ClassUtil.formatClassName(c);
				String pName = Package.formatClassName(c);
				if(ClassUtil.objectAssignableFrom(c, DatabaseView.class)) {
					if(debug)
						System.out.println("Deleteing test view " + c);
					
					connectionManager.deleteTestingView(cName, debug);
				}
				else {
					if(debug) {
						System.out.println("Deleteing sequences, packages and database table for " + c);
					}
					connectionManager.deleteTestingTableAndSequence(cName, debug);
					connectionManager.deleteTestingPackage(pName, debug);
				}
			}
		}
		
	}
	
	/**
	 * Recompiles all packages and package bodies that are invalid
	 */
	public void recompile() {
		if(debug) {
			System.out.println("Recompiling package bodies.");
			connectionManager.recompile(debug);
		}
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	
	public void setCopyData(boolean copyData) {
		this.copyData = copyData;
	}
}

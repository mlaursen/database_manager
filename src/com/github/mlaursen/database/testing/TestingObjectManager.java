/**
 * 
 */
package com.github.mlaursen.database.testing;

import java.util.ArrayList;
import java.util.Arrays;

import com.github.mlaursen.database.ClassUtil;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.DatabaseView;
import com.github.mlaursen.database.objects.ObjectManager;
import com.github.mlaursen.database.objects.Package;

/**
 * @author mikkel.laursen
 *
 */
public class TestingObjectManager extends ObjectManager {

	private TestingConnectionManager connectionManager;
	private boolean delete = true;
	private boolean debug = false;
	
	@SafeVarargs
	public TestingObjectManager(Class<? extends DatabaseObject>... databaseObjects) {
		super();
		connectionManager = new TestingConnectionManager();
		this.databaseObjects = databaseObjects.length > 0 ? Arrays.asList(databaseObjects) : new ArrayList<Class<? extends DatabaseObject>>();
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			addPackage(c);
		}
	}
	
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
			System.out.println("Creating Database View: " + view);
			System.out.println("Creating Package for " + baseClass);
		}
		connectionManager.createTestingTableAndSequence(ClassUtil.formatClassName(baseClass));
		connectionManager.createTestingView(ClassUtil.formatClassName(view), databaseObjects);
		connectionManager.createTestingPackage(Package.formatClassName(baseClass));
	}
	
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
			connectionManager.createTestingView(ClassUtil.formatClassName(type), databaseObjects);
		}
		else {
			if(debug) {
				System.out.println("Creating the Tables, Sequences and Packages for " + type);
			}
			connectionManager.createTestingTableAndSequence(ClassUtil.formatClassName(type));
			connectionManager.createTestingPackage(Package.formatClassName(type));
		}
	}
	
	@Override
	public <T extends DatabaseObject>  boolean packageIsAvailable(Class<T> type) {
		return availablePackages.contains("test_"+Package.formatClassName(type));
	}
	
	@Override
	public <T extends DatabaseObject> Package getPackage(Class<T> type) {
		return packages.get(packageMap.get("test_"+Package.formatClassName(type)));
	}
	
	/**
	 * This deletes all the temporary tables and packages created
	 */
	public void cleanUp() {
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			if(ClassUtil.objectAssignableFrom(c, DatabaseView.class)) {
				if(debug && delete) {
					System.out.println("Deleting test view " + c);
				}
				if(delete)
					connectionManager.deleteTestingView(ClassUtil.formatClassName(c));
			}
			else {
				if(debug && delete) {
					System.out.println("Deleteing all test data for " + c);
				}
				if(delete)
					connectionManager.deleteTestingTableAndSequence(ClassUtil.formatClassName(c));
					connectionManager.deleteTestingPackage(Package.formatClassName(c));
			}
		}
		
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
}

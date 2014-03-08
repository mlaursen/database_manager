/**
 * 
 */
package com.github.mlaursen.database.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	private List<String> testingClasses;
	
	/**
	 * 
	 * @param delete
	 *            Boolean if the data should be deleted on cleanup
	 * @param debug
	 *            Boolean if deubgging statements should be printed
	 * @param copyData
	 *            Boolean if the data from the main system should be copied to testing
	 * @param objects
	 *            Array of optional String for each object in testing. This is used in formatting the testing environment
	 */
	public TestingObjectManager(boolean delete, boolean debug, boolean copyData, String... objects) {
		this.delete = delete;
		this.debug = debug;
		this.copyData = copyData;
		this.packageMap = new HashMap<String, Integer>();
		this.packages = new ArrayList<Package>();
		this.availablePackages = new ArrayList<String>();
		this.connectionManager = new TestingConnectionManager();
		this.testingClasses = Arrays.asList(objects);
	}
	
	/**
	 * Creates a new testing object manager with debug = false delete = true copyData = false
	 * 
	 * @param objects
	 *            List of testing classes String
	 */
	public TestingObjectManager(List<String> objects) {
		this.debug = false;
		this.delete = true;
		this.copyData = false;
		this.testingClasses = objects;
	}
	
	@Override
	public void addPackageWithView(Class<? extends DatabaseObject> baseClass, Class<? extends DatabaseView> view) {
		Package pkgBase = new Package(baseClass, true);
		Package pkgView = new Package(view, true);
		pkgBase.mergeProcedures(pkgView);
		this.databaseObjects.add(baseClass);
		this.databaseObjects.add(view);
		if(packageIsAvailable(pkgBase.getName())) {
			Package pkgOld = getPackage(pkgBase.getName());
			pkgOld.mergeProcedures(pkgBase);
		}
		else {
			this.addPackage(pkgBase);
		}
		if(debug) {
			System.out.println("Creating the Tables and Sequences for " + baseClass);
			connectionManager.createTestingTableAndSequence(ClassUtil.formatClassName(baseClass), debug, copyData);
			System.out.println("Creating Database View: " + view);
			connectionManager.createTestingView(ClassUtil.formatClassName(view), testingClasses, debug);
			System.out.println("Creating Package for " + baseClass);
			connectionManager.createTestingPackage(Package.formatClassName(baseClass), testingClasses, debug);
		}
		else {
			connectionManager.createTestingTableAndSequence(ClassUtil.formatClassName(baseClass), debug, copyData);
			connectionManager.createTestingView(ClassUtil.formatClassName(view), testingClasses, debug);
			connectionManager.createTestingPackage(Package.formatClassName(baseClass), testingClasses, debug);
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
			connectionManager.createTestingView(ClassUtil.formatClassName(type), testingClasses, debug);
		}
		else {
			if(debug) {
				System.out.println("Creating the Tables, Sequences and Packages for " + type);
			}
			connectionManager.createTestingTableAndSequence(ClassUtil.formatClassName(type), debug, copyData);
			connectionManager.createTestingPackage(Package.formatClassName(type), testingClasses, debug);
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
			
		}
		connectionManager.recompile(debug);
	}
	
	/**
	 * Sets the debug boolean to the new boolean
	 * 
	 * @param debug
	 *            Boolean
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Sets the delete boolean to the new boolean
	 * 
	 * @param delete
	 *            Boolean
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	
	/**
	 * Sets the copyData boolean to the new boolean
	 * 
	 * @param copyData
	 *            Boolean
	 */
	public void setCopyData(boolean copyData) {
		this.copyData = copyData;
	}
}

/**
 * 
 */
package com.github.mlaursen.database.testing;

import com.github.mlaursen.database.ClassUtil;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.ObjectManager;
import com.github.mlaursen.database.objects.Package;

/**
 * @author mikkel.laursen
 *
 */
public class TestingObjectManager extends ObjectManager {

	private TestingConnectionManager connectionManager;
	/**
	 * @param databaseObjects
	 */
	@SafeVarargs
	public TestingObjectManager(Class<? extends DatabaseObject>... databaseObjects) {
		super();
		connectionManager = new TestingConnectionManager();
		this.databaseObjects = databaseObjects;
		int i = 0;
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			Package pkg = new Package(c, true);
			packages.add(pkg);
			availablePackages.add(pkg.getName());
			packageMap.put(pkg.getName(), i++);
			connectionManager.createTestingTable(ClassUtil.formatClassName(c));
			connectionManager.createTestingPackage(Package.formatClassName(c));
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
			connectionManager.deleteTestingTable(ClassUtil.formatClassName(c));
			connectionManager.deleteTestingPackage(Package.formatClassName(c));
		}
		
	}
}

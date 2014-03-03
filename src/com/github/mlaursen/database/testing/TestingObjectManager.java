/**
 * 
 */
package com.github.mlaursen.database.testing;

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
	/**
	 * @param databaseObjects
	 */
	@SafeVarargs
	public TestingObjectManager(Class<? extends DatabaseObject>... databaseObjects) {
		super();
		connectionManager = new TestingConnectionManager();
		this.databaseObjects = databaseObjects;
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			Package pkg = new Package(c, true);
			if(packageIsAvailable(pkg.getName())) {
				Package pkgOld = getPackage(pkg.getName());
				pkgOld.mergeProcedures(pkg);
			}
			else {
				this.addPackage(pkg);
			}
			if(ClassUtil.objectAssignableFrom(c, DatabaseView.class)) {
				System.out.println("Was a view.. haven't delt with that yet");
			}
			else {
				connectionManager.createTestingTable(ClassUtil.formatClassName(c));
				connectionManager.createTestingPackage(Package.formatClassName(c));
			}
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
			//connectionManager.deleteTestingView(ClassUtil.formatClassname(c));
			//connectionManager.deleteTestingPackage(Package.formatClassName(c));
		}
		
	}
}

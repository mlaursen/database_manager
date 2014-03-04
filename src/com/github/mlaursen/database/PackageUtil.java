/**
 * 
 */
package com.github.mlaursen.database;

import java.util.Arrays;

/**
 * @author mikkel.laursen
 *
 */
public class PackageUtil {
	
	public static final String UPDATE_IGNORE_CASE = "UPDATE";
	
	public static String formatPackageDeclarationForTest(String packageDeclaration, String packageName) { 
		return formatPackageDeclarationForTest(packageDeclaration, packageName); 
	}
	
	public static String formatPackageDeclarationForTest(String packageDeclaration, String packageName, String... ignoreCases) {
		packageDeclaration = packageDeclaration.toUpperCase();
		packageName = packageName.toUpperCase();
		String[] lines = packageDeclaration.split("\\r?\\n");
		StringBuilder formatted = new StringBuilder("");
		int l = lines.length;
		for(int i = 0; i < l; i++) {
			formatted.append(formatProcedureLine(lines[i], packageName.replace("_PKG", ""), ignoreCases) + (i+1==l ? "" : "\n"));
		}
		return formatted.toString();
	}
	public static String formatProcedureLine(String line, String tableName) { return formatProcedureLine(line, tableName); }
	public static String formatProcedureLine(String line, String tableName, String... ignoreCases) {
		line = line.toUpperCase();
		int lastIndex = 0;
		int startFrom = 0;
		String proc = "";
		while(lastIndex != -1) {
			lastIndex = line.indexOf(tableName, lastIndex);
			if(lastIndex != -1) {
				proc += line.substring(startFrom, lastIndex);
				if(isUpdate(line, tableName, lastIndex)) {
					proc += "TEST";
				}
				else if(!isIgnored(line, tableName, lastIndex, ignoreCases)) {
					proc += "TEST_";
				}
				proc += tableName;
				lastIndex += tableName.length();
				startFrom = lastIndex;
			}
		}
		proc += line.substring(startFrom);
		return proc;
	}
	
	public static boolean isUpdate(String line, String tableName, int lastIndex) {
		int l = UPDATE_IGNORE_CASE.length();
		int from = lastIndex-l;
		return from >= 0 && line.substring(from, lastIndex).contains(UPDATE_IGNORE_CASE);
	}
	
	public static boolean isIgnored(String line, String tableName, int lastIndex, String[] ignoreCases) {
		if(ignoreCases != null) {
			for(int i = 0; i < ignoreCases.length; i++) {
				String ignoreCase = ignoreCases[i].toUpperCase();
				if(ignoreCase.length() > 0) {
					int to = lastIndex + tableName.length() + ignoreCase.length();
					if(to <= line.length()) {
						if(line.substring(lastIndex, to).contains(ignoreCase)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}

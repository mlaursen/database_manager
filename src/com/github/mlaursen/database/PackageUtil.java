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
	
	public static String packageToTest(StringBuilder pkg, String packageName) {
		String tableName = packageName.replace("_pkg", "");
		String str = formatProcedureLine(pkg.toString().toUpperCase(), tableName.toUpperCase());
		
		/*
		String[] splits = pkg.toString().toUpperCase().split("(?i)"+tableName);
		StringBuilder str = new StringBuilder("");
		for(int i = 0; i < splits.length; i++) {
			if(i == 0) {
				str.append(splits[i]);
			}
			else {
				str.append(("TEST_" + tableName + splits[i]).toUpperCase());
			}
		}
		*/
		return str.toString();
	}
	
	public static String formatProcedureLine(String line, String tableName) {
		line = line.toUpperCase();
		String[] splits = line.split(""+tableName);
		String ret = "";
		for(int i = 0; i < splits.length; i++) {
			if(i == 0) {
				ret = splits[i] + "TEST_" + tableName;
			}
			else {
				if(splits[i].endsWith(".") && i+1 < splits.length) {
					ret += splits[i] + tableName;
				}
				else if(i+1 < splits.length){
					//System.out.println(splits[i]);
					ret += splits[i] + "TEST_" + tableName;
				}
				else ret += splits[i];
			}
		}
		return ret;
	}
}

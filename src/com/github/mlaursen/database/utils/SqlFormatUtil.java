/**
 * 
 */
package com.github.mlaursen.database.utils;

import java.util.Arrays;
import java.util.List;


/**
 * @author mlaursen
 * 
 */
public class SqlFormatUtil {
	
	/**
	 * Formats a sql create query to add TEST_ in front of all the tables and views and sequences.
	 * 
	 * @param sqlToChange
	 *            The string of sql to change
	 * @param testingClasses
	 *            The array of classes to append test_ to
	 * @return Formatted sql string
	 */
	public static String formatSqlForTesting(String sqlToChange, List<String> testingClasses) {
		sqlToChange = sqlToChange.toUpperCase();
		String[] lines = sqlToChange.split("\\r?\\n");
		String pkg = "";
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i];
			for(String s : testingClasses) {
				if(s.length() > line.trim().length())
					continue;
				
				s = s.toUpperCase();
				String regex = ClassUtil.combineWith(s.split("\\_"), "\\_");
				String myregex = "\\s(?=\\b"+regex+"(\\_VIEW|\\_PKG|[^_])?\\b)";
				String[] spaceSplits = line.split(myregex);
				String s2 = "";
				for(int j = 0; j < spaceSplits.length; j++) {
					s2 += (j == 0 ? "" : " TEST_") + spaceSplits[j];
				}
				String[] seqs = s2.split("(?=\\s)?SEQ\\_" + regex);
				s2 = "";
				for(int j = 0; j < seqs.length; j++) {
					s2 += (j == 0 ? "" : "SEQ_TEST_" + s) + seqs[j];
				}
				line = s2;
			}
			pkg += line + (i + 1 < lines.length ? "\n" : "");
		}
		return pkg;
	}
	/**
	 * Formats a sql create query to add TEST_ in front of all the tables and views and sequences.
	 * 
	 * @param sqlToChange
	 *            The string of sql to change
	 * @param testingClasses
	 *            The array of classes to append test_ to
	 * @return Formatted sql string
	 */
	public static String formatSqlForTesting(String sqlToChange, String[] testingClasses) {
		return formatSqlForTesting(sqlToChange, Arrays.asList(testingClasses));
	}
}

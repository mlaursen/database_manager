/**
 * 
 */
package com.github.mlaursen.database.utils;



/**
 * @author mikkel.laursen
 *
 */
public class SqlFormatUtil {
	/**
	 * Formats a sql create query to add TEST_ in front of all the tables and views and sequences.
	 * 
	 * @param packageDeclaration
	 * @param testingClasses
	 * @return
	 */
	public static String formatSqlForTesting(String packageDeclaration, String[] testingClasses) {
		packageDeclaration = packageDeclaration.toUpperCase();
		String[] lines = packageDeclaration.split("\\r?\\n");
		String pkg = "";
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i];
			for(String s : testingClasses) {
				if(s.length() > line.trim().length())
					continue;
				
				String regex = s.toUpperCase();
				String myregex = "\\s(?=" + regex + ")((?!"+regex+"\\_)|(?="+regex+"_PKG)|(?="+regex+"_VIEW))";
				String[] spaceSplits = line.split(myregex);
				String s2 = "";
				for(int j = 0; j < spaceSplits.length; j++) {
					s2 += (j == 0 ? "" : " TEST_") + spaceSplits[j];
				}
				String[] seqs = s2.split("\\sSEQ_"+regex);
				s2 = "";
				for(int j = 0; j < seqs.length; j++) {
					s2 += (j ==0 ? "" : " SEQ_TEST_" + regex) + seqs[j];
				}
				line = s2;
			}
			pkg += line + (i+1 < lines.length ? "\n" : "");
		}
		return pkg;
	}
}

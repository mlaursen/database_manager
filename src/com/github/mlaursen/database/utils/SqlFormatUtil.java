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
	 * Converts a create statement in sql for testing by prepending TEST_ before every key word given. The rule will be ignored if:
	 * KEYWORD_X KEYWORD,
	 * 
	 * However, the rule will be followed if: SEQ_KEYWORD_X KEYWORD_VIEW KEYWORD_PKG KEYWORD
	 * 
	 * @param sqlToChange
	 *            The sql to format for testing
	 * @param keyWords
	 *            A list of key words
	 * @return The new "test" creation sql string
	 */
	public static String formatSqlForTesting(String sqlToChange, List<String> keyWords) {
		sqlToChange = sqlToChange.toUpperCase();
		String[] lines = sqlToChange.split("\\r?\\n");
		String pkg = "";
		String words = "(";
		for(int i = 0; i < keyWords.size(); i++) {
			words += ClassUtil.combineWith(keyWords.get(i).split("\\_"), "\\_").toUpperCase();
			words += i + 1 < keyWords.size() ? "|" : "";
		}
		words += ")";
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String[] splits = line.split("\\s(?=\\b" + words + "((\\_VIEW|\\_PKG|[^_,])?)(?!,)\\b)");
			String s2 = "";
			for(int j = 0; j < splits.length; j++) {
				s2 += (j == 0 ? "" : " TEST_") + splits[j];
			}
			String[] seqs = s2.split("(?=\\s)?SEQ\\_(?=" + words + ")");
			s2 = "";
			for(int j = 0; j < seqs.length; j++) {
				s2 += (j == 0 ? "" : "SEQ_TEST_") + seqs[j];
			}
			line = s2;
			pkg += line + (i + 1 < lines.length ? "\n" : "");
		}
		return pkg;
	}
	
	/**
	 * Formats a sql create query to add TEST_ in front of all the tables and views and sequences.
	 * 
	 * @param sqlToChange
	 *            The string of sql to change
	 * @param keyWords
	 *            The array of key words to append test_ to
	 * @return Formatted sql string
	 */
	public static String formatSqlForTesting(String sqlToChange, String[] keyWords) {
		return formatSqlForTesting(sqlToChange, Arrays.asList(keyWords));
	}
}

/**
 * 
 */
package com.github.mlaursen.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utility functions used in the DatabaseManager project.
 * 
 * @author mikkel.laursen
 * 
 */
public class Util {
	public static final String DEF_REGEX = "(?=\\p{Upper})", DEF_COMBINE = "_";

	/**
	 * 
	 * @param c
	 *            The class to split the name for
	 * @param regex
	 *            Regex to split the name on. if it is null, the default regex
	 *            is chosen
	 * @param combineWith
	 *            The way to combine the split string. If it is null, the
	 *            defautl combine is chosen
	 * @return
	 */
	public static String formatClassName(Class<?> c, String regex, String combineWith) {
		String reg = regex == null ? DEF_REGEX : regex;
		String com = combineWith == null ? DEF_COMBINE : combineWith;
		return combineWith(splitOn(c.getSimpleName(), reg), com);
	}

	public static String combineWith(String[] strs) {
		return combineWith(strs, "_");
	}

	public static String combineWith(String[] strs, String with) {
		String s = "";
		for (int i = 0; i < strs.length; i++) {
			s += strs[i] + (i + 1 < strs.length ? with : "");
		}
		return s;
	}

	public static String[] splitOnUpper(String s) {
		String[] upps = splitOn(s, "(?=\\p{Upper})");
		return upps[0].equals("") ? Arrays.copyOfRange(upps, 1, upps.length) : upps;
	}

	public static String[] splitOn(String s, String regex) {
		return s.split(regex);
	}

	/**
	 * Creates a list of all classes that a subclass implements excluding
	 * Object.class
	 * 
	 * @param subclass
	 * @return
	 */
	public static List<Class<?>> getClassList(Class<?> subclass) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> currentClass = subclass;
		while (!currentClass.equals(Object.class)) {
			classes.add(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		Collections.reverse(classes);
		return classes;
	}

	/**
	 * Checks if an object can be parsed as an integer
	 * 
	 * @param i
	 * @return
	 */
	public static boolean canParseInt(Object i) {
		try {
			Integer.parseInt((String) i);
			return true;
		}
		catch (NumberFormatException | ClassCastException e) {
			return false;
		}
	}
}

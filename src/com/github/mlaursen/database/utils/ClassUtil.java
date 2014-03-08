/**
 * 
 */
package com.github.mlaursen.database.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.mlaursen.database.objects.DatabaseObject;

/**
 * Utility functions used in the DatabaseManager project.
 * 
 * @author mlaursen
 * 
 */
public class ClassUtil {
	
	public static final String DEF_REGEX = "(?=\\p{Upper})", DEF_COMBINE = "_";
	
	/**
	 * Formats a class based on the regex to split on and how to recombine the splits.
	 * 
	 * @param c
	 *            The class to split the name for
	 * @param regex
	 *            Regex to split the name on. if it is null, the default regex is chosen which is to split on uppercase
	 * @param combineWith
	 *            The way to combine the split string. If it is null, the default combine is chosen which is an underscore
	 * @return A string of the formatted class name
	 */
	public static String formatClassName(Class<?> c, String regex, String combineWith) {
		String reg = regex == null ? DEF_REGEX : regex;
		String com = combineWith == null ? DEF_COMBINE : combineWith;
		return combineWith(splitOn(c.getSimpleName(), reg), com);
	}
	
	/**
	 * {@link #formatClassName(Class, String, String)} Formats a class name with the {@value #DEF_REGEX} and {@value #DEF_COMBINE}
	 * 
	 * @param c
	 *            The class to format
	 * @return A string with the formatted class name
	 */
	public static String formatClassName(Class<?> c) {
		return combineWith(splitOn(c.getSimpleName(), DEF_REGEX), DEF_COMBINE);
	}
	
	/**
	 * Takes an array of string and combines them with {@value #DEF_COMBINE}
	 * 
	 * @param strs
	 *            Array of strings
	 * @return A single string
	 */
	public static String combineWith(String[] strs) {
		return combineWith(strs, DEF_COMBINE);
	}
	
	/**
	 * Takes an array of string and combines them with the given string
	 * 
	 * @param strs
	 *            Array of strings to combine
	 * @param with
	 *            The string to combine with
	 * @return A single string
	 */
	public static String combineWith(String[] strs, String with) {
		String s = "";
		for(int i = 0; i < strs.length; i++) {
			s += strs[i] + (i + 1 < strs.length ? with : "");
		}
		return s;
	}
	
	/**
	 * Splits a string on upper case {@link #splitOn(String, String)}
	 * 
	 * @param s
	 *            The string to split
	 * @return An array of the splits and removes the first split if it is an empty string
	 */
	public static String[] splitOnUpper(String s) {
		String[] upps = splitOn(s, DEF_REGEX);
		return upps[0].equals("") ? Arrays.copyOfRange(upps, 1, upps.length) : upps;
	}
	
	/**
	 * Splits a string on the regex given
	 * 
	 * @param s
	 *            The string to split
	 * @param regex
	 *            The regex to split the string with
	 * @return An array of the splits and removes the first split if it is an empty string
	 */
	public static String[] splitOn(String s, String regex) {
		String[] upps = s.split(regex);
		return upps[0].equals("") ? Arrays.copyOfRange(upps, 1, upps.length) : upps;
	}
	
	/**
	 * Creates a list of all classes that a subclass implements excluding Object.class
	 * 
	 * @param subclass
	 *            A subclass to get the super classes for
	 * @return A list of classes
	 */
	public static List<Class<?>> getClassList(Class<?> subclass) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> currentClass = subclass;
		while(!currentClass.equals(Object.class)) {
			classes.add(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		Collections.reverse(classes);
		return classes;
	}
	
	/**
	 * Checks if a class is assignable from another class. Example for memory. The Deleteable interface extends the NoCursor interface. To
	 * check if an interface is a NoCursor, objectAssignableFrom(Deleteable.class, NoCursor.class)
	 * 
	 * @param c1
	 *            The sub class
	 * @param c2
	 *            The super class
	 * @return True if the super class is assignable from the sub class
	 */
	public static boolean objectAssignableFrom(Class<?> c1, Class<?> c2) {
		return c2.isAssignableFrom(c1);
	}
	
	/**
	 * Checks if an object can be parsed as an integer
	 * 
	 * @param i
	 *            The object to check
	 * @return True if it can be parsed as an integer
	 */
	public static boolean canParseInt(Object i) {
		try {
			Integer.parseInt((String) i);
			return true;
		}
		catch(NumberFormatException | ClassCastException e) {
			return false;
		}
	}
	
	/**
	 * Checks if a Database Object is able to call the procedureType. This is to make sure that a view and database object can't call each
	 * other's procedures if they are not implemented for that class
	 * 
	 * @param type
	 *            The database object
	 * @param procedureType
	 *            The procedure interface
	 * @return True if the procedure interface is assignable from the database object
	 */
	public static <T extends DatabaseObject> boolean isClassCallable(Class<T> type, Class<?> procedureType) {
		return objectAssignableFrom(type, procedureType);
	}
}

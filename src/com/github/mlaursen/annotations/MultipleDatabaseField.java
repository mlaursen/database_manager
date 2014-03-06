/**
 * 
 */
package com.github.mlaursen.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mlaursen
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MultipleDatabaseField {
	/**
	 * 
	 * @return An array of DatbaseFieldType for which procedure types the
	 *         multiple database fields should be generated for
	 */
	DatabaseFieldType[] values() default { DatabaseFieldType.GET };

	/**
	 * 
	 * @return String of names to be used in procedure generation
	 */
	String[] names();
}

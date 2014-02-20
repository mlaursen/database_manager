/**
 * 
 */
package com.github.mlaursen.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mikkel.laursen
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MultipleDatabaseField {
	DatabaseFieldType[] values() default {DatabaseFieldType.GET};
	String[] names();
}

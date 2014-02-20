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
public @interface DatabaseField {
	/**
	 * Array of DatabaseFieldType to be applied to the field.
	 * The default is an array of just the GET type
	 * This and postion() are two linked arrays, the order of both matter
	 * @return
	 */
	DatabaseFieldType[] values();
	boolean reorder() default false;
	
	int getPosition() default -1;
	int getAllPosition() default -1;
	int filterPosition() default -1;
	int createPosition() default -1;
	int deletePosition() default -1;
	int updatePosition() default -1;
}

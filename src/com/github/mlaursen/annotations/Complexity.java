/**
 * 
 */
package com.github.mlaursen.annotations;

/**
 * @author mikkel.laursen
 *
 */
public @interface Complexity {
	ComplexityLevel[] value() default {ComplexityLevel.GETABLE};
}

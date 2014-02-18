/**
 * 
 */
package com.github.mlaursen.annotations.processors;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.github.mlaursen.annotations.Complexity;

/**
 * @author mikkel.laursen
 *
 */
@SupportedAnnotationTypes("com.github.mlaursen.annotations.Complexity")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ComplexityProcessor extends AbstractProcessor {
	public ComplexityProcessor() {
		super();
	}
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for(Element elem : roundEnv.getElementsAnnotatedWith(Complexity.class)) {
			Complexity c = elem.getAnnotation(Complexity.class);
			String message = "annotation foudn in " + elem.getSimpleName() + " with complexity " + c.value();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
		}
		return true;
	}

}

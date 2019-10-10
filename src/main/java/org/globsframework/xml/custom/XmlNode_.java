package org.globsframework.xml.custom;

import org.globsframework.metamodel.GlobType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.FIELD})

public @interface XmlNode_ {
    String name() default "";

    boolean mandatory() default false;

    GlobType TYPE = _XmlAsNode.TYPE;

}

package uk.ac.standrews.cs.storr.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by al on 21/11/14.
 * This Annotation is used to label a (static) fieldname as being of some reference type
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LXP_REF {
    /**
     * @return the name of the type that the reference is to - as specified in the TypeFactory
     */
    String type();
}

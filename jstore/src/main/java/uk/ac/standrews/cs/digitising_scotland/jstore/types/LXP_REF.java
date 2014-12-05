package uk.ac.standrews.cs.digitising_scotland.jstore.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by al on 21/11/14.
 * <p/>
 * This Annotation is used to label a (static) fieldname as being of some reference type
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LXP_REF {
    /**
     * the name of the type that the reference is to - as specified in the TypeFactory
     */
    String type();
}

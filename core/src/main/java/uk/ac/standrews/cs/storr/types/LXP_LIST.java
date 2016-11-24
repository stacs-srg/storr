package uk.ac.standrews.cs.storr.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * *
 * This Annotation is used to label a (static) fieldname as being of a base type
 * Created by al on 21/11/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LXP_LIST {
    LXPBaseType type();
}

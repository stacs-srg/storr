package uk.ac.standrews.cs.digitising_scotland.jstore.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * *
 * This Annotation is used to label a (static) fieldname as being of a base type
 * <p/>
 * Created by al on 21/11/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LXP_SCALAR {
    LXPBaseType type();
}

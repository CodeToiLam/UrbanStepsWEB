package vn.urbansteps.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark controller methods that should always be logged in Admin action logs,
 * even if they are exposed via GET (state-changing toggles, etc).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AdminAuditable {
    String value() default "";
}

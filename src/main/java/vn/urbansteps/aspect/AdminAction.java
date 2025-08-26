package vn.urbansteps.aspect;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminAction {
    String action();
    String description() default "";
}

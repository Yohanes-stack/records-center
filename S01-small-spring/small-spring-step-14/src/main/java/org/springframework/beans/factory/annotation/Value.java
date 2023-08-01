package org.springframework.beans.factory.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * "#{systemProperties.myProp}".
     * @return
     */
    String value();
}

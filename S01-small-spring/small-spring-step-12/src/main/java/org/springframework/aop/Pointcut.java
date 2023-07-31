package org.springframework.aop;

public interface Pointcut {

    /**
     * Result the classFilter for this pointcut.
     * @return
     */

    ClassFilter getClassFilter();

    /**
     * Return the methodMatcher for this pointcut.
     * @return
     */
    MethodMatcher getMethodMatcher();
}

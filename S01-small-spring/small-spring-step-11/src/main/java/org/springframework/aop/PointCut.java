package org.springframework.aop;

public interface PointCut {

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

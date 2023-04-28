package com.rc;

public class AMatch implements Match{
    @Override
    public boolean test(Object obj) {
        System.out.println("AMatch 被执行了");
        return false;
    }
}

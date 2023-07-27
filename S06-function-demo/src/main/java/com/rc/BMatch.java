package com.rc;

public class BMatch implements Match{
    @Override
    public boolean test(Object obj) {
        System.out.println("BMatch 被执行了");
        return true;
    }
}

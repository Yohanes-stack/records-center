package com.rc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public static int[] sortThree(int[] arr){
        for (int min=0,max=arr.length-1;min<max;min++,max--){
            int temp=arr[min];
            arr[min]=arr[max];
            arr[max]=temp;
        }
        return arr;
    }
    @Test
    public void test(){
        Arrays.stream(sortThree(new int[]{1, 2, 3, 4, 5, 6})).forEach(System.out::println);
    }

}
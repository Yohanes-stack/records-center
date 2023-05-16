package com.rc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    boolean[] vfs;
    int num =0;

    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        int n = rooms.size();
        vfs = new boolean[n];
        dfs(rooms,0);
        return num ==n;
    }
    public void dfs(List<List<Integer>> rooms,int index){
        vfs[index] = true;
        num++;
        List<Integer> list = rooms.get(index);
        for(Integer n : list){
            if(!vfs[n]){
                dfs(rooms,n);
            }
        }
    }
    @Test
    public void a(){
        merge(new int[]{1,2,3,0,0,0},3,new int[]{2,4,5},3);
    }
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int[] mergeNum = new int[m+n];
        int index = 0;
        for(int i =0;i<m;i++){
            mergeNum[index++] = nums1[i];
        }
        for(int i =0;i<n;i++){
            mergeNum[index++] = nums2[i];
        }
        nums1 = mergeNum;
        Arrays.sort(nums1);
        for (int i : nums1) {
            System.out.println(i);
        }
    }
    @Test
    public void test(){
        List<List<Integer>> rooms = new ArrayList<>();
        List<Integer> room1 = new ArrayList<>();
        List<Integer> room2 = new ArrayList<>();
        List<Integer> room3 = new ArrayList<>();
        List<Integer> room4 = new ArrayList<>();
        room1.add(1);
        room2.add(2);
        room3.add(3);
        rooms.add(room1);
        rooms.add(room2);
        rooms.add(room3);
        rooms.add(room4);
        System.out.println(canVisitAllRooms(rooms));
    }

}
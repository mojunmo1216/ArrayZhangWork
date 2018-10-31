package com.arrayzhang.leetcode.simple;

import android.text.TextUtils;

/**
 * LeetCode
 * Created by zhangrui on 2018/5/10.
 */
public class StringSum {
    public int strStr(String haystack, String needle) {
        if(needle == null ||needle.equals("")){
            return 0;
        }
        int stack = haystack.length();
        int need = needle.length();
        if(stack < need){
            return -1;
        }
        for(int i= 0;i<= stack-need;i++){
            char s = haystack.charAt(i);
            if(s == needle.charAt(0)){
                int num = 1;
                for(int j=1; j<need; j++){
                    if(needle.charAt(j)!= haystack.charAt(i+j)){
                        break;
                    }
                    num++;
                }
                if(num == need){
                    return i;
                }
            }
        }
        return -1;
    }
}

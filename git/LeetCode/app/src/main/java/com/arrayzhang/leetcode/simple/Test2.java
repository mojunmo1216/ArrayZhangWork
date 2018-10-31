package com.arrayzhang.leetcode.simple;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode
 * Created by zhangrui on 2018/4/27.
 */
//反转整数
public class Test2 {
    /*
    * 给定一个 32 位有符号整数，将整数中的数字进行反转
    * 假设我们的环境只能存储 32 位有符号整数，其数值范围是 [−231,  231 − 1]。根据这个假设，如果反转后的整数溢出，则返回 0。
    * */
    public int reverse(int x) {
        long z = 0;
        long num = 1;
        List<Integer> list = new ArrayList<>();
        while (x != 0){
            list.add(x%10);
            x = x/10;
        }
        for(int i = list.size()-1;i >= 0 ; i--){
            z=z+list.get(i)*num;
            num = num*10;
            Log.e("zr_log","z:"+z);
        }
        int y = z <= Integer.MAX_VALUE && z >=Integer.MIN_VALUE ? (int)z : 0;
        Log.e("zr_log","y:"+y);
        return y;
    }

    /*
    *
    *   请编写一个函数，其功能是将输入的字符串反转过来。
    *
    * */

    public String reverseString(String s) {
        StringBuffer sb = new StringBuffer();
        for(int i=s.length()-1;i >= 0;i--){
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

}

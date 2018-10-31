package com.arrayzhang.leetcode.simple;

import java.util.Arrays;

/**
 * LeetCode
 * Created by zhangrui on 2018/5/7.
 */
public class Array {

   /* 输入: [1,2,3,4,5,6,7] 和 k = 3
    输出: [5,6,7,1,2,3,4]
    解释:
    向右旋转 1 步: [7,1,2,3,4,5,6]
    向右旋转 2 步: [6,7,1,2,3,4,5]
    向右旋转 3 步: [5,6,7,1,2,3,4]
    */

    public void rotate(int[] nums, int k) {
        int len = nums.length;
        int num = k%len;
        if(num > 0){
            int[] array = new int[len];
            System.arraycopy(nums,len-num,array,0,num);
            System.arraycopy(nums,0,array,num,len-num);
            System.out.println(array);
            nums = array;
            System.out.println(nums);
        }
    }

  /*  给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。

    说明：

    你的算法应该具有线性时间复杂度。 你可以不使用额外空间来实现吗？*/

    public int singleNumber(int[] nums) {
        int n = nums[0];
        for(int i = 1;i<nums.length;i++){
            n ^= nums[i];//使用异或运算符进行运算
        }
        return n;
    }

  /*  给定一个整数数组，判断是否存在重复元素。

    如果任何值在数组中出现至少两次，函数返回 true。如果数组中每个元素都不相同，则返回 false。*/

    public boolean containsDuplicate(int[] nums) {
        if(nums.length <= 1){
            return false;
        }
        Arrays.sort(nums);
        for(int i = 0 ;i< nums.length-1;i++){
            if(nums[i]==nums[i++]){
                return true;
            }
        }
        return false;
    }

/*  给定一个非负整数组成的非空数组，在该数的基础上加一，返回一个新的数组。

    最高位数字存放在数组的首位， 数组中每个元素只存储一个数字。

    你可以假设除了整数 0 之外，这个整数不会以零开头。

    示例 1:

    输入: [1,2,3]
    输出: [1,2,4]
    解释: 输入数组表示数字 123。*/

    public int[] plusOne(int[] digits) {
        int carry = 1;
        int len = digits.length;
        for(int i = len-1;i >=0;i--){
            int val =(digits[i]+carry)%10;
            carry = (digits[i]+carry)/10;
            digits[i] = val;
        }
        if(carry > 0){
            int[] nums = new int[len+1];
            nums[0] = carry;
            System.arraycopy(digits,0,nums,1,len);
            return nums;
        }
        return digits;
    }

/*
    给定一个数组 nums, 编写一个函数将所有 0 移动到它的末尾，同时保持非零元素的相对顺序。

    例如， 定义 nums = [0, 1, 0, 3, 12]，调用函数之后， nums 应为 [1, 3, 12, 0, 0]。

    注意事项:

    必须在原数组上操作，不要为一个新数组分配额外空间。
    尽量减少操作总数。*/

    public void moveZeroes(int[] nums) {
        int num = 0,len = nums.length;
        if(len < 2){
            return;
        }
        for(int i = 0;i<len;i++){
            if(nums[i] != 0){
                nums[num] = nums[i];
                num++;
            }
        }
        for(;num<len;num++){
            nums[num] = 0;
        }
    }
}

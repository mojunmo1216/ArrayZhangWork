package com.arrayzhang.leetcode.simple;


import android.text.TextUtils;

import java.util.Stack;

public class Test1 {

	/*	给定一个整数数组和一个目标值，找出数组中和为目标值的两个数。

        你可以假设每个输入只对应一种答案，且同样的元素不能被重复利用。*/
	
	 public int[] twoSum(int[] nums, int target) {
		 int[] sums = new int[2]; 
		 for(int i = 0;i <nums.length-1;i++){
			 for(int j = i+1;j < nums.length;j++){
				 int num = nums[i]+nums[j];
				 if(num == target){
					 sums[0]=nums[i];
					 sums[1]=nums[j];
					 return sums;
				 }
			 }
		 }
		 return null;
	 }

/*	给定一个排序数组，你需要在原地删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。

	不要使用额外的数组空间，你必须在原地修改输入数组并在使用 O(1) 额外空间的条件下完成。*/

	public int removeDuplicates(int[] nums) {
		int num = 0;
		for(int i = 0;i < nums.length-1;i++){
			if(nums[i] != nums[i+1]){
				num++;
				nums[num] = nums[i+1];
			}
		}
		return num+1;
	}


	/*
	* 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
		有效字符串需满足：
		左括号必须用相同类型的右括号闭合。
		左括号必须以正确的顺序闭合。
		注意空字符串可被认为是有效字符串。
	*
	* */

	public boolean isValid(String s) {
		if(TextUtils.isEmpty(s)){
			return true;
		}
		String left = "([{";
		String right = ")]}";
		Stack<String> mStacks = new Stack<>();
		for(int i=0; i<s.length(); i++){
			String value = s.charAt(i)+"";
			if(left.contains(value)){
				mStacks.push(value);
			}else if(right.contains(value)) {
				if(mStacks.peek()==null)return false;
				int l = left.indexOf(mStacks.peek());
				int r = right.indexOf(value);
				if(l==r){
					mStacks.pop();
				}else {
					return false;
				}
			}
		}
		return mStacks.empty();
	}

}

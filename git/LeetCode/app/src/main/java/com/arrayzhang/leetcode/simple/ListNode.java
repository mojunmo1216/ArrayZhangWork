package com.arrayzhang.leetcode.simple;

import com.arrayzhang.leetcode.middle.NodeSum;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode
 * Created by zhangrui on 2018/5/14.
 */
public class ListNode {

    int val;
    ListNode next;
    ListNode (int x){
        val = x;
    }


/*    请编写一个函数，使其可以删除某个链表中给定的（非末尾的）节点，您将只被给予要求被删除的节点。

    比如：假设该链表为 1 -> 2 -> 3 -> 4  ，给定您的为该链表中值为 3 的第三个节点，那么在调用了您的函数之后，该链表则应变成 1 -> 2 -> 4 。*/

    public void deleteNode(ListNode node) {
        ListNode next=node.next;
        node.val = next.val;
        node.next = next.next;
    }

/*
    给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。

    示例：

    给定一个链表: 1->2->3->4->5, 和 n = 2.

    当删除了倒数第二个节点后，链表变为 1->2->3->5.*/

    public ListNode removeNthFromEnd(ListNode head, int n) {
        if(n < 1){
            return head;
        }
        List<ListNode> list = new ArrayList<>();
        list.add(head);
        while(head.next != null){
            head = head.next;
            list.add(head);
        }
        if(list.size() >= n){
            ListNode delete = list.get(list.size()-n);
            if(delete.next == null){
                list.get(list.size()-n-1).next = null;
            }else{
                delete.val = delete.next.val;
                delete.next = delete.next.next;
            }
            list.remove(delete);
        }

        return list.get(0);
    }

    /*
    * 将两个有序链表合并为一个新的有序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。
    *   输入：1->2->4, 1->3->4
    *   输出：1->1->2->3->4->4
    *
    * */

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode head = new ListNode(0);
        ListNode nextNode = head;
        while (l1 != null || l2 != null){
            if(l1.val <= l2.val){
                nextNode.next = l1;
                l1 = l1 != null ? l1.next : null;
            }else {
                nextNode.next = l2;
                l2 = l2 != null ? l2.next : null;
            }
            nextNode = nextNode.next;
        }

        return head.next;

    }

}

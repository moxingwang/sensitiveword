package top.moxingwang.sensitiveword;

import top.moxingwang.sensitiveword.core.SensitivewordEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: MoXingwang 2019-07-31 09:40
 **/
public class Test {
    public static void main(String[] args) {
        Set<String> list1 = new HashSet<>();
        for (int i = 0; i < 100000; i++) {
            list1.add(i+"铭感词"+i);
        }


        Set<String> list2 = new HashSet<>();
        list2.add("你的");



        SensitivewordEngine.add(1,list1);
        SensitivewordEngine.add(2,list2);

        System.out.println(SensitivewordEngine.getSensitiveWord(1,"我的哈哈返我的543回东萨",2));;
        System.out.println(SensitivewordEngine.getSensitiveWord(2,"我的哈哈返我的543回东萨",2));;
    }
}

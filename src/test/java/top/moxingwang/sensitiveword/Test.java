package top.moxingwang.sensitiveword;

import top.moxingwang.sensitiveword.core.SensitivewordEngine;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: MoXingwang 2019-07-31 09:40
 **/
public class Test {
    public static void main(String[] args) {
        Set<String> list1 = new HashSet<>();
        list1.add("回东");
        list1.add("我的");
        list1.add("的");

        Set<String> list2 = new HashSet<>();
        list2.add("你的");


        SensitivewordEngine.add(1, list1);
        SensitivewordEngine.add(2, list2);

        System.out.println(SensitivewordEngine.getSensitiveWord(1, "我的哈哈返我的543回东萨", 1));

        System.out.println(SensitivewordEngine.getSensitiveWord(2, "我的哈哈返我的543回东萨", 1));

    }
}

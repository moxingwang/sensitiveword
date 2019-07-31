package top.moxingwang.sensitiveword;

import top.moxingwang.sensitiveword.core.KeywordEngine;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: MoXingwang 2019-07-31 09:40
 **/
public class Test {
    public static void main(String[] args) {
//        Set<String> list1 = new HashSet<>();
//        list1.add("回东");
//        list1.add("我的");
//        list1.add("我门的");
//        list1.add("的");

        Set<String> list2 = new HashSet<>();
        list2.add("你的");
        list2.add("你的是");


//        KeywordEngine.add(1, list1);
        KeywordEngine.add(2, list2);
        KeywordEngine.remove(2, "你的");
        System.out.println(1);

//        System.out.println(KeywordEngine.containWord(2,"我"));
//        System.out.println(KeywordEngine.containWord(1,"我的"));
//        System.out.println(KeywordEngine.listWord(1, "我的哈的哈返我的543回东萨"));
//        System.out.println(KeywordEngine.listWord(2, "我的哈哈返我的543回东萨"));

    }
}

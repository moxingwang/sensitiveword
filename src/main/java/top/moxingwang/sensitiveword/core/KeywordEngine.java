package top.moxingwang.sensitiveword.core;

/**
 * @description:
 * @author: MoXingwang 2019-07-29 13:09
 **/

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KeywordEngine {
    private static final String WORD_END_FLAG = "ISEND";
    /**
     * 敏感词库,可以存储多个业务场景(桶)
     */
    public static Map<Integer, Map> lexicon = new ConcurrentHashMap<>();

    /**
     * 是否包含敏感词
     */
    public static boolean containWord(Integer bucketId, String txt) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            int matchFlag = checkWord(bucketId, txt, i);
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 获取敏感词内容
     */
    public static Set<String> listWord(Integer bucketId, String txt) {
        Set<String> keyWordList = new HashSet<String>();

        for (int i = 0; i < txt.length(); i++) {
            int length = checkWord(bucketId, txt, i);
            if (length > 0) {
                keyWordList.add(txt.substring(i, i + length));
                i = i + length - 1;
            }
        }

        return keyWordList;
    }


    /**
     * 检查敏感词数量
     */
    private static int checkWord(Integer bucketId, String keyword, int beginIndex) {
        Map<String, Map> keyWordMap = lexicon.get(bucketId);
        if (keyWordMap == null) {
            return 0;
        }

        boolean flag = false;
        int matchFlag = 0;

        Map nowMap = keyWordMap;
        for (int i = beginIndex; i < keyword.length(); i++) {
            char word = keyword.charAt(i);
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                matchFlag++;
                if ("1".equals(nowMap.get(WORD_END_FLAG))) {
                    flag = true;
                    break;
                }
            } else {
                break;
            }
        }
        if (!flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

    /**
     * 删除关键词
     */
    public static synchronized void remove(Integer bucketId, String keyword) {
        Map<String, Map> nowMap = lexicon.get(bucketId);
        if (nowMap == null || keyword == null) {
            return;
        }

        int length = keyword.length();

        LinkedList<Map> linkedList = new LinkedList<>();
        for (int i = 0; i < length; i++) {
            char word = keyword.charAt(i);
            nowMap = (Map) nowMap.get(word);

            if (nowMap != null) {
                Map charMap = new HashMap();
                charMap.put(word, nowMap);
                linkedList.add(charMap);
            } else {
                break;
            }
        }

        Map lastCharMap = (Map) linkedList.getLast().get(keyword.charAt(length - 1));
        if (realNodeSize(lastCharMap) == 1) {
            if ("1".equals(lastCharMap.get(WORD_END_FLAG))) {
                lastCharMap.put(WORD_END_FLAG, "0");
            }
        } else {
            int stopIndex = linkedList.size();
            for (int i = linkedList.size() - 2; i >= 0; i--) {
                Map tmp = linkedList.get(i);
                if ("1".equals(tmp.get(WORD_END_FLAG)) || realNodeSize(tmp) > 1) {
                    break;
                } else {
                    stopIndex--;
                }
            }

            Map canStopMap = (Map) linkedList.get(stopIndex).get(keyword.charAt(stopIndex));
            canStopMap.remove(keyword.charAt(stopIndex + 1));
            lastCharMap.put(WORD_END_FLAG, "1");
        }
    }

    private static int realNodeSize(Map map) {
        int size = map.keySet().size();
        if (map.get(WORD_END_FLAG) != null) {
            size--;
        }
        return size;
    }

    /**
     * 封装敏感词库
     */
    public static synchronized void add(Integer bucketId, Set<String> keyWordSet) {
        Map<String, Map> keyWordMap = lexicon.get(bucketId);
        if (keyWordMap == null) {
            keyWordMap = new HashMap<>(keyWordSet.size());
            lexicon.put(bucketId, keyWordMap);
        }

        for (String str : keyWordSet) {
            Map nowMap = keyWordMap;
            for (int i = 0; i < str.length(); i++) {
                char keyChar = str.charAt(i);
                Object wordMap = nowMap.get(keyChar);
                if (null != wordMap) {
                    nowMap = (Map) wordMap;
                } else {
                    Map newWorMap = new HashMap<>();
                    newWorMap.put(WORD_END_FLAG, "0");

                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == str.length() - 1) {
                    nowMap.put(WORD_END_FLAG, "1");
                }
            }
        }
    }
}

package top.moxingwang.sensitiveword.core;

/**
 * @description:
 * @author: MoXingwang 2019-07-29 13:09
 **/

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SensitivewordEngine {
    private static final String WORD_END_FLAG = "ISEND";
    /**
     * 敏感词库,可以存储多个业务场景(桶)
     */
    public static Map<Integer, Map> lexicon = new ConcurrentHashMap<>();

    /**
     * 只过滤最小敏感词
     */
    public static int minMatchTYpe = 1;

    /**
     * 过滤所有敏感词
     */
    public static int maxMatchType = 2;

    /**
     * 敏感词库敏感词数量
     *
     * @return
     */
    public static int getWordSize(String bucketId) {
        if (lexicon.get(bucketId) == null) {
            return 0;
        }
        return lexicon.get(bucketId).size();
    }

    /**
     * 是否包含敏感词
     *
     * @param txt
     * @param matchType
     * @return
     */
    public static boolean isContaintSensitiveWord(String txt, int matchType) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            int matchFlag = checkSensitiveWord(txt, i, matchType);
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 获取敏感词内容
     *
     * @param txt
     * @param matchType
     * @return 敏感词内容
     */
    public static Set<String> getSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet<String>();

        for (int i = 0; i < txt.length(); i++) {
            int length = checkSensitiveWord(txt, i, matchType);
            if (length > 0) {
                // 将检测出的敏感词保存到集合中
                sensitiveWordList.add(txt.substring(i, i + length));
                i = i + length - 1;
            }
        }

        return sensitiveWordList;
    }

    /**
     * 替换敏感词
     *
     * @param txt
     * @param matchType
     * @param replaceChar
     * @return
     */
    public static String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
        String resultTxt = txt;
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 替换敏感词内容
     *
     * @param replaceChar
     * @param length
     * @return
     */
    private static String getReplaceChars(String replaceChar, int length) {
        String resultReplace = replaceChar;
        for (int i = 1; i < length; i++) {
            resultReplace += replaceChar;
        }

        return resultReplace;
    }

    /**
     * 检查敏感词数量
     *
     * @param txt
     * @param beginIndex
     * @param matchType
     * @return
     */
    public static int checkSensitiveWord(String txt, int beginIndex, int matchType) {
        boolean flag = false;
        // 记录敏感词数量
        int matchFlag = 0;
        char word = 0;
        Map nowMap = SensitivewordEngine.sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            // 判断该字是否存在于敏感词库中
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                matchFlag++;
                // 判断是否是敏感词的结尾字，如果是结尾字则判断是否继续检测
                if ("1".equals(nowMap.get("isEnd"))) {
                    flag = true;
                    // 判断过滤类型，如果是小过滤则跳出循环，否则继续循环
                    if (SensitivewordEngine.minMatchTYpe == matchType) {
                        break;
                    }
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
     * 封装敏感词库
     */
    private void add(Integer bucketId, Set<String> keyWordSet) {
        Map<String, Map> sensitiveWordMap = lexicon.get(bucketId);
        if (sensitiveWordMap == null) {
            sensitiveWordMap = new HashMap<>(keyWordSet.size());
            lexicon.put(bucketId, sensitiveWordMap);
        }

        for (String str : keyWordSet) {
            Map nowMap = sensitiveWordMap;
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

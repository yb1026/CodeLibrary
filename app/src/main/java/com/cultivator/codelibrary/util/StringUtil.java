package com.cultivator.codelibrary.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author xiaoping.shan
 */
public class StringUtil {

    /**
     * 判断一个字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || " ".equals(str)
                || "[]".equals(str)) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return (str != null && str.length() > 0);
    }


    public static boolean isJsonEmpty(String str) {
        if (isEmpty(str)) {
            return true;
        }
        if (str.equals("\"\"")) {
            return true;
        }
        return false;
    }

    /**
     * 保留前后位,替换字符串
     *
     * @param contentStr 被操作字符串
     * @param replaceStr 替换字符串
     * @param startCount 保留开始位
     * @param endCount   保留结束位
     */
    public static String replaceAndkeepStartEnd(String contentStr,
                                                String replaceStr, int startCount, int endCount) {
        if (isEmpty(contentStr)) {
            return replaceStr;
        } else if (contentStr.length() <= startCount + endCount) {
            int len = contentStr.length();
            if (len <= endCount) {
                // String stStr = contentStr.substring(0, 1);
                String stStr = contentStr.substring(len - 1, len);
                return replaceStr + stStr;
            } else {
                String stStr = contentStr.substring(len - endCount, len);
                return replaceStr + stStr;
            }
        } else {
            String stStr = contentStr.substring(0, startCount);
            String enStr = contentStr.substring(contentStr.length() - endCount);
            return stStr + replaceStr + enStr;
        }
    }

    /**
     * 保留前后位,替换字符串
     *
     * @param contentStr 被操作字符串
     * @param replaceStr 替换字符串
     * @param replaceCnt 字符替换次数(小于等于0时替换1次)
     * @param startCount 保留开始位数
     * @param endCount   保留开始位数
     * @return
     */
    public static String replaceAndkeepStartEnd(String contentStr,
                                                String replaceStr, int replaceCnt, int startCount, int endCount) {
        if (replaceCnt <= 0) {
            replaceCnt = 1;
        }
        StringBuffer sb = new StringBuffer("");
        String startStr = "";
        String endStr = "";
        if (contentStr.length() <= startCount + endCount) {
            int len = contentStr.length();
            if (len <= endCount) {
                endStr = contentStr.substring(len - 1, len);
            } else {
                endStr = contentStr.substring(len - endCount, len);
            }
        } else {
            startStr = contentStr.substring(0, startCount);
            endStr = contentStr.substring(contentStr.length() - endCount);
        }
        sb.append(startStr);
        for (int i = 0; i < replaceCnt; i++) {
            sb.append(replaceStr);
        }
        sb.append(endStr);
        return sb.toString();
    }

    /**
     * @param source          原字符串
     * @param replaceTagerStr 要替换的字符
     * @param replaceStr      替换后的字符
     * @return
     */
    public static String replaceString(String source, String replaceTagerStr,
                                       String replaceStr) {
        if (source == null) {
            return "";
        }
        int index = source.indexOf(replaceTagerStr);
        if (index == -1) {
            return source;
        } else {
            return source.replaceAll(replaceTagerStr, replaceStr);
        }
    }

    /**
     * 替换异常中的公司信息
     *
     * @param message
     * 异常信息
     * @return
     */
    private static final String tag1 = "和付";
    private static final String tag2 = "和付公司";
    private static final String companytag = "湖南银联";

    /**
     * 替换异常
     *
     * @param message
     * @return
     */
    public static String replacecompanyInformation(String message) {
        String resultStr = message;
        resultStr = replaceString(resultStr, tag2, companytag);
        resultStr = replaceString(resultStr, tag1, companytag);
        return resultStr;
    }

    /**
     * @param jo
     * @param key
     * @param defaultStr
     * @return
     */
    public static String getJsonString(JSONObject jo, String key,
                                       String defaultStr) {
        String value = defaultStr;
        try {
            value = jo.getString(key);
        } catch (JSONException e) {
            return value;
        }
        return value;

    }

    public static String getJsonString(JSONObject jo, String key) {
        return getJsonString(jo, key, "");
    }

    private final static String AMOUNT_FORMAT_SPLIT = "%1$,03.2f";

    private final static String AMOUNT_FORMAT_NO_SPLIT = "%1$03.2f";

    /**
     * 金额格式化,带逗号分隔符(保留两位小数)
     *
     * @param amountStr
     * @return exp:1,000,000.00
     */
    public static String formatAmountWithSplit(String amountStr) {
        return formatAmount(amountStr, AMOUNT_FORMAT_SPLIT);
    }

    /**
     * 金额格式化,不带逗号分隔符(保留两位小数)
     *
     * @param amountStr
     * @return exp:1000000.00
     */
    public static String formatAmountNoSplit(String amountStr) {
        return formatAmount(amountStr, AMOUNT_FORMAT_NO_SPLIT);
    }

    private static String formatAmount(String amountStr, String format) {
        String result = null;
        if (amountStr != null && amountStr.length() > 0) {
            //判断传入字符为大于等于0的数值(整数或小数)
            Pattern pt = Pattern.compile("^\\d+(.\\d+)?$");
            Matcher m = pt.matcher(amountStr);
            if (m.matches()) {
                result = String.format(format, new BigDecimal(amountStr));
            } else {
                System.out
                        .println(String
                                .format("====>ERROR: FormatAmount Input String [amountStr=%s] Format is Error. Unaccepted!",
                                        amountStr));
            }
        }
        return result;
    }


    /**
     * 判断是否为手机号码
     *
     * @param numberStr
     * @return
     */
    public static boolean isMobileNumber(String numberStr) {
        if (numberStr == null || numberStr.length() != 11) {
            return false;
        }
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(numberStr);
        return m.matches();
    }

    /**
     * 判断是否为座机号码
     *
     * @param numberStr
     * @return
     */
    public static boolean isLandlineNumber(String numberStr) {
        if (numberStr == null || numberStr.length() != 12 ) {
            return false;
        }
        String a = numberStr.substring(0,4);
        if (!a.equals("0731")) {
            return false;
        }
        return true;
    }

    /**
     * 校验身份证
     * @param s_aStr
     * @return
     */
    public static boolean isIdCard(String s_aStr) {
        Pattern p15 = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
        Pattern p18 = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$");
        if (p15.matcher(s_aStr).matches() || p18.matcher(s_aStr).matches()) {
            return true;
        }
        return false;
    }

    public static String userNameReplaceWithStar(String userName) {
        String userNameAfterReplaced = "";

        if (userName == null){
            userName = "";
        }

        int nameLength = userName.length();

        if (nameLength <= 1) {
            userNameAfterReplaced = "*";
        } else if (nameLength == 2) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\d{0})\\d(?=\\d{1})");
        } else if (nameLength >= 3 && nameLength <= 6) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\d{1})\\d(?=\\d{1})");
        } else if (nameLength == 7) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\d{1})\\d(?=\\d{2})");
        } else if (nameLength == 8) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\d{2})\\d(?=\\d{2})");
        } else if (nameLength == 9) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\d{2})\\d(?=\\d{3})");
        } else if (nameLength == 10) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\d{3})\\d(?=\\d{3})");
        } else if (nameLength >= 11) {
            userNameAfterReplaced = replaceAction(userName, "(?<=\\d{3})\\d(?=\\d{4})");
        }

        return userNameAfterReplaced;

    }

    /**
     * 实际替换动作
     *
     * @param username username
     * @param regular  正则
     * @return
     */
    private static String replaceAction(String username, String regular) {
        return username.replaceAll(regular,"*");
    }

    /**
     * 身份证号替换，保留前四位和后四位
     *
     * 如果身份证号为空 或者 null ,返回null ；否则，返回替换后的字符串；
     *
     * @param idCard 身份证号
     * @return
     */
    public static String idCardReplaceWithStar(String idCard) {

        if (idCard.isEmpty() || idCard == null) {
            return null;
        } else {
            return replaceAction(idCard, "(?<=\\d{4})\\d(?=\\d{4})");
        }
    }

    /**
     * 银行卡替换，保留后四位
     *
     * 如果银行卡号为空 或者 null ,返回null ；否则，返回替换后的字符串；
     *
     * @param bankCard 银行卡号
     * @return
     */
    public static String bankCardReplaceWithStar(String bankCard) {

        if (bankCard.isEmpty() || bankCard == null) {
            return null;
        } else {
            return replaceAction(bankCard, "(?<=\\d{0})\\d(?=\\d{4})");
        }
    }



}

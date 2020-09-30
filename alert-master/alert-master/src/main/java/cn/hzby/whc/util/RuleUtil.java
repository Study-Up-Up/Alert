package cn.hzby.whc.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleUtil {

    //确认控制得到的运算符再跟数据库中的运算符比较
    public static String getOperator(String leftResult,String rightResult){
        StringBuilder stringBuilder=new StringBuilder();
        if (Double.parseDouble(leftResult)<Double.parseDouble(rightResult)){
             stringBuilder.append("<");
        }
        if (Double.parseDouble(leftResult)>Double.parseDouble(rightResult)){
            stringBuilder.append(">");
        }
        if (Double.parseDouble(leftResult)==Double.parseDouble(rightResult)){
            stringBuilder.append("=");
        }
        return stringBuilder.toString();
    }

    /**
     * 使用正则表达式来判断字符串中是否包含字母
     * @param str 待检验的字符串
     * @return 返回是否包含
     * true: 包含字母 ;false 不包含字母
     */
    public static boolean judgeContainsStr(String str) {
        String regex=".*[a-zA-Z]+.*";
        Matcher m= Pattern.compile(regex).matcher(str);
        return m.matches();
    }


    // 判断一个字符串是否都为数字
    public static boolean judgeContainsCode(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher matcher = pattern.matcher((CharSequence)strNum);
        return matcher.matches();
    }
}

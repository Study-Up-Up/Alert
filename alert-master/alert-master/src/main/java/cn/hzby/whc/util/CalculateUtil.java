package cn.hzby.whc.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

//计算钉钉机器人字符串值
public class CalculateUtil {

    public static String getExpression(String expression){
        //计算字符串数值表达式可以用 javax.script.ScriptEngine#eval
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
            String result = String.valueOf(scriptEngine.eval(expression));
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

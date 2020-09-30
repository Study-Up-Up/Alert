package cn.hzby.whc.util;



import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

//JSON转换HashMap
public class JsonToHashMap {

    //将json字符串转换成HashMap<String,String>
    public static HashMap<String, String> toHashMap(String JsonStrin){
        HashMap<String, String> data = new HashMap<String, String>();
        try{
            // 将json字符串转换成jsonObject
            JSONObject jsonObject = JSONObject.fromObject(JsonStrin);
            @SuppressWarnings("rawtypes")
            Iterator it = jsonObject.keys();
            // 遍历jsonObject数据，添加到Map对象
            while (it.hasNext())
            {
                String key = String.valueOf(it.next());
                String value = jsonObject.get(key).toString();
                data.put(key, value);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        //打印控制台数据
        //System.out.println(data);
        return data;
    }

}

package cn.hzby.whc.util;


import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import org.slf4j.LoggerFactory;

//钉钉机器人发送信息完成服务
public class DbRootSendUtil {

    //打印服务台日志
    private static final org.slf4j.Logger Logger= LoggerFactory.getLogger(DbRootSendUtil.class);

    /*
     * 1.定义一个带有参数send(可扩展参数列表)
     * 2.发送消息给钉钉机器人，指定他人获取信息
     * 3.这种方式是钉钉第一种加密方式(直接发送信息的TEXT)
     */
    public static void robotSendText(String msg) {
        try{
            Long timestamp=System.currentTimeMillis();
            DingTalkClient client = new DefaultDingTalkClient();
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("");
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent("来了一条新消息："+msg);
            request.setText(text);
            //OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();    
            //at.setIsAtAll(true);
            //request.setAt(at);
            OapiRobotSendResponse response = client.execute(request);
            Logger.info(""+response.getBody());
        } catch (Exception e){
            e.fillInStackTrace();
            Logger.info("测试程序出错误了");
        }
    }

    /*
     * 1.定义一个带有参数send(可扩展参数列表)
     * 2.发送消息给钉钉机器人，指定他人获取信息
     * 3.这种方式是钉钉第二种加密方式(直接发送信息的LINK)
     */
    public static void robotSendLink(String msg){
        try{
            Long timestamp=System.currentTimeMillis();
            DingTalkClient client = new DefaultDingTalkClient();
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("");
            OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
            link.setMessageUrl("");
            link.setPicUrl("");
            link.setTitle("");
            link.setText(msg);
            request.setLink();

            OapiRobotSendResponse response = client.execute(request);
            Logger.info(""+response.getBody());
        } catch (Exception e){
            e.fillInStackTrace();
            Logger.info("测试程序出错误了");
        }
    }
}

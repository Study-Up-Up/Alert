package cn.hzby.whc.util;


import cn.hzby.whc.entity.RobotTimeAndSignEntity;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

//获取密钥签名
public class DdWebhookUtil {


    //根据当前时间戳获取签名
    public static RobotTimeAndSignEntity getSign(Long timestamp) {
        try {
            RobotTimeAndSignEntity robotTimeAndSignEntity =new RobotTimeAndSignEntity();
            String secret = "";
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), ""));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            robotTimeAndSignEntity.setTimestamp(timestamp);
            robotTimeAndSignEntity.setSign(sign);
            //得到实时的时间戳（被加载过的）与密钥Sign标志
            return robotTimeAndSignEntity;
        }catch (Exception e){
            e.fillInStackTrace();
            return null;
        }
    }
}

package cn.hzby.whc.entity;


/**
 * 钉钉机器人url的时间戳和sign标志，发送需要得到相应实体，代码拆分过后的结果
 */
public class RobotTimeAndSignEntity {

    private long timestamp;
    private String sign;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}

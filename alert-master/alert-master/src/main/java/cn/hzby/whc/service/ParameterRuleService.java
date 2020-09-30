package cn.hzby.whc.service;


import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ParameterRuleService {

    //取出全部规则以Map的方式存储到Redis中
    public void setAllRulesToRedis() throws Exception;

    //取出全部规则组合以List的方式存在Redis中
    public void setRuleCombinationToRedis() throws Exception;

    //建立一个Map存放对应机器的参数值
    public void setMachine(Map<String,String> entity)  throws Exception;

    //删除Redis中Map的指定Key值
    public void deleteRedisMsgMapKey(String key) throws Exception;

    //Redis分布式锁实时控制每一个key
    public void realTimeRedis(String dn) throws Exception;
}

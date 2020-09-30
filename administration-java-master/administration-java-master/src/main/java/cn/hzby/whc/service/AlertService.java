package cn.hzby.whc.service;


import cn.hzby.whc.entity.Alert;
import cn.hzby.whc.entity.AlertMsg;
import cn.hzby.whc.entity.AlertRule;
import cn.hzby.whc.requsetBody.RuleComRequest;
import com.alibaba.druid.wall.WallSQLException;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public interface AlertService {

    //分页获取所有的规则
    PageInfo<Alert> getAllRules(int page, int count) throws Exception;

    //取出全部规则组合以List的方式存在Redis中
    void setRuleCombinationToRedis() throws Exception;

    //获取组合规则
    Map<String,Object> getRuleCombination(int page, int count) throws Exception;

    //分页获取所有失败信息
    PageInfo<AlertMsg> getErrorAll(int page, int count) throws Exception;

    //插入组合规则
    void insertRules(String ruleTitle,String va, String machineList, String note)throws Exception;

    //更新组合规则
    void updateRules(RuleComRequest ruleComRequest) throws Exception;

    //删除组合规则(组合规则主键Id)
    void deleteRules(Long id) throws Exception;

    //控制Redis的组合规则的插入
    void insertRedisComRules(AlertRule alertRule) throws Exception;

    //控制Redis的单个规则的插入与更新，两者共用的
    void insertRedisRules(List<Alert> alertList) throws Exception;

    //控制Redis的单个规则的删除
    void deleteRedisRules(String ruleId)throws Exception;

    //控制机器名-组合规则的Map
    void insertMachinesMapRules(AlertRule alertRule) throws Exception;

}

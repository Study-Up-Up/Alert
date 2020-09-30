package cn.hzby.whc.serviceImpl;

import cn.hzby.whc.entity.Alert;
import cn.hzby.whc.entity.AlertMsg;
import cn.hzby.whc.entity.AlertRule;
import cn.hzby.whc.mapperService.AlertMapper;
import cn.hzby.whc.mapperService.AlertMsgMapper;
import cn.hzby.whc.mapperService.AlertRuleMapper;
import cn.hzby.whc.service.ParameterRuleService;
import cn.hzby.whc.util.CalculateUtil;
import cn.hzby.whc.util.DbRootSendUtil;
import cn.hzby.whc.util.RedisUtil;
import cn.hzby.whc.util.RuleUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class ParameterRuleServiceImpl implements ParameterRuleService {

    //打印控制台
    private static final Logger LOGGER= LoggerFactory.getLogger(ParameterRuleServiceImpl.class);

    //指定资源导入(规则表)
    @Resource(name = "alertMapper")
    AlertMapper alertMapper;

    //指定资源导入(规则信息表)
    @Resource(name = "alertMsgMapper")
    AlertMsgMapper alertMsgMapper;

    //Redisson服务导入(Redis的子集分布式锁)
    @Resource(name = "redissonClient")
    RedissonClient redissonClient;

    //指定资源导入(规则组合表)
    @Resource(name = "alertRuleMapper")
    AlertRuleMapper alertRuleMapper;

    //定义规则组合存储时的key
    private static final String ruleCombination="ruleCombinationKey";

    //定义全部规则存储时的key
    private static final String rules="rulesKey";

    //定义全部规则存储时的key
    private static final String machines="machinesKey";

    //定义全部规则存储时的key
    private static final String machineKeys="machinesRules";



    //取出全部规则以Map的方式存储到Redis中
    @Override
    public void setAllRulesToRedis() throws Exception{
        //从数据库中取出全部规则
        List<Alert> alertList=alertMapper.selectAllRules();
        //判断数据中是否不是null
        if (alertList!=null && alertList.size()>0){
            //创建用户获取分布式锁的key
            final String lockName=new StringBuffer(rules).append(System.currentTimeMillis()).toString();
            //获取分布式锁实例
            RLock rLock=redissonClient.getLock(lockName);
            //可重入锁的方式获取分布式锁
            Boolean suo=rLock.tryLock(100,10, TimeUnit.MINUTES);
            try{
                if (suo) {
                    //定义Redisson中的RMap数据结构
                    RMap<String,Alert> rMap=redissonClient.getMap(rules);
                    //先清空缓存中的数据
                    rMap.clear();
                    //循环遍历当前规则放入到Map中
                    for (Alert alert : alertList) {
                        //在Redisson中Map导入对应的规则
                        rMap.putIfAbsent(alert.getId().toString(), alert);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //释放锁
                if (rLock!=null){
                    rLock.forceUnlock();
                }
            }
        }
        LOGGER.info("加载了全部规则");
    }

    //取出全部规则组合以List的方式存在Redis中
    @Override
    public void setRuleCombinationToRedis() throws Exception {
        //从数据库中取出全部规则
        List<AlertRule> alertRuleList=alertRuleMapper.selectRuleCombination();
        //判断数据中是否不是null
        if (alertRuleList!=null && alertRuleList.size()>0){
            //创建用户获取分布式锁的key
            final String lockName=new StringBuffer(ruleCombination).append(System.currentTimeMillis()).toString();
            //获取分布式锁实例
            RLock rLock=redissonClient.getLock(lockName);
            //可重入锁的方式获取分布式锁
            Boolean suo=rLock.tryLock(100,10, TimeUnit.MINUTES);
            try{
                if (suo) {
                    //获取Redisson的列表组件RList实例
                    RList<AlertRule> rList=redissonClient.getList(ruleCombination);
                    //先清空缓存中的列表数据
                    rList.clear();
                    //将得到最新的列表组合更新至缓存中
                    rList.addAll(alertRuleList);
                    //定义Redisson中的RMap数据结构
                    RMap<String,String> rMap=redissonClient.getMap(machineKeys);
                    //先清空缓存中的列表数据
                    rMap.clear();
                    //以机器名-组合规则循环遍历到Map中
                    for (AlertRule alertRule:rList){
                        if (alertRule!=null) {
                            //拆分机器名字
                            String[] machines = alertRule.getAlertMachines().split(",");
                            for (String machine : machines) {
                                if (machine != null && rMap.get(machine)!=null) {
                                    //在Redisson中Map导入对应的规则
                                    rMap.put(machine,rMap.get(machine)+JSON.toJSON(alertRule)+"-");
                                }else {
                                    rMap.put(machine,JSON.toJSON(alertRule)+"-");
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //释放锁
                if (rLock!=null){
                    rLock.forceUnlock();
                }
            }
        }
        LOGGER.info("加载了组合规则到Redis中");
    }

    @Override
    public void setMachine(Map<String,String> entity) throws Exception {
        //创建用户获取分布式锁的key
        final String lockName=new StringBuffer(machines).append(System.currentTimeMillis()).toString();
        //获取分布式锁实例
        RLock rLock=redissonClient.getLock(lockName);
        //可重入锁的方式获取分布式锁
        Boolean suo=rLock.tryLock(100,10, TimeUnit.MINUTES);
        try{
            if (suo) {
                //获取key-value的值列表
                RMap<String, Map<String,String>> rMap=redissonClient.getMap(machines);
                //分布式锁竞争插入
                rMap.putIfAbsent(entity.get("dn"),entity);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //释放锁
            if (rLock!=null){
                rLock.forceUnlock();
            }
        }
    }

    //删除Redis中Map的指定Key值
    @Override
    public void deleteRedisMsgMapKey(String keys) throws Exception {
        //创建用户获取分布式锁的key
        final String lockName=new StringBuffer(machines).append(System.currentTimeMillis()).toString();
        //获取分布式锁实例
        RLock rLock=redissonClient.getLock(lockName);
        //可重入锁的方式获取分布式锁
        Boolean suo=rLock.tryLock(100,10, TimeUnit.MINUTES);
        try{
            if (suo){
                //获取key-value的值列表
                RMap<String, Map<String,String>> rMap=redissonClient.getMap(machines);
                //拆分各个机器对应的Map
                String[] value=keys.split(",");
                //循环删除对应的Key
                for (String key:value){
                    //删除所有的key
                    rMap.remove(key);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //释放锁
            if (rLock!=null){
                rLock.forceUnlock();
            }
        }
    }

    //Redis分布式锁实时控制每一个key
    @Override
    public void realTimeRedis(String dn) throws Exception {
        //定义Redisson中的RMap数据结构
        RMap<String,String> rMap=redissonClient.getMap(machineKeys);
        //转换实体Map
        Map<String,String> alertRuleMap=rMap.readAllMap();
        //获取key-value的值列表(规则Map)
        RMap<String, Alert> rRulesMap=redissonClient.getMap(rules);
        //对应为Map集合最好,不用再进行比较可直接拿出
        Map<String,Alert> alertMap=rRulesMap.readAllMap();
        //获取key-value的值列表(机器Map)
        RMap<String, Map<String,String>> rCurrentHashMap=redissonClient.getMap(machines);
        //转转Map集合
        Map<String,Map<String,String>> allMachinesMap=rCurrentHashMap.readAllMap();
        if (alertRuleMap.get(dn)!=null) {
            //一整个组合规则实体
            String[] alertRuleArray = alertRuleMap.get(dn).split("-");
            //遍历所有包含复合规则的实体进行比对
            for (String alertRuleJson : alertRuleArray) {
                //转换固定的实体
                AlertRule alertRule = JSON.toJavaObject(JSONObject.parseObject(alertRuleJson),AlertRule.class);
                //根据传过来的机器key查询当前是否含有对应机器聚合都含有的实时消息
                String[] machineList=alertRule.getAlertMachines().split(",");
                //控制整个组合规则中的机器累计存在数字
                int machineTotal=0;
                //循环遍历机器集合中的机器名
                for (String machine:machineList){
                    if (machine!=null && allMachinesMap.get(machine)!=null && !allMachinesMap.get(machine).isEmpty()){
                        //如果机器集合都存在则machineTotal++;
                        machineTotal++;
                    }
                }
                //如果自增的变量长度等于机器的长度则说明可以进行消息比对再决定是否钉钉报警
                if (machineTotal==machineList.length){
                    //控制一个错误消息实体
                    AlertMsg alertMsg=new AlertMsg();
                    //防止插入为null
                    alertMsg.setProjectMachine("");
                    //定义控制机器整形变量对应Redis中存在的机器名，一整个组合规则中的一个规则符合则+1
                    int machines=0;
                    //Sting的一个独立规则
                    String rule="";
                    //单个测试变量
                    String testRule="";
                    //String的一个计算表达式
                    String symbol="";
                    //遍历Redis是否包含此机器集合的所有数据
                    for (String machineName : machineList) {
                        //如果从Redis中取出对应key的value值不为空则机器变量自增
                        if (machineName!=null && allMachinesMap.get(machineName) != null && !allMachinesMap.get(machineName).isEmpty()) {
                            //拆分成规则主键Id的形式
                            String[] rulesList = alertRule.getAlertRuleId().split(",");
                            //循环遍历主键Id的组合规则进行消息比对
                            for (String ruleId : rulesList) {
                                //获取Map的键值，主键id：规则实体的格式
                                if (ruleId!=null && alertMap != null && !alertMap.keySet().isEmpty()) {
                                    //根据规则主键Id取出规则Alert实体类型
                                    Alert alert = alertMap.get(ruleId);
                                    //左右表达式不包含机器名则直接返回
                                    if (!alert.getLeftexpression().contains(machineName) && !alert.getRightexpression().contains(machineName)){
                                        continue;
                                    }
                                    //判断左右表达式全是数字则不要再进行覆盖直接continue;
                                    if (RuleUtil.judgeContainsCode(alert.getLeftexpression()) && RuleUtil.judgeContainsCode(alert.getRightexpression())) {
                                        continue;
                                    }
                                    //单个变量rule
                                    testRule=alert.getLeftexpression() + alert.getMidexpression() + alert.getRightexpression();
                                    //传过来的信息比对并且覆盖左右表达式
                                    for (String k : allMachinesMap.get(machineName).keySet()) {
                                        if (alert.getLeftexpression().equals(machineName + "." + k)) {
                                            //覆盖左表达式原数字
                                            alert.setLeftexpression(alert.getLeftexpression().replaceAll(machineName + "." + k, allMachinesMap.get(machineName).get(k)));
                                        }
                                        if (alert.getRightexpression().equals(machineName + "." + k)) {
                                            //覆盖右表达式原数字
                                            alert.setRightexpression(alert.getRightexpression().replaceAll(machineName + "." + k, allMachinesMap.get(machineName).get(k)));
                                        }
                                    }
                                    //判断左右表达式是否含有字母，有字母说明并没有完全覆盖
                                    if (RuleUtil.judgeContainsStr(alert.getLeftexpression()) || RuleUtil.judgeContainsStr(alert.getRightexpression())) {
                                        //字母不符合规则，需要continue跳出本次循环程序的规则，进行下一个规则
                                        continue;
                                    }
                                    //如果是完整的表达式的话，可以确认得到的运算符，以true控制，对应的变量machines进行自增
                                    if (alert.getMidexpression().contains(RuleUtil.getOperator(CalculateUtil.getExpression(alert.getLeftexpression()), CalculateUtil.getExpression(alert.getRightexpression())))) {
                                        //给工作人员报错哪一条规则带上注释
                                        symbol = symbol + alert.getLeftexpression() + alert.getMidexpression() + alert.getRightexpression() + ",";
                                        //控制失败消息(项目名称和机器集合)
                                        alertMsg.setProject(allMachinesMap.get(machineName).get("project"));
                                        alertMsg.setProjectMachine(alertMsg.getProjectMachine() + machineName + ",");
                                        alertMsg.setMsgTime(new Date(Long.parseLong(allMachinesMap.get(machineName).get("timestamp"))));
                                        //保存当前的失败规则
                                        rule = rule+testRule+",";
                                        //机器变量自增
                                        machines++;
                                    }
                                }
                            }
                        }
                    }
                    //如果对应的机器都可以在Redis中找到则可以发送本条符合规则的钉钉机器人
                    if (machines == machineList.length && !RedisUtil.selectAndSet(alertRule.getId().toString())) {
                        //如果为true则需要报警
                        DbRootSendUtil.robotSendText(alertMsg.getProject()+"的机器："+alertMsg.getProjectMachine()+"\n于"+
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(alertMsg.getMsgTime())+"\n在这个规则出错了："
                                +rule+"\n规则与表达式对比："+symbol+"\n规则10分钟内第一次报警了，需要人工排查.");
                        alertMsg.setRule(rule);
                        alertMsg.setRuleNumber(symbol);
                        alertMsg.setMsg("机器运转出错误");
                        alertMsg.setMsgCode("error");
                        //插入失败的规则
                        alertMsgMapper.insert(alertMsg);
                        //控制台输出失败的规则以及表达式
                        LOGGER.info("规则10分钟内第一次报警了，需要人工排查。"+rule+symbol);
                        LOGGER.info("查看失败的消息："+alertMsg.toString());
                    }
                    //删除Redis中Map的指定Key值
                    deleteRedisMsgMapKey(alertRule.getAlertMachines());
                }
            }
        }
    }
}

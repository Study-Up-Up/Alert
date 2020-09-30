package cn.hzby.whc.serviceImpl;

import cn.hzby.whc.entity.Alert;
import cn.hzby.whc.entity.AlertMsg;
import cn.hzby.whc.entity.AlertRule;
import cn.hzby.whc.mapperService.AlertMapper;
import cn.hzby.whc.mapperService.AlertMsgMapper;
import cn.hzby.whc.mapperService.AlertRuleMapper;
import cn.hzby.whc.requsetBody.RuleComRequest;
import cn.hzby.whc.service.AlertService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class AlertServiceImpl implements AlertService {

    @Resource(name = "alertMapper")
    AlertMapper alertMapper;

    @Resource(name = "redissonClient")
    RedissonClient redissonClient;

    @Resource(name = "alertMsgMapper")
    AlertMsgMapper alertMsgMapper;

    @Resource(name = "alertRuleMapper")
    AlertRuleMapper alertRuleMapper;

    //打印台日志
    private static final Logger LOGGER= LoggerFactory.getLogger(AlertServiceImpl.class);

    //定义全部规则存储时的key
    private static final String rules="rulesKey";

    //定义规则组合存储时的key
    private static final String ruleCombination="ruleCombinationKey";

    //定义全部规则存储时的key
    private static final String machineKeys="machinesRules";

    //分页获取所有的规则
    @Override
    public PageInfo<Alert> getAllRules(int page, int count) throws Exception {
        //确定分页已经记录
        PageHelper.startPage(page,count);
        //数据库分页不用
        List<Alert> alertList=alertMapper.selectAllRules();
        /**从Redis中获取规则的记录
        //定义Redisson中的RMap据结构
        RMap<String,Alert> rMap=redissonClient.getMap(rules);
        //对应为Map集合最好,不用再进行比较可直接拿出
        Map<String,Alert> alertMap=rMap.readAllMap();
        //循环遍历放到List中
        List<Alert> alertList=new ArrayList<>();
        for (String a:alertMap.keySet()){
            alertList.add(alertMap.get(a));
        }**/
        return new PageInfo<Alert>(alertList);
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
                    RMap<String,AlertRule> rMap=redissonClient.getMap(machineKeys);
                    //先清空缓存中的列表数据
                    rMap.clear();
                    //以机器名-组合规则循环遍历到Map中
                    for (AlertRule alertRule:rList){
                        if (alertRule!=null) {
                            //拆分机器名字
                            String[] machines = alertRule.getAlertMachines().split(",");
                            for (String machine : machines) {
                                if (machine != null) {
                                    //在Redisson中Map导入对应的规则
                                    rMap.putIfAbsent(machine,alertRule);
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


    //获取组合规则
    @Override
    public Map<String,Object> getRuleCombination(int page, int count) throws Exception {
        Map<String,Object> map=new HashMap<>();
        //获取key-value的值列表(规则Map)
        RMap<String, Alert> rMap=redissonClient.getMap(rules);
        //对应为Map集合最好,不用再进行比较可直接拿出
        Map<String,Alert> alertMap=rMap.readAllMap();
        //基于数据库和Redis分别分页得到数据
        List<Alert> alertList=new ArrayList<>();
        //组合规则参数
        List<String> stringList=new ArrayList<>();
        //标题List
        List<String> titleList=new ArrayList<>();
        //显示的List
        List<String> realSymbolList=new ArrayList<>();
        //开始分页
        PageHelper.startPage(page,count);
        //从数据库获取组合规则
        List<AlertRule> alertRuleList=alertRuleMapper.selectRuleCombination();
        PageInfo<AlertRule> pageInfo=new PageInfo<AlertRule>(alertRuleList);
        //从Redis中获取到最新的数据
        for (AlertRule alertRule:alertRuleList){
            String[] idList=alertRule.getAlertRuleId().split(",");
            String testString="";
            String title="";
            String realList="";
            for (String id:idList){
                alertList.add(alertMap.get(id));
                testString=testString+alertMap.get(id).getLeftexpression()+","+alertMap.get(id).getMidexpression()+
                        ","+alertMap.get(id).getRightexpression()+"-";
                title=alertMap.get(id).getTitle();
                realList=realList+alertMap.get(id).getLeftexpression()+alertMap.get(id).getMidexpression()
                        +alertMap.get(id).getRightexpression()+"\n";
            }
            titleList.add(title);
            stringList.add(testString);
            realSymbolList.add(realList);
        }
        /**获取Redisson中的组合规则List
        RList<AlertRule> ruleCombinationList=redissonClient.getList(ruleCombination);
        //转换成Java可用的组合规则List
        List<AlertRule> combinationList=ruleCombinationList.readAll();
        //确定组合规则分页记录
        PageHelper.startPage(page,count);
        //控制查询的参数长度
        PageInfo<AlertRule> pageInfo=new PageInfo<AlertRule>(combinationList);
        //获取key-value的值列表(规则Map)
        RMap<String, Alert> rMap=redissonClient.getMap(rules);
        //对应为Map集合最好,不用再进行比较可直接拿出
        Map<String,Alert> alertMap=rMap.readAllMap();
        //创建List
        List<Alert> alertList=new ArrayList<>();
        //循环遍历给前端完整的数据
        for (int i=0;i<pageInfo.getList().size();i++){
            //拆分规则主键ID
            String[] idList=pageInfo.getList().get(i).getAlertRuleId().split(",");
            //放入到Map中
            for (String id: idList){
                alertList.add(alertMap.get(id));
            }
        }**/
        map.put("total",pageInfo.getTotal());
        map.put("ruleCombinationList",pageInfo.getList());
        map.put("ruleList",alertList);
        map.put("stringList",stringList);
        map.put("titleList",titleList);
        map.put("realList",realSymbolList);
        return map;
    }

    //分页获取所有失败信息
    @Override
    public PageInfo<AlertMsg> getErrorAll(int page, int count) throws Exception {
        PageHelper.startPage(page,count);
        List<AlertMsg> alertMsgList=alertMsgMapper.selectAllMsg();
        return new PageInfo<AlertMsg>(alertMsgList);
    }

    //插入组合规则
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void insertRules(String ruleTitle,String va,String machineList, String note) throws Exception {
        //规则主键Id集合
        String idList="";
        //控制Redis中的List
        List<Alert> alertList=new ArrayList<>();
        for (String rule:va.split(",")){
            if (rule!=null){
                //分隔组合规则
                String[] rules=rule.split("-");
                Alert alert = new Alert();
                alert.setTitle(ruleTitle);
                alert.setLeftexpression(rules[0]);
                alert.setMidexpression(rules[1]);
                alert.setRightexpression(rules[2]);
                alert.setNote(note);
                alert.setTime(new Date());
                //插入实体获取规则
                alertMapper.insert(alert);
                //获取主键Id集合
                idList = idList + alert.getId() + ",";
                alertList.add(alert);
            }
        }
        //插入到组合规则表中
        AlertRule alertRule=new AlertRule();
        alertRule.setAlertRuleId(idList);
        alertRule.setAlertMachines(machineList);
        alertRule.setAlertRuleNote(note);
        alertRule.setAlertRuleTime(new Date());
        alertRuleMapper.insert(alertRule);
        if (alertRule.getId()!=null){
            //如果保存了组合规则则刷新Redis的规则和组合(采取分布式锁控制Redis，避免全查全删)
            insertRedisComRules(alertRule);
            insertRedisRules(alertList);
            insertMachinesMapRules(alertRule);
        }
    }

    //更新组合规则,比较传过来的主键Id集合是否和数据库的主键Id集合的长度一致
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void updateRules(RuleComRequest ruleComRequest) throws Exception {
        //控制Reids中的更新List
        List<Alert> alertList=new ArrayList<>();
        //删除的Redis中的Id
        String deleteId="";
        //根据传过来的组合规则Id查询指定的组合规则
        AlertRule alertRuleCrud=alertRuleMapper.selectByPrimaryKey(ruleComRequest.getId());
        //分割数据库中主键ID
        String[] baseIdList=alertRuleCrud.getAlertRuleId().split(",");
        //主键ID集合和规则表达式集合
        String[] ruleList=ruleComRequest.getComId().split(",");
        String[] symbol=ruleComRequest.getVa().split("," );
        //不管怎么更新主键Id的个数一定会更新，表达式可能比主键Id集合大或者小(这个是不管如何更行都会肯定执行的)
        for (int i = 0; i < ruleList.length; i++) {
            if (symbol[i] != null && !symbol[i].equals("")) {
                //分隔组合规则
                String[] rules = symbol[i].split("-");
                if (rules[i]!=null && !rules[i].equals("")) {
                    Alert alert = new Alert();
                    alert.setId(Long.parseLong(ruleList[i]));
                    alert.setTitle(ruleComRequest.getRuleTitle());
                    alert.setLeftexpression(rules[0]);
                    alert.setMidexpression(rules[1]);
                    alert.setRightexpression(rules[2]);
                    alert.setNote(ruleComRequest.getNote());
                    alert.setTime(new Date());
                    //插入实体获取规则
                    alertMapper.updateByPrimaryKey(alert);
                    alertList.add(alert);
                }
            }
        }

        //更新组合表的内容
        AlertRule alertRule=new AlertRule();
        alertRule.setAlertRuleId("");

        //当长度一致时，仅仅是更新组合原则即可
        if (baseIdList.length == ruleList.length && baseIdList.length == symbol.length) {
            //一致则不用更新,放入原生Id集合
            alertRule.setAlertRuleId(ruleComRequest.getComId());
        }
        //当长度不一致时，主键Id长度>数据库规则Id集合长度，说明组合规则发生了减少
        if (baseIdList.length > ruleList.length && baseIdList.length > symbol.length){
            //需要减少组合规则中缺少的规则
            for (String baseId:baseIdList){
                //如果不包含的主键Id则可以删除原数据库的规则主键Id
                if (baseId!=null && !ruleComRequest.getComId().contains(baseId)){
                    //先查询当前规则Id是否存在被其他规则所复用，需要数据库模糊查询(组合规则比较少，模糊查询并没有什么大问题)
                    //现在不存在规则复用情况，可以不用模糊查询，后续扩展前后端点击按钮时会用到(个人手动修改数据库随意增加组合规则不允许)

                    //删除不符合的规则主键Id
                    alertMapper.deleteByPrimaryKey(Long.parseLong(baseId));
                    //控制需要删除的idList
                    deleteId=deleteId+baseId+",";
                }else {
                    //控制主键Id列表
                    alertRule.setAlertRuleId(alertRule.getAlertRuleId()+baseId+",");
                }

            }
        }
        //当长度不一致时，主键Id长度==数据库规则Id集合长度但是规则多了，说明组合规则发生了增加
        if (baseIdList.length == ruleList.length && baseIdList.length < symbol.length){
            //首先确定前面主键Id的边界
            alertRule.setAlertRuleId(ruleComRequest.getComId());
            //需要增加相对应的未含有的规则
            for(int i= ruleList.length;i<symbol.length;i++){
                //分隔组合规则
                String[] rules = symbol[i].split("-");
                if (rules[i]!=null && !rules[i].equals("")) {
                    //插入指定的规则并获取对应的主键ID集合
                    Alert alert = new Alert();
                    alert.setTitle(ruleComRequest.getRuleTitle());
                    alert.setLeftexpression(rules[0]);
                    alert.setMidexpression(rules[1]);
                    alert.setRightexpression(rules[2]);
                    alert.setNote(ruleComRequest.getNote());
                    alert.setTime(new Date());
                    alertMapper.insert(alert);
                    alertList.add(alert);
                    //控制完整的主键Id集合
                    alertRule.setAlertRuleId(alertRule.getAlertRuleId()+alert.getId()+",");
                }
            }
        }

        alertRule.setId(ruleComRequest.getId());
        alertRule.setAlertMachines(ruleComRequest.getMachineList());
        alertRule.setAlertRuleNote(ruleComRequest.getNote());
        alertRule.setAlertRuleTime(new Date());
        alertRuleMapper.updateByPrimaryKey(alertRule);

        if (!deleteId.equals("")) {
            //删除不符合Redis中的主键Id
            deleteRedisRules(deleteId);
        }
        //刷新Redis的规则和组合
        insertRedisRules(alertList);
        setRuleCombinationToRedis();
    }

    //删除组合规则(组合规则主键Id)
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void deleteRules(Long id) throws Exception {
        //删除组合规则中的主键Id与其他规则的主键Id
        //先根据组合规则Id查询对应的实体
        AlertRule alertRule=alertRuleMapper.selectByPrimaryKey(id);
        if (alertRule!=null) {
            //拆分主键Id进行删除
            String[] ruleIdList = alertRule.getAlertRuleId().split(",");
            for (String ruleId : ruleIdList) {
                if (ruleId != null) {
                    //删除规则主键Id
                    alertMapper.deleteByPrimaryKey(Long.parseLong(ruleId));
                }
            }
            //删除Redis中的数据
            deleteRedisRules(alertRule.getAlertRuleId());
        }
        //最后删除整个组合
        alertRuleMapper.deleteByPrimaryKey(id);
        //冲刷Redis中的数据
        setRuleCombinationToRedis();
    }

    //控制Redis的组合规则和规则的插入
    @Override
    public void insertRedisComRules(AlertRule alertRule) throws Exception {
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
                //分布式锁竞争插入
                rList.add(alertRule);
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

    //控制Redis的单个规则的插入
    @Override
    public void insertRedisRules(List<Alert> alertList) throws Exception {
        //创建用户获取分布式锁的key
        final String lockName=new StringBuffer(rules).append(System.currentTimeMillis()).toString();
        //获取分布式锁实例
        RLock rLock=redissonClient.getLock(lockName);
        //可重入锁的方式获取分布式锁
        Boolean suo=rLock.tryLock(100,10, TimeUnit.MINUTES);
        try{
            if (suo) {
                //获取key-value的值列表(规则Map)
                RMap<String, Alert> rMap=redissonClient.getMap(rules);
                //循环遍历当前规则放入到Map中
                for (Alert alert : alertList) {
                    //在Redisson中Map导入对应的规则
                    rMap.put(alert.getId().toString(), alert);
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



    //控制Redis的单个规则的删除
    @Override
    public void deleteRedisRules(String ruleId) throws Exception {
        //创建用户获取分布式锁的key
        final String lockName=new StringBuffer(rules).append(System.currentTimeMillis()).toString();
        //获取分布式锁实例
        RLock rLock=redissonClient.getLock(lockName);
        //可重入锁的方式获取分布式锁
        Boolean suo=rLock.tryLock(100,10, TimeUnit.MINUTES);
        try{
            if (suo) {
                //获取key-value的值列表(规则Map)
                RMap<String, Alert> rMap=redissonClient.getMap(rules);
                //切割IdList
                String[] idList=ruleId.split(",");
                //循环遍历当前规则放入到Map中
                for (String id : idList) {
                    if (id!=null) {
                        //在Redisson中Map导入对应的规则
                        rMap.remove(id);
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

    //控制机器名-组合规则的Map
    @Override
    public void insertMachinesMapRules(AlertRule alertRule) throws Exception {
        //创建用户获取分布式锁的key
        final String lockName=new StringBuffer(machineKeys).append(System.currentTimeMillis()).toString();
        //获取分布式锁实例
        RLock rLock=redissonClient.getLock(lockName);
        //可重入锁的方式获取分布式锁
        Boolean suo=rLock.tryLock(100,10, TimeUnit.MINUTES);
        try{
            if (suo){
                //定义Redisson中的RMap数据结构
                RMap<String,String> rMap=redissonClient.getMap(machineKeys);
                //以机器名-组合规则循环遍历到Map中
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
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //释放锁
            if (rLock!=null){
                rLock.forceUnlock();
            }
        }
    }

}

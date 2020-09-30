package cn.hzby.whc.controller;


import cn.hzby.whc.entity.Alert;
import cn.hzby.whc.entity.AlertMsg;
import cn.hzby.whc.requsetBody.RuleComRequest;
import cn.hzby.whc.service.AlertService;
import cn.hzby.whc.tokenauthentication.annotation.AuthToken;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AlertController {

    @Autowired
    AlertService alertService;

    //分页查询所有组合规则(被Token修饰后就必须验证Redis是否存在且不销时)
    @AuthToken
    @CrossOrigin(origins = "*")
    @PostMapping("/getRulesAll")
    public Map<String,Object> getRulesAll(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "count", defaultValue = "15") int count) throws Exception{
        //规则参数处理结果
        Map<String, Object> resultMap = new HashMap<>();
        //获得所有的规则
        PageInfo<Alert> pageInfo=alertService.getAllRules(page,count);
        resultMap.put("success",true);
        resultMap.put("list",pageInfo.getList());
        resultMap.put("total",pageInfo.getTotal());
        return resultMap;
    }

    //分页查询组合
    @AuthToken
    @CrossOrigin(origins = "*")
    @PostMapping("/getRuleCombination")
    public Map<String,Object> getRuleCombination(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "count", defaultValue = "100") int count) throws Exception{
        //规则参数处理结果
        Map<String, Object> resultMap = new HashMap<>();
        //获取组合规则
        resultMap=alertService.getRuleCombination(page, count);
        resultMap.put("success",true);
        //直接返还解析过的数据
        return resultMap;
    }

    //分页获取所有失败的消息
    @AuthToken
    @CrossOrigin(origins = "*")
    @PostMapping("/getErrorAll")
    public Map<String,Object> getErrorAll(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "count", defaultValue = "15") int count) throws Exception{
        //规则参数处理结果
        Map<String, Object> resultMap = new HashMap<>();
        //得到分页后的数据
        PageInfo<AlertMsg> pageInfo=alertService.getErrorAll(page, count);
        resultMap.put("success",true);
        resultMap.put("list",pageInfo.getList());
        resultMap.put("total",pageInfo.getTotal());
        return resultMap;
    }

    //增加组合规则
    @AuthToken
    @CrossOrigin(origins = "*")
    @PostMapping("/insertRules")
    public Map<String,Object> insertRules(@RequestParam("ruleTitle")String ruleTitle,@RequestParam("va")String va,
                                          @RequestParam("machineList")String machineList, @RequestParam("note")String note) throws Exception{
        //规则参数处理结果
        Map<String, Object> resultMap = new HashMap<>();
        //插入组合规则
        alertService.insertRules(ruleTitle, va,machineList, note);
        resultMap.put("message",true);
        return resultMap;
    }

    //更新组合规则
    @AuthToken
    @CrossOrigin(origins = "*")
    @PostMapping("/updateRules")
    public Map<String,Object> updateRules(RuleComRequest ruleComRequest) throws Exception{
        //规则参数处理结果
        Map<String, Object> resultMap = new HashMap<>();
        //更新组合规则
        alertService.updateRules(ruleComRequest);
        resultMap.put("message",true);
        return resultMap;
    }

    //删除组合规则
    @AuthToken
    @CrossOrigin(origins = "*")
    @PostMapping("/deleteRules")
    public Map<String,Object> deleteRules(@RequestParam("id")String id) throws Exception{
        //规则参数处理结果
        Map<String, Object> resultMap = new HashMap<>();
        //删除组合规则
        alertService.deleteRules(Long.parseLong(id));
        resultMap.put("message",true);
        return resultMap;
    }

}

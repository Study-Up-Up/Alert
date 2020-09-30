package cn.hzby.whc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;

@Repository
public class AlertMsg implements Serializable {
    /**
     * 消息的主键
     */
    private Long msgId;

    /**
     * 项目
     */
    private String project;

    /**
     * 项目的机器
     */
    private String projectMachine;

    /**
     * 规则
     */
    private String rule;

    /**
     * 规则的数字
     */
    private String ruleNumber;

    /**
     * 消息
     */
    private String msg;

    /**
     * 消息的code
     */
    private String msgCode;

    /**
     * 消息的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date msgTime;

    private static final long serialVersionUID = 1L;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProjectMachine() {
        return projectMachine;
    }

    public void setProjectMachine(String projectMachine) {
        this.projectMachine = projectMachine;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(String ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public Date getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Date msgTime) {
        this.msgTime = msgTime;
    }
}
package cn.hzby.whc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;

@Repository
public class AlertRule implements Serializable {
    /**
     * 信息警报的ID
     */
    private Long id;

    /**
     * 信息警报的规则ID集合
     */
    private String alertRuleId;

    /**
     * 规则的机器集合
     */
    private String alertMachines;

    /**
     * 信息警报ID的注释规则
     */
    private String alertRuleNote;

    /**
     * 组合规则的插入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date alertRuleTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlertRuleId() {
        return alertRuleId;
    }

    public void setAlertRuleId(String alertRuleId) {
        this.alertRuleId = alertRuleId;
    }

    public String getAlertMachines() {
        return alertMachines;
    }

    public void setAlertMachines(String alertMachines) {
        this.alertMachines = alertMachines;
    }

    public String getAlertRuleNote() {
        return alertRuleNote;
    }

    public void setAlertRuleNote(String alertRuleNote) {
        this.alertRuleNote = alertRuleNote;
    }

    public Date getAlertRuleTime() {
        return alertRuleTime;
    }

    public void setAlertRuleTime(Date alertRuleTime) {
        this.alertRuleTime = alertRuleTime;
    }
}
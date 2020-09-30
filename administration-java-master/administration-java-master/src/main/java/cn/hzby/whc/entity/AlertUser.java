package cn.hzby.whc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;


@Repository
public class AlertUser implements Serializable {
    /**
     * 规则管理系统的用户主键Id
     */
    private Long alertId;

    /**
     * 规则管理系统的用户名
     */
    private String alertUsername;

    /**
     * 规则管理系统的用户密码
     */
    private String alertPassword;

    /**
     * 规则管理系统的用户创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date alertInsert;

    private static final long serialVersionUID = 1L;

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public String getAlertUsername() {
        return alertUsername;
    }

    public void setAlertUsername(String alertUsername) {
        this.alertUsername = alertUsername;
    }

    public String getAlertPassword() {
        return alertPassword;
    }

    public void setAlertPassword(String alertPassword) {
        this.alertPassword = alertPassword;
    }

    public Date getAlertInsert() {
        return alertInsert;
    }

    public void setAlertInsert(Date alertInsert) {
        this.alertInsert = alertInsert;
    }
}
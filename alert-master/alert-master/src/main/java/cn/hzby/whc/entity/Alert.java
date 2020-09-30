package cn.hzby.whc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;


@Repository
public class Alert implements Serializable {
    /**
     * 警报的主键
     */
    private Long id;

    /**
     * 规则的标题
     */
    private String title;

    /**
     * 规则的左表达式
     */
    private String leftexpression;

    /**
     * 规则的中间运算符
     */
    private String midexpression;

    /**
     * 规则的右表达式
     */
    private String rightexpression;

    /**
     * 规则的注释
     */
    private String note;

    /**
     * 规则的插入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date time;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLeftexpression() {
        return leftexpression;
    }

    public void setLeftexpression(String leftexpression) {
        this.leftexpression = leftexpression;
    }

    public String getMidexpression() {
        return midexpression;
    }

    public void setMidexpression(String midexpression) {
        this.midexpression = midexpression;
    }

    public String getRightexpression() {
        return rightexpression;
    }

    public void setRightexpression(String rightexpression) {
        this.rightexpression = rightexpression;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
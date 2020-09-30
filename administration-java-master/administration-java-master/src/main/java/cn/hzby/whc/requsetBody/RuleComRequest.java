package cn.hzby.whc.requsetBody;

public class RuleComRequest {

    public Long  id;
    public String comId;
    public String ruleTitle;
    public String va;
    public String machineList;
    public String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getRuleTitle() {
        return ruleTitle;
    }

    public void setRuleTitle(String ruleTitle) {
        this.ruleTitle = ruleTitle;
    }

    public String getVa() {
        return va;
    }

    public void setVa(String va) {
        this.va = va;
    }

    public String getMachineList() {
        return machineList;
    }

    public void setMachineList(String machineList) {
        this.machineList = machineList;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

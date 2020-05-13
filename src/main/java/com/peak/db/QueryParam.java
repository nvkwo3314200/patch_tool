package com.peak.db;

import java.util.Date;
import java.util.List;

public class QueryParam {
    private Date lastUpdateTime;
    private String objName;
    private List<ObjectEnum> typeList;

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<ObjectEnum> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<ObjectEnum> typeList) {
        this.typeList = typeList;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }
}

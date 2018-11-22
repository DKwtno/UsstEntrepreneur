package com.usst.demo.vo;

import java.util.List;

public class Groupping {
    private String groupName,groupId;
    private String demand;//对队员的要求
    private String introduction;
    private Integer currentSize,maxSize;

    public Integer getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(Integer currentSize) {
        this.currentSize = currentSize;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    private List<Tag> fieldTags,personalTags;//涉及的领域和需求的人物性格

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<Tag> getFieldTags() {
        return fieldTags;
    }

    public void setFieldTags(List<Tag> fieldTags) {
        this.fieldTags = fieldTags;
    }

    public List<Tag> getPersonalTags() {
        return personalTags;
    }

    public void setPersonalTags(List<Tag> personalTags) {
        this.personalTags = personalTags;
    }

}

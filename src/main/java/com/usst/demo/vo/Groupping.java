package com.usst.demo.vo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class Groupping {
    @NotNull
    private String groupName;
    @NotNull
    private Integer groupId;
    @Length(max = 300)
    private String demand;//对队员的要求
    private String introduction;
    @Min(1)
    private Integer currentSize;
    @Min(2)
    private Integer maxSize;
    private List<Tag> fieldTags,personalTags;//涉及的领域和需求的人物性格

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
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

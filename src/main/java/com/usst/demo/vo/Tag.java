package com.usst.demo.vo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Tag {
    private Integer tagId;
    private String tagName;
    private Integer type;

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public Tag() {
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static Tag getTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setTagId(rs.getInt(1));
        tag.setTagName(rs.getString(2));
        tag.setType(rs.getInt(3));
        return tag;
    }
}

package com.usst.demo.repo;

import com.usst.demo.vo.Groupping;
import com.usst.demo.vo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class GrouppingRepository {
    public static final Integer ORDER_BY_DATE = 1;
    public static final Integer SUCCESS = 2;
    public static final Integer ALREADY_INSERTED = 3;
    private JdbcTemplate jdbc;
    @Autowired
    public GrouppingRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    /**
     * 返回数据库中的groupping_info有多少
     * @return
     */
    private Long size(){
        Long size = jdbc.query("select count(*) from groupping_info;",new RowMapper<Long>(){

                @Override
                public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getLong(1);
                }
            }).get(0);
        return size;
    }

    public Integer maxPage(Integer rows){
        return Integer.valueOf((int)(size()/rows)+1);
    }
    /**
     *
     * @param page
     * @param method 根据排序方式搜索索引
     * @return
     */
    public List<Groupping> getGrouppings(Integer page, Integer rows, Integer method){
        if(method==ORDER_BY_DATE){
            return getByOrder(page, rows);
        }
        return null;
    }

    /**
     * 不仅要添加组队信息，还要把tag也添加进数据库
     * @param groupping 要求包含group_id，一个小队只能发布一次，但是可以update
     * @return
     */
    public Integer addGroupping(Groupping groupping){
        if(findGrouppingByGroupId(groupping.getGroupId())!=null){
            return ALREADY_INSERTED;
        }
        //插入数据记录
        jdbc.update("insert into groupping_info(gid,maxsize,cursize,demand,update_date) " +
                "values(?,?,?,?,?);",groupping.getGroupId(),groupping.getMaxSize(),
                groupping.getCurrentSize(),groupping.getDemand(),new java.sql.Date(new Date().getTime()));
        //插入personaltag
        jdbc.batchUpdate("insert into grouping_personalitytag_taken(grouping_id,tid) " +
                "values(?,?);", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Tag tag = groupping.getPersonalTags().get(i);
                ps.setInt(1,groupping.getGroupId());
                ps.setInt(2,tag.getTagId());
            }

            @Override
            public int getBatchSize() {
                return groupping.getPersonalTags().size();
            }
        });
        //field_tag是和group绑定的，一定是先有了group才能创建groupping找队友信息
        return SUCCESS;
    }

    public Groupping findGrouppingByGroupId(Integer groupId) {
        List<Groupping> grouppings = jdbc.query("select gp.gid,gp.maxsize,gp.cursize,gi.name " +
                "from groupping_info gp where gp.gid=?", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1,groupId);
            }
        }, new RowMapper<Groupping>() {
            @Override
            public Groupping mapRow(ResultSet rs, int rowNum) throws SQLException {
                return getGroupping(rs);

            }
        });
        return grouppings==null||grouppings.size()<=0?null:grouppings.get(0);
    }

    private Groupping getGroupping(ResultSet rs) throws SQLException {
        Groupping groupping = new Groupping();
        groupping.setGroupId(rs.getInt(1));
        groupping.setMaxSize(rs.getInt(2));
        groupping.setCurrentSize(rs.getInt(3));
        groupping.setGroupName(rs.getString(4));
        return groupping;
    }

    /**
     * 返回最新的组队信息，要求组队名称，组队ID，组队的标签，组队的介绍<br>
     * 要涉及tag表和tag_taken表，sql语句很复杂
     * @param page 页数，从1开始
     * @param rows 行数，不为0
     * @return
     */
    private List<Groupping> getByOrder(Integer page, Integer rows) {
        int start = (page-1)*rows;
        int end = start+rows;
        long finsize = size();
        int size = rows;
        if(end>finsize)
            size = (int)(finsize-start);
        if(size<=0)
            throw new RuntimeException("页数/行数错误！");
        int finalSize = size;
        List<Groupping> tmp = jdbc.query("select gp.gid,gp.maxsize,gp.cursize,gi.name " +
                "from groupping_info gp inner join group_info gi " +
                "on gp.gid=gi.gid order by gi.update_date limit ?,?;", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, start);
                ps.setInt(2, finalSize);
            }
        }, new RowMapper<Groupping>() {
            @Override
            public Groupping mapRow(ResultSet rs, int rowNum) throws SQLException {
                return getGroupping(rs);
            }
        });
        for(Groupping groupping:tmp){
            groupping.setPersonalTags(jdbc.query("select t.tid,t.name,t.ptype from " +
                    "groupping_personalitytag_taken g inner join personality_tag t " +
                    "on g.tid=t.tid where g.grouping_id=?", new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, groupping.getGroupId());
                }
            }, new RowMapper<Tag>() {
                @Override
                public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return getTag(rs);
                }
            }));
            groupping.setFieldTags(jdbc.query("select t.tid,t.name,t.ptype from " +
                    "group_fieldtag_taken g inner join field_tag t " +
                    "on g.tid=t.tid where g.group_id=?", new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, groupping.getGroupId());
                }
            }, new RowMapper<Tag>() {
                @Override
                public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return getTag(rs);
                }
            }));
        }
        return tmp;
    }

    public static Tag getTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setTagId(rs.getInt(1));
        tag.setTagName(rs.getString(2));
        tag.setType(rs.getInt(3));
        return tag;
    }
}

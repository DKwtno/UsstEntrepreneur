package com.usst.demo.repo;

import com.usst.demo.vo.Group;
import com.usst.demo.vo.GroupApplying;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 入队申请如果被拒绝了，应该把拒绝信息发送到用户邮箱中，
 * 不需要在数据库中存储一个专门的"rejected"状态
 */
@Repository
public class GroupRepository {
    private JdbcTemplate jdbc;
    @Autowired
    public GroupRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    public List<Group> findAgreedGroupsByUserId(Integer userId){
        return jdbc.query("select g.gid, g.name, g.captain_id from " +
                "group_info g inner join group_user_taken gu on g.gid=gu.gid where gu.uid=? " +
                "and gu.stat='agreed'", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, userId.toString());
            }
        }, new RowMapper<Group>() {
            @Override
            public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
                return getGroup(rs);
            }
        });
    }

    private Group getGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setGroupId(rs.getInt(1));
        group.setGroupName(rs.getString(2));
        group.setCaptainId(rs.getInt(3));
        return group;
    }

    /**
     *
     * @param groupId
     * @return 找小组的组员
     */
    public List<Group> findAgreedGroupsByGroupId(Integer groupId){
        return jdbc.query("select g.gid, g.name, g.captain_id from " +
                "group_info g inner join group_user_taken gu on g.gid=gu.gid where gu.gid=? " +
                "and gu.stat='agreed'", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, groupId.toString());
            }
        }, new RowMapper<Group>() {
            @Override
            public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
                return getGroup(rs);
            }
        });
    }
    public List<GroupApplying> findApplyingGroupsByGroupId(Integer groupId){
        return jdbc.query("select gu.gid,gu.uid from " +
                "group_user_taken gu where gu.gid=? " +
                "and gu.stat='applying'", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, groupId.toString());
            }
        }, new RowMapper<GroupApplying>() {
            @Override
            public GroupApplying mapRow(ResultSet rs, int rowNum) throws SQLException {
                GroupApplying groupApplying = new GroupApplying();
                groupApplying.setGroupId(rs.getInt(1));
                groupApplying.setUserId(rs.getInt(2));
                return groupApplying;
            }
        });
    }
    public List<GroupApplying> findApplyingGroupsByUserId(Integer userId){
        return jdbc.query("select gu.gid,gu.uid from " +
                " group_user_taken gu where gu.uid=? " +
                "and gu.stat='applying'", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, userId.toString());
            }
        }, new RowMapper<GroupApplying>() {
            @Override
            public GroupApplying mapRow(ResultSet rs, int rowNum) throws SQLException {
                GroupApplying groupApplying = new GroupApplying();
                groupApplying.setGroupId(rs.getInt(1));
                groupApplying.setUserId(rs.getInt(2));
                return groupApplying;
            }
        });
    }
    public void createGroup(Group group){

    }
}

package com.usst.demo.repo;

import com.usst.demo.vo.Group;
import com.usst.demo.vo.GroupApplying;
import com.usst.demo.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 入队申请如果被拒绝了，应该把拒绝信息发送到用户邮箱中，
 * 不需要在数据库中存储一个专门的"rejected"状态
 */
@Repository
public class GroupRepository {
    private JdbcTemplate jdbc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    public GroupRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    public Group findGroupByGroupId(Integer groupId){

        List<Group> list = jdbc.query("select gid,`name`,abstract,establist_date,captain_id," +
                "cursize from group_info where gid=? ", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1,groupId);
            }
        }, new RowMapper<Group>() {
            @Override
            public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
                Group group = new Group();
                group.setGroupId(rs.getInt(1));
                group.setGroupName(rs.getString(2));
                group.setIntroduction(rs.getString(3));
                group.setEstablishDate(rs.getDate(4));
                group.setCaptainId(rs.getInt(5));
                return group;
            }
        });
        if(list.size()==1){
            list.get(0).setMembers(findMemebersByGroupId(groupId));
        }
        return list==null||list.size()==0?null:list.get(0);
    }

    public List<Group> findAgreedGroupsByUserId(Integer userId){
        return jdbc.query("select g.gid, g.name, g.captain_id,g.establish_date from " +
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
        group.setEstablishDate(rs.getDate(4));
        return group;
    }

    /**
     *
     * @param groupId
     * @return 找小组的组员
     */
    public List<User> findMemebersByGroupId(Integer groupId){
        List<Integer> userIds = jdbc.query("select gu.uid from " +
                "group_user_taken gu where gu.gid=? " +
                "and gu.stat='agreed'", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1,groupId);
            }
        }, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });
        List<User> users = new ArrayList<>();
        for(Integer uid:userIds)
            users.add(userRepository.findByUserId(uid));
        return users;
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

    /**
     * 查找用户正在申请的小组
     * @param userId
     * @return
     */
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
        jdbc.update("insert into group_info(`name`, abstract, establish_date, captain_id) " +
                "values(?,?,?,?)", group.getGroupName(),group.getIntroduction(),group.getEstablishDate(),
                group.getCaptainId());
    }
}

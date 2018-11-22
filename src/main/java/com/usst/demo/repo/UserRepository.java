package com.usst.demo.repo;

import com.usst.demo.util.RandName;
import com.usst.demo.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {
    private JdbcTemplate jdbc;
    @Autowired
    public UserRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    public User findByUserId(Integer userId){
        List<User> list = jdbc.query("select uid,username,password,nickname " +
                "from user_basic where uid=?", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1,userId);
            }
        }, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return getUser(rs);
            }
        });
        return list==null||list.size()==0?null:list.get(0);
    }

    private User getUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUid(rs.getInt(1));
        user.setUsername(rs.getString(2));
        user.setPassword(rs.getString(3));
        user.setNickName(rs.getString(4));
        return user;
    }

    public User findByUserName(String userName){
        List<User> list = jdbc.query("select uid,username,password,nickname " +
                "from user_basic where username=?", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1,userName);
            }
        }, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return getUser(rs);
            }
        });
        return list==null||list.size()==0?null:list.get(0);
    }
    public boolean doLogin(User u){
        String username = u.getUsername();
        String password = u.getPassword();
        User user = findByUserName(username);
        if(user!=null) {
            u.setNickName(user.getNickName());
            u.setUid(user.getUid());
        }
        return user==null?false:user.getPassword().trim().equals(password.trim());
    }
    public boolean doRegister(User u){
        String username = u.getUsername();
        User user = findByUserName(username);
        if(user!=null)
            return false;
        updateUser(u);
        return true;
    }
    public void updateUser(User user) {
        jdbc.update("insert into user_basic(username, password, nickname) " +
                "values(?,?,?)",user.getUsername(),user.getPassword(),
                RandName.getRandName());
    }
}

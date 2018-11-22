package com.usst.demo.repo;

import com.usst.demo.vo.Groupping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GrouppingRepository {
    public static final Integer ORDER_BY_DATE = 1;
    private JdbcTemplate jdbc;
    @Autowired
    public GrouppingRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    /**
     * 返回数据库中的groupping_info有多少
     * @return
     */
    public Long size(){
        Long size = jdbc.query("select count(*) from groupping_info;",new RowMapper<Long>(){

                @Override
                public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getLong(1);
                }
            }).get(0);
        return size;
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
     * 返回最新的组队信息，要求组队名称，组队ID，组队的标签，组队的介绍<br>
     * 想法是创建视图，要涉及tag表和tag_taken表，sql语句很复杂
     * @param page
     * @param rows
     * @return
     */
    private List<Groupping> getByOrder(Integer page, Integer rows) {
        return jdbc.query("select gi.gid, gi.max_size,gi.cur_size,g.");
    }
}

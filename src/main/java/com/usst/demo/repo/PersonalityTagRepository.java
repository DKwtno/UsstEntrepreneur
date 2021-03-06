package com.usst.demo.repo;

import com.usst.demo.vo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PersonalityTagRepository {

    private JdbcTemplate jdbc;
    @Autowired
    public PersonalityTagRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    public List<Tag> getAllTags(){
        return jdbc.query("select * from personality_tag;", new RowMapper<Tag>() {
            @Override
            public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Tag.getTag(rs);
            }
        });
    }


    public void saveNewTag(Tag tag){
        jdbc.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement preparedStatement = con.prepareStatement
                        ("insert into personality_tag values(null, ?, ?);");
                preparedStatement.setString(1,tag.getTagName());
                preparedStatement.setString(2,tag.getType()==null?null:tag.getType().toString());
                return preparedStatement;
            }
        });
    }
}

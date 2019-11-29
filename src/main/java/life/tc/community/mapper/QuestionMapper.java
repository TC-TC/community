package life.tc.community.mapper;

import life.tc.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface QuestionMapper {

    //将问题发布的相关信息插入数据库
    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,tag) values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    public void create(Question question);

    //显示当前页面的问题发布
    @Select("select * from question limit #{offset},#{size}")
    List<Question> list(@Param(value = "offset") Integer offset, Integer size);

    //查询问题发布的总数
    @Select("select count(1) from question")
    Integer count();
}

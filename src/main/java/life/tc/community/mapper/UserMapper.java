package life.tc.community.mapper;

import life.tc.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    //将用户登录信息插入数据库
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified,avatar_url,bio) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl},#{bio})")
    void insert(User user);

    //寻找之前已经登陆过的用户
    @Select("select * from user where token = #{token}")
    User findByToken(@Param("token") String token);

    @Select("select * from user where id = #{id}")
    User findByID(@Param("id") Integer creator);
}

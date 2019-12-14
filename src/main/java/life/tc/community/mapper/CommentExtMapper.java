package life.tc.community.mapper;

import life.tc.community.model.Comment;
import life.tc.community.model.CommentExample;
import life.tc.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}
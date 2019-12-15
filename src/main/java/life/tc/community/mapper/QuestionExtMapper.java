package life.tc.community.mapper;

import life.tc.community.model.Question;

import java.util.List;

//这些接口的方法通过myBatis Generator 映射到 resourse里面相应mapper的sql方法
public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);
}

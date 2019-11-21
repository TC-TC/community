package life.tc.community.service;

import life.tc.community.dto.QuestionDTO;
import life.tc.community.mapper.QuestionMapper;
import life.tc.community.mapper.UserMapper;
import life.tc.community.model.Question;
import life.tc.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;

    public List<QuestionDTO> list() {
        List<Question> questions = questionMapper.list();
        List<QuestionDTO> questionDTOlist = new ArrayList<>();
        for(Question question : questions){
         User user = userMapper.findByID(question.getCreator());
         QuestionDTO questionDTO = new QuestionDTO();
         //将question对象里面的值放进questionDTO里面
         BeanUtils.copyProperties(question,questionDTO);
         questionDTO.setUser(user);
         questionDTOlist.add(questionDTO);
        }
        return questionDTOlist;
    }

}

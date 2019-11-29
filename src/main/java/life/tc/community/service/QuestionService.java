package life.tc.community.service;

import life.tc.community.dto.PaginationDTO;
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

    public PaginationDTO list(Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        //问题所有的数量
        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount,page,size);

        if(page <1){
            page = 1;
        }
        if(page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        //size*(page-1),offset为每页第一个问题的编号
        Integer offset = size * (page-1);
        List<Question> questions = questionMapper.list(offset,size);
        List<QuestionDTO> questionDTOlist = new ArrayList<>();


        for(Question question : questions){
         User user = userMapper.findByID(question.getCreator());
         QuestionDTO questionDTO = new QuestionDTO();
         //将question对象里面的值放进paginationDTO里面
         BeanUtils.copyProperties(question,questionDTO);
         questionDTO.setUser(user);
         questionDTOlist.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOlist);

        return paginationDTO;
    }

}

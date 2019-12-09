package life.tc.community.service;

import life.tc.community.dto.PaginationDTO;
import life.tc.community.dto.QuestionDTO;
import life.tc.community.exception.CustomErrorCode;
import life.tc.community.exception.CustomizeException;
import life.tc.community.exception.ICustomizeErrorCode;
import life.tc.community.mapper.QuestionExtMapper;
import life.tc.community.mapper.QuestionMapper;
import life.tc.community.mapper.UserMapper;
import life.tc.community.model.Question;
import life.tc.community.model.QuestionExample;
import life.tc.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private  QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    //index页面的问题显示
    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        //问题所有的数量
        Integer totalCount =  (int)questionMapper.countByExample(new QuestionExample());

        //计算总共需要多少页
        Integer totalPage;
        if(totalCount % size == 0) {
            totalPage = totalCount / size;
        }
        else{
            totalPage = totalCount / size + 1;
        }

        if(page <1){
            page = 1;
        }
        if(page > totalPage) {
            page = paginationDTO.getTotalPage();
        }

        paginationDTO.setPagination(totalPage,page);

        //size*(page-1),offset为每页第一个问题的编号
        Integer offset = size * (page-1);
        //List<Question> questions = questionMapper.list(offset,size);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,size));

        List<QuestionDTO> questionDTOlist = new ArrayList<>();
        for(Question question : questions){
         User user = userMapper.selectByPrimaryKey(question.getCreator());
         QuestionDTO questionDTO = new QuestionDTO();
         //将question对象里面的值放进paginationDTO里面
         BeanUtils.copyProperties(question,questionDTO);
         questionDTO.setUser(user);
         questionDTOlist.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOlist);

        return paginationDTO;
    }

    //profile页面的问题显示
    public PaginationDTO list(long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        //问题所有的数量

        //Integer totalCount = questionMapper.countByUserId(userId);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);

        //计算总共需要多少页
        Integer totalPage;
        if(totalCount % size == 0) {
            totalPage = totalCount / size;
        }
        else{
            totalPage = totalCount / size + 1;
        }

        if(page <1){
            page = 1;
        }
        if(page > totalPage) {
            page = paginationDTO.getTotalPage();
        }
        paginationDTO.setPagination(totalPage,page);


        //size*(page-1),offset为每页第一个问题的编号
        Integer offset = size * (page-1);
        //List<Question> questions = questionMapper.listByUserId(userId,offset,size);
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));

        List<QuestionDTO> questionDTOlist = new ArrayList<>();
        for(Question question : questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question对象里面的值放进paginationDTO里面
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOlist.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOlist);

        return paginationDTO;
    }

    //获取question详情
    public QuestionDTO getById(long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null)
        {
            throw new CustomizeException(CustomErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setUser(user);
        BeanUtils.copyProperties(question,questionDTO);
        return questionDTO;
    }

    //更新question
    public void createOrUpdate(Question question) {
        if(question.getId() == null) {
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else{
            //更新
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            //questionMapper.update(question);
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            //判断是否在跟你更新之前该问题还存在（有可能被删除）
            int updated = questionMapper.updateByExampleSelective(updateQuestion,example);
            if(updated != 1){
                throw new CustomizeException(CustomErrorCode.QUESTION_NOT_FOUND);
            }
        }

    }


    //实现了阅读数量加一
    public void incView(long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}

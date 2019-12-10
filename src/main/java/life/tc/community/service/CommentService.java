package life.tc.community.service;

import life.tc.community.dto.CommentDTO;
import life.tc.community.enums.CommentTypeEnum;
import life.tc.community.exception.CustomErrorCode;
import life.tc.community.exception.CustomizeException;
import life.tc.community.mapper.CommentMapper;
import life.tc.community.mapper.QuestionExtMapper;
import life.tc.community.mapper.QuestionMapper;
import life.tc.community.mapper.UserMapper;
import life.tc.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    //加上事务框架，当一个语句执行失败时，所有语句全部回滚
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            //抛出该问题已经不存在的异常
            throw new CustomizeException(CustomErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            //抛出要评论的该条评论不存在的异常
            throw new CustomizeException(CustomErrorCode.TYPE_PARAM_NOT_FOUND);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论(为二级评论，此时parent_ID为该条评论人ID)
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                //抛出二级评论不存在的异常
                throw new CustomizeException(CustomErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        } else {
            //回复问题（为一级评论，此时parent_ID为发帖人ID）
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                //抛出一级评论不存在的异常
                throw new CustomizeException(CustomErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }

    }

    public List<CommentDTO> listByQuestionId(Long id) {

        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //获取去重的评论人ID
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList();
        //将其转化成相应的userId
        userIds.addAll(commentators);

        //获取userID并且转化为Map (userID -> user)
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long,User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换 comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}

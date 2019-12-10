package life.tc.community.controller;

import life.tc.community.dto.CommentCreateDTO;
import life.tc.community.dto.ResultDTO;
import life.tc.community.exception.CustomErrorCode;
import life.tc.community.model.Comment;
import life.tc.community.model.User;
import life.tc.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request
    ){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
            return ResultDTO.errorOf(CustomErrorCode.NO_LOGIN);
        }
        if (commentCreateDTO.getContent() == null || commentCreateDTO == null || commentCreateDTO.getContent() ==""){
            return ResultDTO.errorOf(CustomErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setType(commentCreateDTO.getType());
        comment.setContent(commentCreateDTO.getContent());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreat(comment.getGmtModified());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }
}

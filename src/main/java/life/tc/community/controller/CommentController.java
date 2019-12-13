package life.tc.community.controller;

import life.tc.community.dto.CommentCreateDTO;
import life.tc.community.dto.CommentDTO;
import life.tc.community.dto.ResultDTO;
import life.tc.community.enums.CommentTypeEnum;
import life.tc.community.exception.CustomErrorCode;
import life.tc.community.model.Comment;
import life.tc.community.model.User;
import life.tc.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public  ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        //返回的json对象还带有一个commentDTOS对象
        return ResultDTO.okOf(commentDTOS);
    }
}

package life.tc.community.controller;
import life.tc.community.cache.TagCache;
import life.tc.community.dto.QuestionDTO;
import life.tc.community.mapper.QuestionMapper;
import life.tc.community.model.Question;
import life.tc.community.model.User;
import life.tc.community.service.QuestionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    //重新编辑发布内容
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model
    ){
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    //第一次发布问题
    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    //创建问题的时候返回相应错误信息
    @PostMapping("/publish")
    public String doPublish(
    @RequestParam(value = "title", required = false) String title,
    @RequestParam(value = "description", required = false) String description,
    @RequestParam(value = "tag", required = false) String tag,
    @RequestParam(value = "id",required = false) Long id,
    HttpServletRequest request,
    Model model
    ){

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());

        if(title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(description == null || description == ""){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if(tag == null || tag== ""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        //判断是否为非法标签
        String invaild = TagCache.Invaild(tag);
        if(StringUtils.isNotBlank(invaild)){
            model.addAttribute("error","标签不能为非法标签 "+invaild);
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";

    }

}


package life.tc.community.advice;


import com.alibaba.fastjson.JSON;
import life.tc.community.dto.ResultDTO;
import life.tc.community.exception.CustomErrorCode;
import life.tc.community.exception.CustomizeException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO ;
            //返回json（说明是评论功能出现了异常，并不需要跳转页面）
            if (e instanceof CustomizeException) {
                resultDTO =  ResultDTO.errorOf((CustomizeException)e);
            } else {
                resultDTO =  ResultDTO.errorOf(CustomErrorCode.SYS_ERROR);
            }
            try{
                //将resultDTO手写返回json格式
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            }catch(IOException ioe){
            }
            return null;
        } else {
            //错误页面跳转
            //在questionService中throw出的异常会放进throwable中
            //再次拦截的都是springMVC可以handler的异常
            if (e instanceof CustomizeException) {
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("message", CustomErrorCode.SYS_ERROR);
            }
            return new ModelAndView("error");
        }
    }
}



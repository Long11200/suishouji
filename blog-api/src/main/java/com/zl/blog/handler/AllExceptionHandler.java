package com.zl.blog.handler;

import com.zl.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author shkstart
 * @create 2022-06-26 10:09
 */
@ControllerAdvice
public class AllExceptionHandler {
    //异常处理,处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handle_Exception(Exception ex){
        ex.printStackTrace();
        return  Result.fail(-999,"系统异常");
    }
}

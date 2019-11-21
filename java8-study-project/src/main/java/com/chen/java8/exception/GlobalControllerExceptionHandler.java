package com.chen.java8.exception;

import com.chen.java8.common.MsgCode;
import com.chen.java8.common.ResponseMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ChenTian
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 全局异常捕捉处理
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseMsg errorHandler(Exception e) {
        logger.error("system error.{}", e);
        return new ResponseMsg(MsgCode.Error);
    }
}

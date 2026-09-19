package com.baffang.common.exception;

import com.baffang.common.enums.ErrorMsg;

/**
 * 自定义业务异常类
 */

public class BusinessException extends RuntimeException {
    private Integer code = 400;
    //只传错误信息
    public BusinessException(String message) {
        this(400,message);
    }
    //传code和信息
    public BusinessException(int code,String message){
        super(message);
        this.code = code;
    }
    //直接传错误枚举
    public BusinessException(ErrorMsg errorMsg){
        this(errorMsg.getCode(), errorMsg.getMsg());
    }
    public int getCode(){
        return code;
    }
}

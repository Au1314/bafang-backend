package com.baffang.common.result;

import com.baffang.common.enums.ErrorMsg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * 
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    @ApiModelProperty("状态码")
    private Integer code;

    @ApiModelProperty("信息")
    private String msg;
    /**
     * 具体数据
     */
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 200;
        result.msg = "OK";
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.msg = "OK";
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(T object, String msg) {
        Result<T> result = success(object);
        result.msg = msg;
        return result;
    }

    /**
     * 注意：error()方法通常在抛出BusinessException异常时由全局异常处理器自动调用并返回
     * 因此通常无需手动调用error方法进行返回
     */

    /**
     * 在枚举类ErrorMsg中定义状态码，并用该方法包装返回
     */
    public static <T> Result<T> error(ErrorMsg errorMsg) {
        Result result = new Result();
        result.msg = errorMsg.getMsg();
        result.code = errorMsg.getCode();
        return result;
    }

    /**
     * 通用报错，状态码 400
     * 不建议使用
     */
    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 400;
        return result;
    }

    // 自定义状态码和报错信息（不建议使用）
    public static <T> Result<T> error(int code, String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = code;
        return result;
    }

    /**
     * 带返回信息的报错
     */
    public static <T> Result<T> error(int code, String msg, Object data) {
        Result result = error(code, msg);
        result.data = data;
        return result;
    }

}

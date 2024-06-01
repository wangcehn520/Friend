package com.bit.springboot.common;

/**
 * 自定义错误码
 *
 * @author bit
 * ""
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    FRIEND_ERROR(50001, "'已是好友'"),
    NULL_USERNAME(11111,"用户名为空"),
    NULL_USERACCOUNT(22222,"账号名为空"),
    NULL_PASSWORD(33333,"密码长度小于6位或为空"),
    USER_EXISTS(33333,"用户已存在"),
    NULL_ADD(33333,"标题或内容不能为空"),
    USER_EXIST(33333,"用户已存在"),
    ALL_PASSWORD(33333,"两次密码不一致");
    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

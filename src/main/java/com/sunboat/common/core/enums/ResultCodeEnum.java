package com.sunboat.common.core.enums;

import lombok.Getter;

/**
 * 返回状态码枚举
 */
@Getter
public enum ResultCodeEnum {
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    
    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    // 业务错误
    BUSINESS_ERROR(1000, "业务逻辑错误"),
    DATA_VALIDATION_ERROR(1001, "数据验证失败"),
    DB_ERROR(1002, "数据库操作错误");

    /**
     * 状态码
     */
    private final int code;
    
    /**
     * 消息
     */
    private final String message;

    ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
    
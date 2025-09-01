package com.company.common.core.result;


import com.company.common.core.enums.ResultCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果封装
 */
@Data
public class RtnResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 分页信息，有数据且是分页查询时才返回
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageInfo pageInfo;

    /**
     * 私有构造方法
     */
    private RtnResult() {}

    /**
     * 成功返回
     */
    public static <T> RtnResult<T> success(T data) {
        RtnResult<T> rtnResult = new RtnResult<>();
        rtnResult.code = ResultCodeEnum.SUCCESS.getCode();
        rtnResult.message = ResultCodeEnum.SUCCESS.getMessage();
        rtnResult.data = data;
        return rtnResult;
    }

    /**
     * 带分页信息的成功返回
     */
    public static <T> RtnResult<T> success(T data, long total, int pageNum, int pageSize) {
        RtnResult<T> rtnResult = success(data);
        rtnResult.pageInfo = new PageInfo(total, pageNum, pageSize);
        return rtnResult;
    }

    /**
     * 失败返回
     */
    public static <T> RtnResult<T> fail(int code, String message) {
        RtnResult<T> rtnResult = new RtnResult<>();
        rtnResult.code = code;
        rtnResult.message = message;
        return rtnResult;
    }

    /**
     * 失败返回
     */
    public static <T> RtnResult<T> fail(ResultCodeEnum resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 失败返回
     */
    public static <T> RtnResult<T> fail(ResultCodeEnum resultCode, String message) {
        return fail(resultCode.getCode(), message);
    }

    /**
     * 分页信息内部类
     */
    @Data
    public static class PageInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 总记录数
         */
        private long total;

        /**
         * 当前页码
         */
        private int pageNum;

        /**
         * 每页条数
         */
        private int pageSize;

        /**
         * 总页数
         */
        private int totalPages;

        public PageInfo(long total, int pageNum, int pageSize) {
            this.total = total;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        }
    }
}
    
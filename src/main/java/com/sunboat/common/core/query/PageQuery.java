package com.sunboat.common.core.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基础类
 */
@Data
public class PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码，默认第1页
     */
    @ApiModelProperty(value = "当前页码", example = "1")
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10条
     */
    @ApiModelProperty(value = "每页条数", example = "10")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private String orderByColumn;

    /**
     * 排序方式（asc/desc）
     */
    @ApiModelProperty(value = "排序方式（asc/desc）")
    private String isAsc = "asc";

    /**
     * 计算分页起始位置
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}

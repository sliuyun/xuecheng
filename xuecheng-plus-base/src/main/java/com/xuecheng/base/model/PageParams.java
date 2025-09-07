package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

/**
 * @description 分页查询通用参数
 * @author Mr.M
 * @date 2022/9/6 14:02
 * @version 1.0
 */
@Data
@ToString
public class PageParams {
    // 分页页码，默认是1
    private Long pageNo = 1L;

    // 分页大小，默认30
    private Long pageSize = 60L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}

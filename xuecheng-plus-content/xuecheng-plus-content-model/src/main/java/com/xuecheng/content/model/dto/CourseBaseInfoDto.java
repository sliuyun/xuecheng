package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import lombok.Data;

/**
 * 课程基础信息，用于返回给前端
 * 这个对象相当于 CourseBase + CourseMarket，对应于数据库表 course_base + course_market
 * 这个Dto是给前端的返回数据，所以这里用Dto不太规范
 * @description 课程基本信息dto
 * @author Mr.M
 * @date 2022/9/7 17:44
 * @version 1.0
 */
@Data
public class CourseBaseInfoDto extends CourseBase {


    /**
     * 收费规则，对应数据字典
     */
    private String charge;

    /**
     * 价格
     */
    private Float price;


    /**
     * 原价
     */
    private Float originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

    /**
     * 大分类名称
     */
    private String mtName;

    /**
     * 小分类名称
     */
    private String stName;

}

package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

public interface CourseBaseInfoService {

    PageResult<CourseBase> queryCourseBaseInfo(Long companyId, PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 添加课程基本信息
     * @param companyId  教学机构id
     * @param addCourseDto  课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author Mr.M
     * @date 2022/9/7 17:51
     */
    CourseBaseInfoDto createCourseBase(Long companyId, String username, AddCourseDto addCourseDto);

    /**
     * 修改课程基础信息
     * @param companyId    机构id
     * @param editCourseDto 修改后课程基本信息
     * @param username     课程修改人
     * @return             修改后课程基本信息
     */
    CourseBaseInfoDto modifyCourseBaseInfo(Long companyId,String username, EditCourseDto editCourseDto);

    /**
     * 根据课程id查询课程基础信息
     * @param courseId 课程id
     * @return 课程基础信息
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);
}

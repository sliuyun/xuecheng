package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseTeacher;
import lombok.Data;

import java.util.List;

@Data
public class CoursePreviewDto {

    // 课程基本信息，包含CourseBase和CourseMarket
    CourseBaseInfoDto courseBase;

    // 课程计划信息
    List<TeachplanDto> teachplans;


    // 课程师资信息
    List<CourseTeacherDto> courseTeachers;
}

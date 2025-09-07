package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 在新增/编辑教师接口中，该 对象是请求参数
 * 在查询教师列表方法中，该对象是返回参数
 */
@Data
@ApiModel(value="CourseTeacherDto", description="课程-老师请求/返回参数")
public class CourseTeacherDto {

    // 课程id
    private Long courseId;

    // 课程发布id
    private Long coursePubId;

    // 课程老师id
    private Long courseTeacherId;

    // 课程创建时间
    private LocalDateTime createDate;

    // 教师简介
    private String introduction;

    // 教师照片
    private String photograph;

    // 教师职位
    private String position;

    // 教师姓名
    @NotNull
    private String teacherName;
}

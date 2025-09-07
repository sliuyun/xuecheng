package com.xuecheng.content.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 修改课程参数dto
 * 与添加课程参数dto的唯一区别在于多了课程id
 */
@Data
public class EditCourseDto extends AddCourseDto {

    @NotNull
    Long id;
}

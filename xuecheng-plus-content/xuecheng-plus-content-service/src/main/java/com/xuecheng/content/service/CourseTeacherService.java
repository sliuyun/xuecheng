package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseTeacherDto;

import java.util.List;

public interface CourseTeacherService {

    public List<CourseTeacherDto> findAll(Long courseId);

    void saveCourseTeacher(CourseTeacherDto courseTeacherDto);

    void removeCourseTeacher(Long coursebaseId, Long courseTeacherId);
}

package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    private final CourseTeacherMapper courseTeacherMapper;

    private final CoursePublishMapper coursePublishMapper;

    private final CourseBaseMapper courseBaseMapper;

    public CourseTeacherServiceImpl(CourseTeacherMapper courseTeacherMapper, CoursePublishMapper coursePublishMapper, CourseBaseMapper courseBaseMapper) {
        this.courseTeacherMapper = courseTeacherMapper;
        this.coursePublishMapper = coursePublishMapper;
        this.courseBaseMapper = courseBaseMapper;
    }

    @Override
    public List<CourseTeacherDto> findAll(Long courseId) {
        // 根据课程id查询课程的所有老师
        Wrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(wrapper);

        // 返回结果
        List<CourseTeacherDto> courseTeacherDtos = courseTeachers.stream().map(courseTeacher -> {
            CourseTeacherDto courseTeacherDto = new CourseTeacherDto();
            BeanUtils.copyProperties(courseTeacher, courseTeacherDto);
            courseTeacherDto.setCourseTeacherId(courseTeacher.getId());
            return courseTeacherDto;
        }).collect(Collectors.toList());

        // 去课程基础表中查询课程
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase.getStatus() == "203002") {
            // 如果课程已发布，那么发布课程表的id就是课程id
            for(CourseTeacherDto courseTeacherDto : courseTeacherDtos) {
                courseTeacherDto.setCoursePubId(courseId);
            }
        }

        return courseTeacherDtos;
    }

    @Override
    public void saveCourseTeacher(CourseTeacherDto courseTeacherDto) {
        CourseTeacher courseTeacher = new CourseTeacher();

        // 如果课程教师id为null，说明是新增，否则是修改
        if(courseTeacherDto.getCourseTeacherId() == null) {
            // 新增教师
            BeanUtils.copyProperties(courseTeacherDto, courseTeacher);
            courseTeacher.setCreateDate(LocalDateTime.now());
            courseTeacherMapper.insert(courseTeacher);
            return ;
        }

        // 更新教师
        BeanUtils.copyProperties(courseTeacherDto, courseTeacher);
        LambdaUpdateWrapper<CourseTeacher> updateWrapper = new LambdaUpdateWrapper<CourseTeacher>()
                .eq(CourseTeacher::getId, courseTeacherDto.getCourseTeacherId());
        courseTeacherMapper.update(courseTeacher, updateWrapper);
    }

    @Override
    public void removeCourseTeacher(Long coursebaseId, Long courseTeacherId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getId, courseTeacherId)
                .eq(CourseTeacher::getCourseId, coursebaseId);
        courseTeacherMapper.delete(wrapper);
    }
}

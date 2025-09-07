package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 课程老师接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Api(value = "课程老师接口",tags = "课程老师接口")
@RestController
public class CourseTeacherController {

    private final CourseTeacherService courseTeacherService;

    public CourseTeacherController(CourseTeacherService courseTeacherService) {
        this.courseTeacherService = courseTeacherService;
    }

    @ApiOperation("根据课程id查询教师列表")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacherDto>  getCourseTeacherList(@PathVariable Long courseId){
        return courseTeacherService.findAll(courseId);
    }


    @ApiOperation("保存教师信息")
    @PostMapping("/courseTeacher")
    public void saveCourseTeacher(@RequestBody @Validated CourseTeacherDto courseTeacherDto){
        courseTeacherService.saveCourseTeacher(courseTeacherDto);
    }

    @ApiOperation("根据课程id和教师id移除教师")
    @DeleteMapping("/courseTeacher/course/{coursebaseId}/{courseTeacherId}")
    public void removeCourseTeacher(@PathVariable @NotNull Long coursebaseId, @PathVariable @NotNull Long courseTeacherId){
        courseTeacherService.removeCourseTeacher(coursebaseId, courseTeacherId);
    }

}

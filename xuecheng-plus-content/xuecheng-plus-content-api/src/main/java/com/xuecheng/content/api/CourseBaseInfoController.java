package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.impl.CourseBaseInfoServiceImpl;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @description 课程信息编辑接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
@Validated
@RestController
public class CourseBaseInfoController {

    private final CourseBaseInfoServiceImpl courseBaseInfoService;

    public CourseBaseInfoController(CourseBaseInfoServiceImpl courseBaseInfoService) {
        this.courseBaseInfoService = courseBaseInfoService;
    }

    /**
     * 查询课程基本信息
     * @param pageParams            分页参数
     * @param queryCourseParamsDto  课程查询参数
     * @return 课程基本信息列表分页结果
     */
    @PreAuthorize("hasAnyAuthority('xc_teachmanager_course_list')")
    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String companyId = null;
        if(user != null) {
            companyId = user.getCompanyId();
        }



        return courseBaseInfoService.queryCourseBaseInfo(Long.valueOf(companyId), pageParams, queryCourseParamsDto);
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfoDto addCourseBase(@RequestBody @Validated AddCourseDto addCourseDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String companyId = user.getCompanyId();
        String username = user.getUsername();
        return courseBaseInfoService.createCourseBase(Long.valueOf(companyId), username, addCourseDto);
    }

    @ApiOperation("根据课程id查询课程")
    @GetMapping("/course/{id}")
    public CourseBaseInfoDto getCourseBaseInfoById(@PathVariable @NotNull Long id) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user);

        return courseBaseInfoService.getCourseBaseInfo(id);
    }

    @ApiOperation("修改课程信息（基本信息和营销信息）")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBaseInfo(@Validated @RequestBody EditCourseDto editCourseDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        assert user != null;
        Long companyId = null;
        if(user.getCompanyId() != null) {
            companyId = Long.valueOf(user.getCompanyId());
        }
        String username = user.getUsername();

        return courseBaseInfoService.modifyCourseBaseInfo(companyId, username, editCourseDto);
    }
}

package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    private final CourseBaseMapper courseBaseMapper;

    private final CourseMarketMapper courseMarketMapper;

    private final CourseCategoryMapper courseCategoryMapper;

    public CourseBaseInfoServiceImpl(CourseBaseMapper courseBaseMapper, CourseMarketMapper courseMarketMapper, CourseCategoryMapper courseCategoryMapper) {
        this.courseBaseMapper = courseBaseMapper;
        this.courseMarketMapper = courseMarketMapper;
        this.courseCategoryMapper = courseCategoryMapper;
    }

    @Override
    public PageResult<CourseBase> queryCourseBaseInfo(Long companyId, PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 1.构造分页查询条件
        //构建查询条件对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //构建查询条件，根据课程名称查询
        // 如果前面条件为真，则会带上后面的查询条件；否则不带上
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        //构建查询条件，根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //构建查询条件，根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());
        // 教学机构
        queryWrapper.eq(CourseBase::getCompanyId, companyId);

        IPage<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 2.进行分页查询
        IPage<CourseBase> courseBaseIPage = courseBaseMapper.selectPage(page, queryWrapper);
        // 3.返回查询结果
        return new PageResult<>(courseBaseIPage.getRecords(), courseBaseIPage.getTotal(), courseBaseIPage.getPages(), courseBaseIPage.getSize());
    }

    /**
     * @param companyId    教学机构id
     * @param dto 课程基本信息
     * @param username 当前用户名称
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @description 添加课程基本信息
     * @author Mr.M
     * @date 2022/9/7 17:51
     */
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, String username, AddCourseDto dto) {

        //合法性校验
        // if (StringUtils.isBlank(dto.getName())) {
        //     throw new RuntimeException("课程名称为空");
        // }
        //
        // if (StringUtils.isBlank(dto.getMt())) {
        //     throw new RuntimeException("课程分类为空");
        // }
        //
        // if (StringUtils.isBlank(dto.getSt())) {
        //     throw new RuntimeException("课程分类为空");
        // }
        //
        // if (StringUtils.isBlank(dto.getGrade())) {
        //     throw new RuntimeException("课程等级为空");
        // }
        //
        // if (StringUtils.isBlank(dto.getTeachmode())) {
        //     throw new RuntimeException("教育模式为空");
        // }
        //
        // if (StringUtils.isBlank(dto.getUsers())) {
        //     throw new RuntimeException("适应人群为空");
        // }
        //
        // if (StringUtils.isBlank(dto.getCharge())) {
        //     throw new RuntimeException("收费规则为空");
        // }
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //课程修改人名称(这里只添加修改人名称，因为创建人必须要查数据库才知道)
        courseBaseNew.setCreatePeople(username);
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if(insert<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }
        //向课程营销表保存课程营销信息
        //课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        Long courseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        int i = saveCourseMarket(courseMarketNew);
        if(i<=0){
            throw new RuntimeException("保存课程营销信息失败");
        }
        //查询课程基本信息及营销信息并返回
        return getCourseBaseInfo(courseId);
    }

    /**
     * 修改课程基础信息
     * 注意，课程有 base信息和market信息
     * @param companyId    机构id
     * @param editCourseDto 修改后课程基本信息
     * @return 修改后课程基本信息
     */
    @Override
    public CourseBaseInfoDto modifyCourseBaseInfo(Long companyId, String username, EditCourseDto editCourseDto) {
        // 课程id
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);

        // 能跨机构修改课程
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("不能跨机构修改课程");
        }

        // 封装课程基本信息
        BeanUtils.copyProperties(editCourseDto,courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        // 修改人
        courseBase.setChangePeople(username);
        // 修改课程基本信息
        courseBaseMapper.updateById(courseBase);

        // 修改课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        saveCourseMarket(courseMarket);

        // 查询课程信息
        return getCourseBaseInfo(courseBase.getId());
    }

    /**
     * 根据课程id查询课程基础信息
     *
     * @param courseId 课程id
     * @return 课程基础信息
     */
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        // 根据课程id查询CourseBase
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        // 根据课程id查询CourseMarket，在course_market表中id等于courseId
        LambdaQueryWrapper<CourseMarket> wrapper = new LambdaQueryWrapper<CourseMarket>()
                .eq(CourseMarket::getId, courseId);
        CourseMarket courseMarket = courseMarketMapper.selectOne(wrapper);

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null) {
            // 有些课程的课程营销信息暂未添加
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        return courseBaseInfoDto;
    }


    //保存课程营销信息
    private int saveCourseMarket(CourseMarket courseMarketNew){
        //收费规则
        String charge = courseMarketNew.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if(courseMarketObj == null){
            return courseMarketMapper.insert(courseMarketNew);
        }else{
            BeanUtils.copyProperties(courseMarketNew,courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }

    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(long courseId){

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;

    }
}

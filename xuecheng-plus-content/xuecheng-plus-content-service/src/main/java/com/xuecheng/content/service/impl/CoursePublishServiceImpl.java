package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    // 在服务层对象注入其他服务层对象,预览方法比较特殊
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    TeachplanService teachplanService;
    @Autowired
    CourseTeacherService courseTeacherService;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private CoursePublishMapper coursePublishMapper;
    @Autowired
    private MediaServiceClient mediaServiceClient;

    /**
     * @param courseId 课程id
     * @return File 静态化文件
     * @description 课程静态化
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        //静态化文件
        File htmlFile  = null;

        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass().getClassLoader(),"/templates"));

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");


            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("course",".html");
            log.debug("课程静态化，生成静态文件:{}",htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("课程静态化异常:{}",e.toString());
            XueChengPlusException.cast("课程静态化异常");
        }

        return htmlFile;
    }

    /**
     * @param courseId
     * @param file     静态化文件
     * @return void
     * @description 上传课程静态化页面
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String course = mediaServiceClient.upload(multipartFile, "course/"+courseId+".html");
        if(course==null){
            XueChengPlusException.cast("上传静态文件异常");
        }
    }

    /**
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * @description 获取课程预览信息
     * @author Mr.M
     * @date 2022/9/16 15:36
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();

        // 获取课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);

        //课程计划信息
        List<TeachplanDto> teachplanTree= teachplanService.getTreeNodes(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);

        // 师资信息
        List<CourseTeacherDto> teacherDtos = courseTeacherService.findAll(courseId);
        coursePreviewDto.setCourseTeachers(teacherDtos);

        return coursePreviewDto;
    }

    /**
     * @param companyId
     * @param courseId  课程id
     * @return void
     * @description 提交审核
     * @author Mr.M
     * @date 2022/9/18 10:31
     */
    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        CourseBaseInfoDto courseBase = courseBaseInfoService.getCourseBaseInfo(courseId);

        // 约束校验
        // 课程状态为审核中，不允许再次提交审核
        String auditStatus = courseBase.getAuditStatus();
        if(auditStatus.equals("202003")){
            XueChengPlusException.cast("课程审核中，不能重复提交");
        }

        // 本机构只能提交本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("不允许提交其它机构的课程");
        }

        // 课程图片是否填写
        if(courseBase.getPic() == null || courseBase.getPic().equals("")){
            XueChengPlusException.cast("提交失败，请上传课程图片");
        }


        // 添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();

        // 课程基本信息加上部分营销信息
        // 这里拷贝的时候只会将CourseBase部分拷过去
        BeanUtils.copyProperties(courseBase, coursePublishPre);

        // 将课程营销信息JSON放入课程预发布表
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String s = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(s);

        // 添加课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.getTreeNodes(courseId);
        if(teachplanTree.size()<=0){
            XueChengPlusException.cast("提交失败，还没有添加课程计划");
        }
        String string = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(string);

        // 设置预发布记录状态，已提交
        coursePublishPre.setStatus("202003");

        // 教学机构id
        coursePublishPre.setCompanyId(companyId);

        // 提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());

        // 大小分类名称
        CourseCategory mtCategory = courseCategoryMapper.selectById(courseBase.getMt());
        CourseCategory stCategory = courseCategoryMapper.selectById(courseBase.getSt());
        coursePublishPre.setMtName(mtCategory.getName());
        coursePublishPre.setStName(stCategory.getName());

        // 添加课程预发布记录
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPreUpdate == null){
            //添加课程预发布记录
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        // 更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }


    /**
     * @param companyId 机构id
     * @param courseId  课程id
     * @return void
     * @description 课程发布接口
     * @author Mr.M
     * @date 2022/9/20 16:23
     */
    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {
        // 查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);

        // 非空校验
        if(coursePublishPre == null){
            XueChengPlusException.cast("请先提交课程审核，审核通过才可以发布");
        }

        // 本机构只允许提交本机构课程
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("不允许提交其它机构的课程");
        }

        // 课程审核状态
        if(!coursePublishPre.getStatus().equals("202004")){
            XueChengPlusException.cast("课程尚未通过审核，不能发布");
        }

        // 保存课程发布信息
        saveCoursePublish(coursePublishPre);

        // 修改课程基本信息中的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);

        // 保存消息表
        saveCoursePublishMessage(courseId);

        // 删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish ;
    }

    /**
     * @description 保存课程发布信息，这个方法用于新增课程发布和修改课程发布
     * @param coursePublishPre  课程预发布表
     * @return void
     * @author Mr.M
     * @date 2022/9/20 16:32
     */
    private void saveCoursePublish(CoursePublishPre coursePublishPre){
        // 课程发布对象
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus("203002");

        // 查询课程发布对象
        // 课程发布表和课程预发布表的id都是课程id
        CoursePublish publish = coursePublishMapper.selectById(coursePublishPre.getId());
        if(publish == null){
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
    }

    /**
     * @description 保存消息表记录
     * @param courseId  课程id
     * @return void
     * @author Mr.M
     * @date 2022/9/20 16:32
     */
    private void saveCoursePublishMessage(Long courseId){
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}

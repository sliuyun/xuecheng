package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {
    private final TeachplanMapper teachplanMapper;

    private final TeachplanMediaMapper teachplanMediaMapper;

    public TeachplanServiceImpl(TeachplanMapper teachplanMapper, TeachplanMediaMapper teachplanMediaMapper) {
        this.teachplanMapper = teachplanMapper;
        this.teachplanMediaMapper = teachplanMediaMapper;
    }

    @Override
    public List<TeachplanDto> getTreeNodes(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long id = saveTeachplanDto.getId();

        if(id == null) {
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            int teachplanCount = getTeachplanCount(saveTeachplanDto.getParentid(), saveTeachplanDto.getCourseId());
            // 新增前确定计划的排序字段
            teachplan.setOrderby(teachplanCount);
            teachplanMapper.insert(teachplan);
        } else {
            // 改的时候先查，因为 saveTeachplanDto对象的信息有限，只有修改后的一些新字段值，旧字段值没有
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void moveupTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan.getOrderby() == 1) {
            XueChengPlusException.cast("不能上移");
        }

        // 执行上移
        // 查询上一个计划
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getOrderby, teachplan.getOrderby() - 1);
        Teachplan prevTeachplan = teachplanMapper.selectOne(wrapper);
        // 下移上一个计划
        teachplanMapper.updateOrderby(prevTeachplan.getId(), prevTeachplan.getOrderby() + 1);
        // 上移当前计划
        teachplanMapper.updateOrderby(teachplanId, teachplan.getOrderby() - 1);
    }

    @Override
    public void movedownTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        // 假设当前计划为插入计划，调用 getTeachplanCount()方法可以得到它的插入位置，插入位置减一就是 当前计划的父计划的子计划数
        if(getTeachplanCount(teachplan.getParentid(), teachplan.getCourseId()) - 1 == teachplan.getOrderby()) {
            XueChengPlusException.cast("不能下移");
        }

        // 执行下移
        // 查询下一个计划
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getOrderby, teachplan.getOrderby() + 1);
        Teachplan prevTeachplan = teachplanMapper.selectOne(wrapper);
        // 上移下一个计划
        teachplanMapper.updateOrderby(prevTeachplan.getId(), prevTeachplan.getOrderby() - 1);
        // 下移当前计划
        teachplanMapper.updateOrderby(teachplanId, teachplan.getOrderby() + 1);
    }

    @Override
    public void deleteTeachplan(Long teachplanId) {
        Teachplan rmdTeachplan = teachplanMapper.selectById(teachplanId);
        // 当前计划的兄弟数（包括自己）
        int total = getTeachplanCount(rmdTeachplan.getParentid(), rmdTeachplan.getCourseId()) - 1;
        if(total != rmdTeachplan.getOrderby()) {
            // 待删除计划下面还有计划，先将下面计划的排序字段都减1
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<Teachplan>()
                    .eq(Teachplan::getCourseId, rmdTeachplan.getCourseId())
                    .eq(Teachplan::getParentid, rmdTeachplan.getParentid())
                    .gt(Teachplan::getOrderby, rmdTeachplan.getOrderby());
            List<Teachplan> teachplans = teachplanMapper.selectList(wrapper);
            for(Teachplan teachplan : teachplans) {
                teachplanMapper.updateOrderby(teachplan.getId(), teachplan.getOrderby() - 1);
            }
        }
        if(rmdTeachplan.getGrade() == 1) {
            // 当前计划是一级计划，需要删除它的所有子计划，而当前计划是二级计划时不需要
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<Teachplan>().eq(Teachplan::getParentid, rmdTeachplan.getId());
            List<Teachplan> teachplans = teachplanMapper.selectList(wrapper);
            for(Teachplan teachplan : teachplans) {
                teachplanMapper.deleteById(teachplan.getId());
            }
        }
        // 删除当前计划
        teachplanMapper.deleteById(rmdTeachplan);

        // 删除与媒资的绑定
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId, rmdTeachplan.getId());
        List<TeachplanMedia> teachplanMedia = teachplanMediaMapper.selectList(wrapper);
        if(teachplanMedia.size() == 1) {
            teachplanMediaMapper.delete(wrapper);
        }
    }

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @author Mr.M
     * @date 2022/9/14 22:20
     */
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {

        // 查出 原课程计划-媒资绑定记录
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId());
        TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(queryWrapper);

        if(teachplanMedia == null) {
            // 绑定关系为null，先初始化几个参数
            teachplanMedia = new TeachplanMedia();
            teachplanMedia.setTeachplanId(bindTeachplanMediaDto.getTeachplanId());
            // 先根据 课程计划id 查出 课程计划，然后从 课程计划获取 课程id
            Teachplan teachplan = teachplanMapper.selectById(bindTeachplanMediaDto.getTeachplanId());
            teachplanMedia.setCourseId(teachplan.getCourseId());
            teachplanMedia.setCreateDate(LocalDateTime.now());
        }

        // 设置前端传过来的参数
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMediaMapper.updateById(teachplanMedia);

        // 再根据 id 是否为空，选择插入或更新
        if(teachplanMedia.getId() == null) {
            teachplanMediaMapper.insert(teachplanMedia);
        } else {
            teachplanMediaMapper.updateById(teachplanMedia);
        }

        return null;
    }

    @Override
    public List<TeachplanMedia> getAllTeachplanMedia(String filemd5) {
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<TeachplanMedia>()
                .eq( TeachplanMedia::getMediaId, filemd5);
        return teachplanMediaMapper.selectList(wrapper);
    }

    // 返回课程计划插入时的排序字段值
    private int getTeachplanCount(Long parentid, Long course_id) {
        LambdaQueryWrapper<Teachplan> lambdaQueryWrapper = new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getParentid, parentid)
                .eq(Teachplan::getCourseId, course_id);

        return teachplanMapper.selectCount(lambdaQueryWrapper) + 1;
    }
}

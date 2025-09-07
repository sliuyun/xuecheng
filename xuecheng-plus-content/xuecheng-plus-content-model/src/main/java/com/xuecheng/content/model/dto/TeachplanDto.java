package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @description 课程计划树型结构dto，用于返回给前端，包含3部分：课程计划、媒体资源和子节点
 * 在查询时需要返回计划属性、子节点属性，媒资对象（1级节点通常没有媒资属性，从2级开始才有）
 * @author Mr.M
 * @date 2022/9/9 10:27
 * @version 1.0
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {

    //课程计划关联的媒资信息
    TeachplanMedia teachplanMedia;

    //子结点
    List<TeachplanDto> teachPlanTreeNodes;

}

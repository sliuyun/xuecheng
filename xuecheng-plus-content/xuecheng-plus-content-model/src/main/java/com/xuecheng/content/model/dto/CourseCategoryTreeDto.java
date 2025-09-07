package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;

import java.io.Serializable;
import java.util.List;

/**
 * @description 课程分类树型结点dto
 * 这个Dto是给前端的返回数据，所以这里用Dto不太规范
 * @author Mr.M
 * @date 2022/9/7 15:16
 * @version 1.0
 */
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    // 子节点
    List<CourseCategoryTreeDto> childrenTreeNodes;

    public List<CourseCategoryTreeDto> getChildrenTreeNodes() {
        return childrenTreeNodes;
    }

    public void setChildrenTreeNodes(List<CourseCategoryTreeDto> childrenTreeNodes) {
        this.childrenTreeNodes = childrenTreeNodes;
    }
}

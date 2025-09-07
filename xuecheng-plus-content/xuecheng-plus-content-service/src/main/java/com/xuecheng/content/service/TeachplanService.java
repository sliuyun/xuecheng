package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TeachplanService {

    public List<TeachplanDto> getTreeNodes(Long courseId);

    public void saveTeachplan( @RequestBody SaveTeachplanDto saveTeachplanDto);

    void moveupTeachplan(Long teachplanId);

    void movedownTeachplan(Long teachplanId);

    void deleteTeachplan(Long teachplanId);

    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);


    List<TeachplanMedia> getAllTeachplanMedia(String filemd5);
}

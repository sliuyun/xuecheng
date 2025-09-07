package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 课程计划编辑接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Validated
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
public class TeachplanController {

    private final TeachplanService teachplanService;

    public TeachplanController(TeachplanService teachplanService) {
        this.teachplanService = teachplanService;
    }

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.getTreeNodes(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody @Validated SaveTeachplanDto saveTeachplanDto){
        teachplanService.saveTeachplan(saveTeachplanDto);
    }


    @ApiOperation("上移课程计划")
    @PostMapping("/teachplan/moveup/{teachplanId}")
    public void moveupTeachplan(@PathVariable @NotNull Long teachplanId) {
        teachplanService.moveupTeachplan(teachplanId);
    }


    @ApiOperation("下移课程计划")
    @PostMapping("/teachplan/movedown/{teachplanId}")
    public void movedownTeachplan(@PathVariable @NotNull Long teachplanId) {
        teachplanService.movedownTeachplan(teachplanId);
    }


    @ApiOperation("课程计划id删除计划")
    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachplan(@PathVariable @NotNull Long teachplanId) {
        teachplanService.deleteTeachplan(teachplanId);
    }


    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation(value = "查询所有媒资与计划绑定")
    @GetMapping("/r/teachplan/association/media")
    public int getAssociationMedia(@NotNull @RequestParam String filemd5){
        List<TeachplanMedia> allTeachplanMedia = teachplanService.getAllTeachplanMedia(filemd5);
        return allTeachplanMedia.size();
    }
}

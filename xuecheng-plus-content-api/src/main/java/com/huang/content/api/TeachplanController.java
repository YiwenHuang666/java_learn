package com.huang.content.api;

import com.huang.content.model.dto.BindTeachplanMediaDto;
import com.huang.content.model.dto.SaveTeachplanDto;
import com.huang.content.model.dto.TeachplanDto;
import com.huang.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> teachplanTree =  teachplanService.findTeachplanTree(courseId);
        return teachplanTree;
    }


    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{deleteId}")
    public void saveTeachplan( @PathVariable Long deleteId){
        teachplanService.deleteTeachplanById(deleteId);
        return;
    }

    @ApiOperation("课程计划排序移动")
    @PostMapping("/teachplan/{moveStyle}/{moveId}")
    public void moveTeachplan(@PathVariable String moveStyle, @PathVariable Long moveId){
        teachplanService.moveTeachplan(moveStyle,moveId);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

}

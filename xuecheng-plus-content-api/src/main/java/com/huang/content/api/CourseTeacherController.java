package com.huang.content.api;

import com.huang.content.model.dto.AddCourseTeacherDto;
import com.huang.content.model.dto.TeachplanDto;
import com.huang.content.model.po.CourseTeacher;
import com.huang.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "课程教师编辑接口",tags = "课程教师编辑接口")
public class CourseTeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("查询教师接口")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeachers(@PathVariable Long courseId){
         return courseTeacherService.selectTeacherByCourseId(courseId);
    }

    @ApiOperation("添加教师接口")
    @PostMapping("/courseTeacher")
    public List<CourseTeacher> addCourseTeacher(@RequestBody @Validated AddCourseTeacherDto addCourseTeacherDto){
        return courseTeacherService.addTeacher(addCourseTeacherDto);
    }

    @ApiOperation("修改教师接口")
    @PutMapping("/courseTeacher")
    public CourseTeacher updateCourseTeacher(@RequestBody @Validated CourseTeacher courseTeacher){
        return courseTeacherService.updateTeacher(courseTeacher);
    }

    @ApiOperation("删除教师接口")
    @DeleteMapping("/courseTeacher/course/{courseBaseId}/{courseTeacherId}")
    public void updateCourseTeacher(@PathVariable Long courseBaseId,@PathVariable Long courseTeacherId){
        courseTeacherService.deleteTeacher(courseBaseId,courseTeacherId);
    }

}

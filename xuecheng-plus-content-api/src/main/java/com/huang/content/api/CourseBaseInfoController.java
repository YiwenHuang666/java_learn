package com.huang.content.api;

import com.huang.base.model.PageParams;
import com.huang.base.model.PageResult;
import com.huang.content.config.SecurityUtil;
import com.huang.content.model.dto.AddCourseDto;
import com.huang.content.model.dto.CourseBaseInfoDto;
import com.huang.content.model.dto.EditCourseDto;
import com.huang.content.model.dto.QueryCourseParamsDto;
import com.huang.content.model.po.CourseBase;
import com.huang.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @PostMapping("course/list")
    @ApiOperation("课程查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto CourseParamsDto) {

        SecurityUtil.XcUser user = SecurityUtil.getUser();

        Long companyId = Long.parseLong(user.getCompanyId());

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(companyId, pageParams, CourseParamsDto);

        return courseBasePageResult;
    }

    @ApiOperation("新增课程")
    @PostMapping("course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){
        Long companyId = 1232141425L;
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        return courseBase;
    }

    @ApiOperation("根据课程id查询接口")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println(principal);
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user.getUsername());
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseById(courseId);
        return courseBaseInfoDto;
    }

    @ApiOperation("修改课程")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        Long companyId = 1232141425L;
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        return courseBaseInfoDto;
    }

    @ApiOperation("删除课程")
    @PutMapping("/course/{courseBaseId}")
    public void deleteCourseBaseById(@PathVariable Long courseBaseId){
        courseBaseInfoService.deleteCourseBaseById(courseBaseId);
    }
}

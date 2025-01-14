package com.huang.content.service;

import com.huang.base.model.PageParams;
import com.huang.base.model.PageResult;
import com.huang.content.model.dto.AddCourseDto;
import com.huang.content.model.dto.CourseBaseInfoDto;
import com.huang.content.model.dto.EditCourseDto;
import com.huang.content.model.dto.QueryCourseParamsDto;
import com.huang.content.model.po.CourseBase;

public interface CourseBaseInfoService {
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto courseParamsDto);

    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    public CourseBaseInfoDto getCourseBaseById(Long courseId);

    public CourseBaseInfoDto updateCourseBase(Long companyId,EditCourseDto editCourseDto);

    void deleteCourseBaseById(Long courseBaseId);
}

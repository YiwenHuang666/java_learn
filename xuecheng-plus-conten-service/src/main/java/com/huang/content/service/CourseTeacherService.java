package com.huang.content.service;

import com.huang.content.model.dto.AddCourseTeacherDto;
import com.huang.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    public List<CourseTeacher> selectTeacherByCourseId(Long courseId);

    public List<CourseTeacher> addTeacher(AddCourseTeacherDto addCourseTeacherDto);

    CourseTeacher updateTeacher(CourseTeacher courseTeacher);

    void deleteTeacher(Long courseBaseId, Long courseTeacherId);
}

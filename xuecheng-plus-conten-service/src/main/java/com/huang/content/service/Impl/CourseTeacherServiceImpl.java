package com.huang.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huang.content.mapper.CourseTeacherMapper;
import com.huang.content.model.dto.AddCourseTeacherDto;
import com.huang.content.model.po.CourseTeacher;
import com.huang.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> selectTeacherByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        return courseTeacherMapper.selectList(queryWrapper.eq(CourseTeacher::getCourseId, courseId));
    }

    @Override
    public List<CourseTeacher> addTeacher(AddCourseTeacherDto addCourseTeacherDto) {
        CourseTeacher courseTeacher = new CourseTeacher();

        BeanUtils.copyProperties(addCourseTeacherDto, courseTeacher);

        courseTeacherMapper.insert(courseTeacher);

        return selectTeacherByCourseId(addCourseTeacherDto.getCourseId());
    }

    @Override
    public CourseTeacher updateTeacher(CourseTeacher courseTeacher) {
        courseTeacherMapper.updateById(courseTeacher);
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }

    @Override
    public void deleteTeacher(Long courseBaseId, Long courseTeacherId) {
        courseTeacherMapper.deleteById(courseTeacherId);
    }


}

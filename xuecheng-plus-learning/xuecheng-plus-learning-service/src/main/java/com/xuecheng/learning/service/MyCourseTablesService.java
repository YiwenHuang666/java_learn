package com.xuecheng.learning.service;

import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcCourseTables;

public interface MyCourseTablesService {
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    public XcCourseTables getXcCourseTables(String userId, Long courseId);

    boolean saveChooseCourseStauts(String choosecourseId);
}

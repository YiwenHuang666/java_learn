package com.huang.content.service;

import com.huang.content.model.dto.CoursePreviewDto;
import com.huang.content.model.po.CoursePublish;

import java.io.File;

public interface CoursePublishService {

    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    public  void commitAudit(Long company,Long courseId);

    public void publish(Long companyId,Long courseId);

    public File generateCourseHtml(Long courseId);

    public void uploadCourseHtml(Long courseId,File file);

    CoursePublish getCoursePublish(Long courseId);
}

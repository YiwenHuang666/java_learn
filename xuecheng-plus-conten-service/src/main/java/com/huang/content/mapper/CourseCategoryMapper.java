package com.huang.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huang.content.model.dto.CourseCategoryTreeDto;
import com.huang.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    public List<CourseCategoryTreeDto> selectTreeNodes(String id);
}

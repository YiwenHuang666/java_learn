package com.huang.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huang.content.model.dto.TeachplanDto;
import com.huang.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    public List<TeachplanDto> selectTreeNodes(Long courseId);

}

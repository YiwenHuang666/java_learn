package com.huang.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huang.base.exception.XueChengPlusException;
import com.huang.base.model.PageParams;
import com.huang.base.model.PageResult;
import com.huang.content.mapper.CourseBaseMapper;
import com.huang.content.mapper.CourseCategoryMapper;
import com.huang.content.mapper.CourseMarketMapper;
import com.huang.content.model.dto.AddCourseDto;
import com.huang.content.model.dto.CourseBaseInfoDto;
import com.huang.content.model.dto.EditCourseDto;
import com.huang.content.model.dto.QueryCourseParamsDto;
import com.huang.content.model.po.CourseBase;
import com.huang.content.model.po.CourseCategory;
import com.huang.content.model.po.CourseMarket;
import com.huang.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto courseParamsDto) {

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(courseParamsDto.getCourseName()), CourseBase::getName, courseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotBlank(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotBlank(courseParamsDto.getPublishStatus()), CourseBase::getStatus, courseParamsDto.getPublishStatus());

        queryWrapper.eq(CourseBase::getCompanyId,companyId);

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        List<CourseBase> items = pageResult.getRecords();

        long total = pageResult.getTotal();

        return new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        CourseBase courseBase = new CourseBase();

        BeanUtils.copyProperties(addCourseDto, courseBase);

        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");

        int insert = courseBaseMapper.insert(courseBase);
        if (insert <= 0) {
            throw new XueChengPlusException("课程添加失败");
        }

        CourseMarket courseMarket = new CourseMarket();

        BeanUtils.copyProperties(addCourseDto, courseMarket);

        Long id = courseBase.getId();
        courseMarket.setId(id);
        saveCourseMarket(courseMarket);

        CourseBaseInfoDto courseBaseInfo = getCourseBaseById(id);

        return courseBaseInfo;
    }

    private int saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if (StringUtils.isEmpty(charge)) {
            throw new XueChengPlusException("收费规则为空");
        }
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <= 0)
                throw new XueChengPlusException("课程价格不能为空且必须大于0");
        }
        Long id = courseMarket.getId();
        CourseMarket courseMarket1 = courseMarketMapper.selectById(id);
        if (courseMarket1 == null) {
            int insert = courseMarketMapper.insert(courseMarket);
            return insert;
        } else {
            BeanUtils.copyProperties(courseMarket, courseMarket1);
            courseMarket1.setId(courseMarket.getId());
            int i = courseMarketMapper.updateById(courseMarket1);
            return i;
        }
    }

    //根据课程 id 查询课程基本信息，包括基本信息和营销信息
    @Override
    public CourseBaseInfoDto getCourseBaseById(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket =
                courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
//查询分类名称
        CourseCategory courseCategoryBySt =
                courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt =
                courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {

        Long courseId = editCourseDto.getId();

        CourseBase courseBase = courseBaseMapper.selectById(courseId);

        if(courseBase == null){
            XueChengPlusException.cast("课程不存在");
        }

        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        BeanUtils.copyProperties(editCourseDto, courseBase);

        courseBase.setChangeDate(LocalDateTime.now());

        if(courseBaseMapper.updateById(courseBase) <= 0){
            XueChengPlusException.cast("修改课程失败");
        }

        CourseMarket courseMarket = new CourseMarket();

        BeanUtils.copyProperties(editCourseDto, courseMarket);

        saveCourseMarket(courseMarket);

        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseById(courseId);

        return courseBaseInfoDto;
    }

    @Override
    public void deleteCourseBaseById(Long courseBaseId) {

    }
}

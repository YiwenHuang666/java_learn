package com.huang.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huang.base.exception.XueChengPlusException;
import com.huang.content.mapper.TeachplanMapper;
import com.huang.content.mapper.TeachplanMediaMapper;
import com.huang.content.model.dto.BindTeachplanMediaDto;
import com.huang.content.model.dto.SaveTeachplanDto;
import com.huang.content.model.dto.TeachplanDto;
import com.huang.content.model.po.Teachplan;
import com.huang.content.model.po.TeachplanMedia;
import com.huang.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    private int getTeachplanCount(Long courseId,Long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getParentid,parentId).eq(Teachplan::getCourseId,courseId);
        return teachplanMapper.selectCount(queryWrapper) + 1;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        Long id = teachplanDto.getId();

        if(id!=null){
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else{
//取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(),
                    teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
//设置排序号
            teachplanNew.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplanNew);
            teachplanMapper.insert(teachplanNew);
        }
    }

    @Override
    public void deleteTeachplanById(Long deleteId) {
        //获取删除节点信息
        Teachplan teachplan = teachplanMapper.selectById(deleteId);
        //parentId==0查看子小结，parentId！=0查看teachplan_media
        if(teachplan.getParentid()==0){
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            List<Teachplan> teachplanList = teachplanMapper.selectList(queryWrapper.eq(Teachplan::getParentid,deleteId));
            if(teachplanList.isEmpty()){
                teachplanMapper.deleteById(deleteId);
            }else {
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            }
        }else {
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            teachplanMediaMapper.delete(queryWrapper.eq(TeachplanMedia::getTeachplanId,deleteId));
            teachplanMapper.deleteById(deleteId);
        }
    }

    @Override
    public void moveTeachplan(String moveStyle, Long moveId) {
        int num_up_down = 0;
        if(moveStyle.equals("moveup")){
            num_up_down = -1;
        }else if(moveStyle.equals("movedown")){
            num_up_down = 1;
        }
        Teachplan teachplan = teachplanMapper.selectById(moveId);
        //获取上一个orderBy的对象
        int orderBy = teachplan.getOrderby();
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        List<Teachplan> teachplanList = teachplanMapper.selectList(
                queryWrapper.eq(Teachplan::getOrderby, orderBy+num_up_down).eq(Teachplan::getParentid,teachplan.getParentid()).eq(Teachplan::getCourseId,teachplan.getCourseId()));
        if(!teachplanList.isEmpty()){
            Teachplan teachplanNew = teachplanMapper.selectById(teachplanList.get(0).getId());
            teachplanNew.setOrderby(orderBy);
            teachplanMapper.updateById(teachplanNew);
            teachplan.setOrderby(orderBy+num_up_down);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    @Transactional
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
//课程 id
        Long courseId = teachplan.getCourseId();
        int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId()));

        TeachplanMedia teachplanMedia = new TeachplanMedia();

        BeanUtils.copyProperties(bindTeachplanMediaDto, teachplanMedia);
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return null;
    }
}

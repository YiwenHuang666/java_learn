package com.huang.content.service.Impl;

import com.huang.content.mapper.CourseCategoryMapper;
import com.huang.content.model.dto.CourseCategoryTreeDto;
import com.huang.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos =
                courseCategoryMapper.selectTreeNodes(id);
        //将 list 转 map,以备使用,排除根节点
        Map<String, CourseCategoryTreeDto> mapTemp =
                courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId()
                )).collect(Collectors.toMap(key -> key.getId(), value -> value,
                        (key1, key2) -> key2));
        //最终返回的 list
        List<CourseCategoryTreeDto> categoryTreeDtos = new
                ArrayList<>();
        //依次遍历每个元素,排除根节点
        courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId()
        )).forEach(item -> {
            if (item.getParentid().equals(id)) {
                categoryTreeDtos.add(item);
            }
            //找到当前节点的父节点
            CourseCategoryTreeDto courseCategoryTreeDto =
                    mapTemp.get(item.getParentid());
            if (courseCategoryTreeDto != null) {
                if (courseCategoryTreeDto.getChildrenTreeNodes() == null) {
                    courseCategoryTreeDto.setChildrenTreeNodes(new
                            ArrayList<CourseCategoryTreeDto>());
                }
                //下边开始往 ChildrenTreeNodes 属性中放子节点
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtos;
    }
}

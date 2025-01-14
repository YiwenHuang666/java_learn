package com.huang.content.service;

import com.huang.content.model.dto.BindTeachplanMediaDto;
import com.huang.content.model.dto.SaveTeachplanDto;
import com.huang.content.model.dto.TeachplanDto;
import com.huang.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService {

    public List<TeachplanDto> findTeachplanTree(Long courseId);

    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    public void deleteTeachplanById(Long deleteId);

    public void moveTeachplan(String moveStyle, Long moveId);

    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

}

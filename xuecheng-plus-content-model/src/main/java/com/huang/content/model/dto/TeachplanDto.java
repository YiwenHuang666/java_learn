package com.huang.content.model.dto;

import com.huang.content.model.po.Teachplan;
import com.huang.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

@Data
public class TeachplanDto extends Teachplan {

    private TeachplanMedia teachplanMedia;

    private List<TeachplanDto> teachPlanTreeNodes;

}

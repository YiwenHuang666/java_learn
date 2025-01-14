package com.huang.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@ApiModel(value="AddCourseTeacherDto", description="新增课程教师基本信息")
public class AddCourseTeacherDto {

    @NotEmpty(message = "课程id不能为空")
    @ApiModelProperty(value = "课程id", required = true)
    private Long courseId;

    @NotEmpty(message = "教师姓名不能为空")
    @ApiModelProperty(value = "教师姓名", required = true)
    private String teacherName;

    @NotEmpty(message = "教师职位不能为空")
    @ApiModelProperty(value = "教师职位", required = true)
    private String position;

    @NotEmpty(message = "教师简介不能为空")
    @ApiModelProperty(value = "教师简介", required = true)
    private String introduction;
}

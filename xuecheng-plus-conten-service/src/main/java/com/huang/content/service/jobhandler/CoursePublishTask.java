package com.huang.content.service.jobhandler;

import com.huang.base.exception.XueChengPlusException;
import com.huang.content.feign.SearchServiceClient;
import com.huang.content.mapper.CoursePublishMapper;
import com.huang.content.mapper.CoursePublishPreMapper;
import com.huang.content.model.dto.CoursePreviewDto;
import com.huang.content.model.po.CoursePublish;
import com.huang.content.service.CoursePublishService;
import com.huang.messagesdk.model.po.MqMessage;
import com.huang.messagesdk.service.MessageProcessAbstract;
import com.huang.messagesdk.service.MqMessageService;
import com.xuecheng.search.po.CourseIndex;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    SearchServiceClient searchServiceClient;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception{
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());

        generateCourseHtml(mqMessage, courseId);

        saveCourseIndex(mqMessage, courseId);

        return true;
    }

    private void saveCourseIndex(MqMessage mqMessage, Long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(taskId);
        if(stageTwo>0){
            log.debug("课程索引信息已经完成，无需处理");
            return;
        }

        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);

        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);

        Boolean add = searchServiceClient.add(courseIndex);
        if(!add){
            XueChengPlusException.cast("远程调用添加课程失败");
        }

        mqMessageService.completedStageTwo(taskId);

    }

    private void generateCourseHtml(MqMessage mqMessage, Long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageOne = mqMessageService.getStageOne(taskId);
        if(stageOne>0){
            log.debug("课程静态化已经完成，无需处理");
            return;
        }

        File file = coursePublishService.generateCourseHtml(courseId);
        if(file == null){
            XueChengPlusException.cast("生成的静态页面为空");
        }
        coursePublishService.uploadCourseHtml(courseId, file);

        mqMessageService.completedStageOne(taskId);
    }
}

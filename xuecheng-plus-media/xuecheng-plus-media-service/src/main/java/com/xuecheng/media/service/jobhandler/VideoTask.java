package com.xuecheng.media.service.jobhandler;

import com.huang.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class VideoTask {

    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    @XxlJob("videoJobHandler")
    public void videoJobHandler()throws Exception{

        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        int processors = Runtime.getRuntime().availableProcessors();

        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);

        int size = mediaProcessList.size();
        log.debug("启动的任务数:{}",size);
        if(size<=0){
            return;
        }

        CountDownLatch countDownLatch = new CountDownLatch(size);

        ExecutorService executorService = Executors.newFixedThreadPool(size);

        mediaProcessList.forEach(mediaProcess -> {
            executorService.execute(() ->{
                try {
                    Long taskId = mediaProcess.getId();
                    boolean isStart = mediaFileProcessService.startTask(taskId);
                    if(!isStart){
                        log.debug("抢占任务失败，任务ID:{}", taskId);
                    }
                    String fileId = mediaProcess.getFileId();
                    String bucket = mediaProcess.getBucket();
                    String objectName = mediaProcess.getFilePath();
                    File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);

                    if(file == null){
                        log.debug("下周视频出错，任务ID:{},bucket:{},objectName:{}", taskId,bucket,objectName);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId,null,"下载视频到本地失败");
                        return;
                    }

                    //源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId+".mp4";

                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    }catch (IOException e){
                        log.debug("转码时创建临时文件异常,{}",e.getMessage());
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件异常");
                        return;
                    }
                    //转换后mp4文件的路径
                    String mp4_path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath,video_path,mp4_name,mp4_path);
                    //开始视频转换，成功将返回success
                    String result = videoUtil.generateMp4();
                    if(!result.equals("success")){
                        log.debug("视频转码失败，原因:{},bucket:{},objectName:{}",result,bucket,objectName);

                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                        return;
                    }

                    String url = "/video/" + getFilePath(fileId, ".mp4");
                    boolean isUpload = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, url);

                    if(!isUpload){
                        log.error("上传mp4到minio失败，taskId:{}",taskId);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "上传mp4到minio失败");
                        return;
                    }



                    mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, "");
                }finally {
                    countDownLatch.countDown();
                }
            });
        });

        boolean await = countDownLatch.await(30, TimeUnit.MINUTES);

    }
    private String getFilePath(String fileMd5,String fileExt){
        return fileMd5.substring(0,1) + "/" +
                fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

}

package com.xuecheng.media.config;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// @Component
public class SamplexllJob {

    private final Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);






    @XxlJob("demoHandler1")
    public void demoHandler1(){
        // 分片参数，由调度器动态下发
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();


        logger.info("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);
        logger.info("开始执行第"+shardIndex+"批任务");
        System.out.println("处理视频");
    }
}

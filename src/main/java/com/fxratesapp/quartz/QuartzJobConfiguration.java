package com.fxratesapp.quartz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobConfiguration {

    private static final Logger logger = LogManager.getLogger(QuartzJobConfiguration.class);
    private static final String CRON_SCHEDULE_EVERY_30_SECONDS = "0/30 * * ? * * *"; // <== for demo purposes only
    private static final String CRON_SCHEDULE_MON_FRI_7AM = "0 0 7 ? * MON,TUE,WED,THU,FRI *"; // <== http://www.cronmaker.com/

    @Bean
    public JobDetail jobDetails() {
        logger.info("Creating jobDetails bean in " + getClass().getName() + " class");
        return JobBuilder.newJob(QuartzJob.class).withIdentity("getFXRates").storeDurably().build();
    }

    @Bean
    public Trigger jobTrigger(JobDetail jobDetails) {
        logger.info("Creating jobTrigger bean in " + getClass().getName() + " class");
        return TriggerBuilder.newTrigger().forJob(jobDetails)
                .withIdentity("QuartzTriggerForDailyFXRates")
                .withSchedule(CronScheduleBuilder.cronSchedule(CRON_SCHEDULE_EVERY_30_SECONDS))
                .build();
    }

}
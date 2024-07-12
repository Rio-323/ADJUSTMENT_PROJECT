package com.example.scheduler;

import com.example.config.VideoStatsJobConfig;
import com.example.repository.VideoDailyViewCountRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableScheduling
public class BatchScheduler implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final VideoDailyViewCountRepository videoDailyViewCountRepository;
    private static final Logger logger = LoggerFactory.getLogger(BatchScheduler.class);

    @Autowired
    public BatchScheduler(JobLauncher jobLauncher, VideoStatsJobConfig jobConfig, VideoDailyViewCountRepository videoDailyViewCountRepository) {
        this.jobLauncher = jobLauncher;
        this.job = jobConfig.videoStatsJob();
        this.videoDailyViewCountRepository = videoDailyViewCountRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul") // 매일 자정에 실행 (한국 시간)
    public void runDailyJob() throws Exception {
        LocalDate today = LocalDate.now();
        logger.info("Running daily job for date: {}", today);
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("run.id", new JobParameter<>(String.valueOf(System.currentTimeMillis()), String.class));
        parameters.put("startDate", new JobParameter<>(today.toString(), String.class));
        parameters.put("endDate", new JobParameter<>(today.toString(), String.class));
        JobParameters jobParameters = new JobParameters(parameters);
        jobLauncher.run(job, jobParameters);
    }

    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul") // 매주 월요일 자정에 실행 (한국 시간)
    public void runWeeklyJob() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        logger.info("Running weekly job from {} to {}", startOfWeek, endOfWeek);
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("run.id", new JobParameter<>(String.valueOf(System.currentTimeMillis()), String.class));
        parameters.put("startDate", new JobParameter<>(startOfWeek.toString(), String.class));
        parameters.put("endDate", new JobParameter<>(endOfWeek.toString(), String.class));
        JobParameters jobParameters = new JobParameters(parameters);
        jobLauncher.run(job, jobParameters);
    }

    @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Seoul") // 매월 1일 자정에 실행 (한국 시간)
    public void runMonthlyJob() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        logger.info("Running monthly job for {} to {}", startOfMonth, endOfMonth);
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("run.id", new JobParameter<>(String.valueOf(System.currentTimeMillis()), String.class));
        parameters.put("startDate", new JobParameter<>(startOfMonth.toString(), String.class));
        parameters.put("endDate", new JobParameter<>(endOfMonth.toString(), String.class));
        JobParameters jobParameters = new JobParameters(parameters);
        jobLauncher.run(job, jobParameters);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        runInitialJob();
    }

    public void runInitialJob() throws Exception {
        Optional<LocalDate> oldestDateOpt = videoDailyViewCountRepository.findOldestDate();
        LocalDate today = LocalDate.now();

        if (oldestDateOpt.isPresent()) {
            LocalDate oldestDate = oldestDateOpt.get();
            logger.info("Running initial job from {} to {}", oldestDate, today);
            Map<String, JobParameter<?>> parameters = new HashMap<>();
            parameters.put("run.id", new JobParameter<>(String.valueOf(System.currentTimeMillis()), String.class));
            parameters.put("startDate", new JobParameter<>(oldestDate.toString(), String.class));
            parameters.put("endDate", new JobParameter<>(today.toString(), String.class));
            JobParameters jobParameters = new JobParameters(parameters);
            jobLauncher.run(job, jobParameters);
        } else {
            logger.info("No data found in VideoDailyViewCount repository.");
        }
    }
}
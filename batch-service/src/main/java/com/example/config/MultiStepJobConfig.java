package com.example.config;

import com.example.entity.VideoDailyViewCount;
import com.example.entity.VideoRevenue;
import com.example.entity.VideoStatistics;
import com.example.processor.VideoRevenueProcessor;
import com.example.processor.VideoStatsProcessor;
import com.example.reader.VideoRevenueReader;
import com.example.reader.VideoStatsReader;
import com.example.writer.VideoRevenueWriter;
import com.example.writer.VideoStatsWriter;
import com.example.listener.VideoStatsStepExecutionListener;
import com.example.listener.VideoRevenueStepExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MultiStepJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final VideoStatsProcessor videoStatsProcessor;
    private final VideoRevenueProcessor videoRevenueProcessor;
    private final VideoStatsStepExecutionListener videoStatsStepExecutionListener;
    private final VideoRevenueStepExecutionListener videoRevenueStepExecutionListener;
    private final DataSource dataSource;

    public MultiStepJobConfig(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              VideoStatsProcessor videoStatsProcessor,
                              VideoRevenueProcessor videoRevenueProcessor,
                              VideoStatsStepExecutionListener videoStatsStepExecutionListener,
                              VideoRevenueStepExecutionListener videoRevenueStepExecutionListener,
                              DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.videoStatsProcessor = videoStatsProcessor;
        this.videoRevenueProcessor = videoRevenueProcessor;
        this.videoStatsStepExecutionListener = videoStatsStepExecutionListener;
        this.videoRevenueStepExecutionListener = videoRevenueStepExecutionListener;
        this.dataSource = dataSource;
    }

    @Bean
    public Job combinedJob(Step videoStatsStep, Step videoRevenueStep) {
        return new JobBuilder("combinedJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(videoStatsStep)
                .next(videoRevenueStep)
                .build();
    }

    @Bean
    @JobScope
    public Step videoStatsStep(VideoStatsReader videoStatsReader,
                               VideoStatsWriter videoStatsWriter,
                               @Value("#{jobParameters['startDate']}") String startDate,
                               @Value("#{jobParameters['endDate']}") String endDate) {
        return new StepBuilder("videoStatsStep", jobRepository)
                .<VideoDailyViewCount, VideoStatistics>chunk(10, transactionManager)
                .reader(videoStatsReader.videoStatsJdbcReader(startDate, endDate))
                .processor(videoStatsProcessor)
                .writer(videoStatsWriter.videoStatsWriter())
                .listener(videoStatsStepExecutionListener)
                .build();
    }

    @Bean
    @JobScope
    public Step videoRevenueStep(VideoRevenueReader videoRevenueReader,
                                 VideoRevenueWriter videoRevenueWriter,
                                 @Value("#{jobParameters['startDate']}") String startDate,
                                 @Value("#{jobParameters['endDate']}") String endDate) {
        return new StepBuilder("videoRevenueStep", jobRepository)
                .<VideoDailyViewCount, VideoRevenue>chunk(10, transactionManager)
                .reader(videoRevenueReader.videoRevenueJdbcReader(startDate, endDate))
                .processor(videoRevenueProcessor)
                .writer(videoRevenueWriter.videoRevenueWriter())
                .listener(videoRevenueStepExecutionListener)
                .build();
    }
}
package com.example.config;

import com.example.entity.VideoDailyViewCount;
import com.example.entity.VideoStatistics;
import com.example.listener.VideoStatsStepExecutionListener;
import com.example.processor.VideoStatsProcessor;
import com.example.reader.VideoStatsReader;
import com.example.writer.VideoStatsWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class VideoStatsJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final VideoStatsProcessor processor;
    private final VideoStatsWriter writer;
    private final DataSource dataSource;
    private final VideoStatsStepExecutionListener stepExecutionListener;

    @Autowired
    public VideoStatsJobConfig(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               VideoStatsProcessor processor,
                               VideoStatsWriter writer,
                               DataSource dataSource,
                               VideoStatsStepExecutionListener stepExecutionListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.processor = processor;
        this.writer = writer;
        this.dataSource = dataSource;
        this.stepExecutionListener = stepExecutionListener;
    }

    @Bean
    public Job videoStatsJob() {
        return new JobBuilder("videoStatsJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 이 줄을 추가하여 Job이 재실행 가능하도록 설정
                .start(videoStatsStep())
                .build();
    }

    @Bean
    @JobScope
    public Step videoStatsStep() {
        return new StepBuilder("videoStatsStep", jobRepository)
                .<VideoDailyViewCount, VideoStatistics>chunk(10, transactionManager)
                .reader(videoStatsJdbcReader(null, null))
                .processor(processor)
                .writer(writer.writer(dataSource))
                .listener(stepExecutionListener) // StepListener 추가
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<VideoDailyViewCount> videoStatsJdbcReader(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate) {
        return new VideoStatsReader().reader(dataSource, startDate, endDate);
    }
}
package com.example.config;

import com.example.tasklet.GenerateStatisticsTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final GenerateStatisticsTasklet generateStatisticsTasklet;

    @Bean
    public Job statisticsJob() {
        return new JobBuilder("statisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(generateStatisticsStep())
                .build();
    }

    @Bean
    public Step generateStatisticsStep() {
        return new StepBuilder("generateStatisticsStep", jobRepository)
                .tasklet(generateStatisticsTasklet, transactionManager)
                .build();
    }
}

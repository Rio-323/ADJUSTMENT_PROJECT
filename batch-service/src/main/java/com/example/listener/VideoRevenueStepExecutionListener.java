package com.example.listener;

import com.example.entity.AdDailyViewCount;
import com.example.entity.VideoRevenue;
import com.example.processor.VideoRevenueProcessor;
import com.example.reader.AdStatsReader;
import com.example.writer.VideoRevenueWriter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Component
public class VideoRevenueStepExecutionListener extends StepExecutionListenerSupport {

    private final VideoRevenueProcessor processor;
    private final VideoRevenueWriter writer;
    private final AdStatsReader adStatsReader;

    @Autowired
    public VideoRevenueStepExecutionListener(VideoRevenueProcessor processor, VideoRevenueWriter writer, AdStatsReader adStatsReader) {
        this.processor = processor;
        this.writer = writer;
        this.adStatsReader = adStatsReader;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        List<AdDailyViewCount> adDailyViewCounts = adStatsReader.readAdDailyViewCounts();
        processor.setAdDailyViewCounts(adDailyViewCounts);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        try {
            List<VideoRevenue> summaries = processor.generateFinalSummaries();
            writer.writeSummaries(summaries);
            return ExitStatus.COMPLETED;
        } catch (Exception e) {
            return ExitStatus.FAILED;
        }
    }
}
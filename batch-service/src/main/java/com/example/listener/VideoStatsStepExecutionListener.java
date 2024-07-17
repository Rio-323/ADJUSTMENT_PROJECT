package com.example.listener;

import com.example.entity.VideoStatistics;
import com.example.processor.VideoStatsProcessor;
import com.example.writer.VideoStatsWriter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Component
public class VideoStatsStepExecutionListener extends StepExecutionListenerSupport {

    private final VideoStatsProcessor processor;
    private final VideoStatsWriter writer;

    public VideoStatsStepExecutionListener(VideoStatsProcessor processor, VideoStatsWriter writer) {
        this.processor = processor;
        this.writer = writer;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        try {
            // 오늘의 날짜를 기준으로 period 값을 생성
            String dailyPeriod = LocalDate.now().toString();
            List<VideoStatistics> dailySummariesList = processor.generateFinalSummaries(dailyPeriod);
            writer.writeSummaries(dailySummariesList);

            // 주간 period 계산
            LocalDate now = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            LocalDate startOfWeek = now.with(weekFields.dayOfWeek(), 1);
            LocalDate endOfWeek = now.with(weekFields.dayOfWeek(), 7);
            String weeklyPeriod = startOfWeek + " ~ " + endOfWeek;
            List<VideoStatistics> weeklySummariesList = processor.generateFinalSummaries(weeklyPeriod);
            writer.writeSummaries(weeklySummariesList);

            // 월간 period 계산
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            String monthlyPeriod = startOfMonth + " ~ " + endOfMonth;
            List<VideoStatistics> monthlySummariesList = processor.generateFinalSummaries(monthlyPeriod);
            writer.writeSummaries(monthlySummariesList);

            return ExitStatus.COMPLETED;
        } catch (Exception e) {
            return ExitStatus.FAILED;
        }
    }
}
package com.example.listener;

import com.example.entity.VideoEntity;
import com.example.entity.VideoStatistics;
import com.example.processor.VideoStatsProcessor;
import com.example.writer.VideoStatsWriter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        // videoEntityMap 초기화 예제 코드 (데이터베이스에서 데이터 로드 등)
        Map<Long, VideoEntity> videoEntityMap = new HashMap<>();
        // videoEntityMap을 실제 데이터로 초기화하는 코드 추가
        // 예: videoEntityMap.put(1L, new VideoEntity(...));

        processor.setVideoEntityMap(videoEntityMap); // 초기화한 videoEntityMap 설정

        // 오늘의 날짜를 기준으로 period 값을 생성
        String dailyPeriod = LocalDate.now().toString();
        List<VideoStatistics> dailySummaries = processor.generateFinalSummaries(dailyPeriod);
        writer.writeSummaries(dailySummaries);

        // 주간 period 계산
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = now.with(weekFields.dayOfWeek(), 1);
        LocalDate endOfWeek = now.with(weekFields.dayOfWeek(), 7);
        String weeklyPeriod = startOfWeek + " ~ " + endOfWeek;
        List<VideoStatistics> weeklySummaries = processor.generateFinalSummaries(weeklyPeriod);
        writer.writeSummaries(weeklySummaries);

        // 월간 period 계산
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        String monthlyPeriod = startOfMonth + " ~ " + endOfMonth;
        List<VideoStatistics> monthlySummaries = processor.generateFinalSummaries(monthlyPeriod);
        writer.writeSummaries(monthlySummaries);

        return ExitStatus.COMPLETED;
    }
}
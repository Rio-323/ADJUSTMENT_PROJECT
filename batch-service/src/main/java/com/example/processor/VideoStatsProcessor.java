package com.example.processor;

import com.example.entity.VideoDailyViewCount;
import com.example.entity.VideoEntity;
import com.example.entity.VideoStatistics;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class VideoStatsProcessor implements ItemProcessor<VideoDailyViewCount, VideoStatistics> {

    private final List<VideoDailyViewCount> viewCountList = new ArrayList<>();
    @Setter
    private Map<Long, VideoEntity> videoEntityMap = new HashMap<>(); // 초기화 추가

    private static final Logger logger = LoggerFactory.getLogger(VideoStatsProcessor.class);

    @Override
    public VideoStatistics process(VideoDailyViewCount item) {
        if (item == null) {
            logger.warn("No data to process.");
            return null;
        }
        logger.info("Processing item: {}", item);
        viewCountList.add(item);
        return null; // 여기서는 ItemWriter에서 최종 처리
    }

    public List<VideoStatistics> generateFinalSummaries(String period) {
        List<VideoStatistics> finalSummaries = new ArrayList<>();
        logger.info("Generating final summaries for period: {}", period);

        finalSummaries.addAll(getTopViewedVideos(period));
        finalSummaries.addAll(getTopPlayTimeVideos(period));

        logger.info("Final summaries: {}", finalSummaries);

        return finalSummaries;
    }

    private List<VideoStatistics> getTopViewedVideos(String period) {
        return viewCountList.stream()
                .collect(Collectors.groupingBy(v -> v.getVideo().getId(), Collectors.summingInt(VideoDailyViewCount::getViewCount)))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> createViewCountSummary(entry.getKey(), entry.getValue(), period))
                .collect(Collectors.toList());
    }

    private List<VideoStatistics> getTopPlayTimeVideos(String period) {
        return videoEntityMap.values().stream()
                .sorted(Comparator.comparingInt(this::getTotalPlayTime).reversed())
                .limit(5)
                .map(video -> createPlayTimeSummary(video, period))
                .collect(Collectors.toList());
    }

    private VideoStatistics createViewCountSummary(Long videoId, int viewCount, String period) {
        VideoStatistics summary = new VideoStatistics();
        summary.setVideoId(videoId);
        summary.setViewCount(viewCount);
        summary.setPeriod(period);
        summary.setType("view");
        return summary;
    }

    private VideoStatistics createPlayTimeSummary(VideoEntity video, String period) {
        VideoStatistics summary = new VideoStatistics();
        summary.setVideoId(video.getId());
        summary.setPlayTime(getTotalPlayTime(video));
        summary.setPeriod(period);
        summary.setType("playTime");
        return summary;
    }

    private int getTotalPlayTime(VideoEntity video) {
        return video.getUserWatchPositions().values().stream().mapToInt(Integer::intValue).sum();
    }
}
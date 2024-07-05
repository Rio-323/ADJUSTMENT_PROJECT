package com.example.tasklet;

import com.example.client.StreamingServiceClient;
import com.example.dto.VideoStatistics;
import com.example.entity.StatisticsEntity;
import com.example.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateStatisticsTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(GenerateStatisticsTasklet.class);

    private final StreamingServiceClient streamingServiceClient;
    private final StatisticsRepository statisticsRepository;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        logger.info("Starting statistics generation tasklet");
        saveStatistics("daily");
        saveStatistics("weekly");
        saveStatistics("monthly");
        logger.info("Finished statistics generation tasklet");

        return RepeatStatus.FINISHED;
    }

    private void saveStatistics(String period) {
        saveTop5Views(period);
        saveTop5Durations(period);
    }

    private void saveTop5Views(String period) {
        logger.info("Fetching top 5 videos by views for period: {}", period);
        VideoStatistics statistics = streamingServiceClient.getTop5VideosByViews(period);
        for (var video : statistics.getTopVideos()) {
            StatisticsEntity entity = new StatisticsEntity();
            entity.setPeriod(period);
            entity.setType("views");
            entity.setVideoTitle(video.getTitle());
            entity.setViewCount(video.getViewCount());
            entity.setDuration(video.getDuration());
            statisticsRepository.save(entity);
            logger.info("Saved statistics for video: {}", video.getTitle());
        }
    }

    private void saveTop5Durations(String period) {
        logger.info("Fetching top 5 videos by duration for period: {}", period);
        VideoStatistics statistics = streamingServiceClient.getTop5VideosByDuration(period);
        for (var video : statistics.getTopVideos()) {
            StatisticsEntity entity = new StatisticsEntity();
            entity.setPeriod(period);
            entity.setType("duration");
            entity.setVideoTitle(video.getTitle());
            entity.setViewCount(video.getViewCount());
            entity.setDuration(video.getDuration());
            statisticsRepository.save(entity);
            logger.info("Saved statistics for video: {}", video.getTitle());
        }
    }
}

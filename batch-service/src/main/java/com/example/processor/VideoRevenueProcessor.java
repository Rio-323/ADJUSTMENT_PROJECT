package com.example.processor;

import com.example.entity.AdDailyViewCount;
import com.example.entity.VideoDailyViewCount;
import com.example.entity.VideoRevenue;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class VideoRevenueProcessor implements ItemProcessor<VideoDailyViewCount, VideoRevenue> {

    @Setter
    private List<AdDailyViewCount> adDailyViewCounts;

    private static final Logger logger = LoggerFactory.getLogger(VideoRevenueProcessor.class);

    private final Map<Long, Map<String, VideoRevenue>> videoRevenueMap = new HashMap<>();

    @Override
    public VideoRevenue process(VideoDailyViewCount item) {
        if (item == null) {
            logger.warn("No data to process.");
            return null;
        }
        logger.info("Processing item: {}", item);

        LocalDate date = item.getDate();
        long videoId = item.getVideo().getId();

        String dailyPeriod = date.toString();
        String weeklyPeriod = getWeeklyPeriod(date);
        String monthlyPeriod = getMonthlyPeriod(date);

        double videoRevenue = calculateVideoRevenue(item.getViewCount());
        double adRevenue = calculateAdRevenue(videoId);

        processRevenue(videoId, dailyPeriod, videoRevenue, adRevenue);
        processRevenue(videoId, weeklyPeriod, videoRevenue, adRevenue);
        processRevenue(videoId, monthlyPeriod, videoRevenue, adRevenue);

        return null;
    }

    private void processRevenue(long videoId, String period, double videoRevenue, double adRevenue) {
        videoRevenueMap.putIfAbsent(videoId, new HashMap<>());
        Map<String, VideoRevenue> periodMap = videoRevenueMap.get(videoId);

        VideoRevenue revenue = periodMap.getOrDefault(period, new VideoRevenue());
        revenue.setVideoId(videoId);
        revenue.setPeriod(period);

        revenue.setTotalRevenue(formatCurrency(parseRevenue(revenue.getTotalRevenue()) + videoRevenue + adRevenue));
        revenue.setVideoRevenue(formatCurrency(parseRevenue(revenue.getVideoRevenue()) + videoRevenue));
        revenue.setAdRevenue(formatCurrency(parseRevenue(revenue.getAdRevenue()) + adRevenue));

        periodMap.put(period, revenue);
    }

    private String getWeeklyPeriod(LocalDate date) {
        LocalDate startOfWeek = date.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = date.with(java.time.DayOfWeek.SUNDAY);
        return startOfWeek + " ~ " + endOfWeek;
    }

    private String getMonthlyPeriod(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return startOfMonth + " ~ " + endOfMonth;
    }

    private double calculateVideoRevenue(int viewCount) {
        if (viewCount < 100000) {
            return viewCount * 1.0;
        } else if (viewCount < 500000) {
            return 100000 * 1.0 + (viewCount - 100000) * 1.1;
        } else if (viewCount < 1000000) {
            return 100000 * 1.0 + 400000 * 1.1 + (viewCount - 500000) * 1.3;
        } else {
            return 100000 * 1.0 + 400000 * 1.1 + 500000 * 1.3 + (viewCount - 1000000) * 1.5;
        }
    }

    private double calculateAdRevenue(long videoId) {
        return adDailyViewCounts.stream()
                .filter(ad -> ad.getAd() != null && ad.getAd().getVideos().stream().anyMatch(video -> video.getId() == videoId))
                .mapToDouble(ad -> {
                    int adViewCount = ad.getViewCount();
                    if (adViewCount < 100000) {
                        return adViewCount * 10.0;
                    } else if (adViewCount < 500000) {
                        return 100000 * 10.0 + (adViewCount - 100000) * 12.0;
                    } else if (adViewCount < 1000000) {
                        return 100000 * 10.0 + 400000 * 12.0 + (adViewCount - 500000) * 15.0;
                    } else {
                        return 100000 * 10.0 + 400000 * 12.0 + 500000 * 15.0 + (adViewCount - 1000000) * 20.0;
                    }
                })
                .sum();
    }

    private String formatCurrency(double revenue) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);
        return numberFormat.format(revenue);
    }

    private double parseRevenue(String revenue) {
        try {
            return NumberFormat.getCurrencyInstance(Locale.KOREA).parse(revenue).doubleValue();
        } catch (Exception e) {
            return 0.0;
        }
    }

    public List<VideoRevenue> generateFinalSummaries() {
        return videoRevenueMap.values().stream()
                .flatMap(periodMap -> periodMap.values().stream())
                .collect(Collectors.toList());
    }
}
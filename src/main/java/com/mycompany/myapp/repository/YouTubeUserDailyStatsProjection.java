package com.mycompany.myapp.repository;

public interface YouTubeUserDailyStatsProjection {
    Long getUserId();
    String getUserEmail();
    long getChannelsChecked();
    long getChannelsPassed();
    long getChannelsFailed();
}

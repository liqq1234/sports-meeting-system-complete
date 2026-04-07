package com.sports.sports.service;

import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getDashboard(Long meetingId);
    Map<String, Object> getOverview();
    Map<String, Object> getMedicalAnalytics(Long meetingId);
}

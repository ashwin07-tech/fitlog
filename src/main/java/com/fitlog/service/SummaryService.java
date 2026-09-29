package com.fitlog.service;

import com.fitlog.dto.DailySummaryDto;
import com.fitlog.dto.WeeklySummaryDto;

import java.time.LocalDate;

public interface SummaryService {
    DailySummaryDto getDailyBalance(Long userId, LocalDate date);
    WeeklySummaryDto getWeeklyTrend(Long userId, LocalDate dateInWeek);
}

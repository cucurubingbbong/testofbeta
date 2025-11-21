package com.jrm.app.service;

import com.jrm.app.model.AttendanceResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    private final Map<String, Set<LocalDate>> attendance = new ConcurrentHashMap<>();

    public AttendanceResponse checkIn(String email) {
        attendance.computeIfAbsent(email, k -> ConcurrentHashMap.newKeySet()).add(LocalDate.now());
        return build(email);
    }

    public AttendanceResponse build(String email) {
        Set<LocalDate> dates = attendance.getOrDefault(email, ConcurrentHashMap.newKeySet());
        int streak = 0;
        LocalDate cursor = LocalDate.now();
        while (dates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return new AttendanceResponse(dates.stream().collect(Collectors.toSet()), streak);
    }
}

package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.WorkLogStatus;
import com.mycompany.myapp.dto.request.WorkLogRequest;
import com.mycompany.myapp.dto.request.WorkLogReviewRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.WorkLogResponse;
import org.springframework.data.domain.Pageable;

public interface WorkLogService {
    WorkLogResponse create(WorkLogRequest request, Long userId);
    PageResponse<WorkLogResponse> findMyLogs(Long userId, Pageable pageable);
    PageResponse<WorkLogResponse> findAll(WorkLogStatus status, Pageable pageable);
    WorkLogResponse review(Long id, WorkLogReviewRequest request, Long adminId);
}

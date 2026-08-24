package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.MonetizationApplicationStatus;
import com.mycompany.myapp.dto.request.MonetizationApplicationRequest;
import com.mycompany.myapp.dto.request.MonetizationApplicationReviewRequest;
import com.mycompany.myapp.dto.response.MonetizationApplicationResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface YouTubeMonetizationService {
    MonetizationApplicationResponse submit(MonetizationApplicationRequest request, Long userId);
    PageResponse<MonetizationApplicationResponse> findMyApplications(Long userId, Pageable pageable);
    PageResponse<MonetizationApplicationResponse> findAll(MonetizationApplicationStatus status, Pageable pageable);
    MonetizationApplicationResponse review(Long id, MonetizationApplicationReviewRequest request, Long adminId);
}

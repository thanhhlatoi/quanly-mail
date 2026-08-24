package com.mycompany.myapp.service;

import com.mycompany.myapp.dto.request.AdSenseChannelLinkRequest;
import com.mycompany.myapp.dto.request.AdSenseMonthlyRevenueRequest;
import com.mycompany.myapp.dto.request.AdSenseRevenueRequest;
import com.mycompany.myapp.dto.response.AdSenseChannelLinkResponse;
import com.mycompany.myapp.dto.response.AdSenseMonthlyRevenueResponse;
import com.mycompany.myapp.dto.response.AdSenseMonthlyTotalResponse;
import com.mycompany.myapp.dto.response.AdSenseRevenueResponse;
import com.mycompany.myapp.dto.response.AdSenseRevenueSummaryResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AdSenseService {
    AdSenseChannelLinkResponse linkChannel(AdSenseChannelLinkRequest request);
    PageResponse<AdSenseChannelLinkResponse> findLinks(Pageable pageable);
    void unlinkChannel(Long linkId);
    AdSenseRevenueResponse saveRevenue(Long linkId, AdSenseRevenueRequest request, Long adminId);
    PageResponse<AdSenseRevenueResponse> findRevenues(Pageable pageable);
    AdSenseRevenueSummaryResponse summarize(LocalDate from, LocalDate to, String currency);
    AdSenseMonthlyRevenueResponse saveMonthlyRevenue(Long channelId, AdSenseMonthlyRevenueRequest request, Long userId);
    PageResponse<AdSenseMonthlyRevenueResponse> findMonthlyRevenues(Long userId, Pageable pageable);
    List<AdSenseMonthlyTotalResponse> monthlyTotals(String month, String currency);
}

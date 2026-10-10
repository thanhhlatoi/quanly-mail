package com.mycompany.myapp.service;

import com.mycompany.myapp.dto.request.UserRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.UserResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse create(UserRequest request);
    UserResponse register(UserRequest request);
    UserResponse update(Long id, UserRequest request);
    Optional<UserResponse> findById(Long id);
    List<UserResponse> findAll();
    PageResponse<UserResponse> findPage(Pageable pageable);
    void delete(Long id);
}

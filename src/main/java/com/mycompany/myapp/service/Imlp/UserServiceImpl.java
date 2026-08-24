package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.UserRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.UserResponse;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse create(UserRequest request) {
        User user = new User();
        copy(request, user);
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        copy(request, user);
        return toResponse(userRepository.save(user));
    }

    @Override
    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id).map(this::toResponse);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public PageResponse<UserResponse> findPage(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable).map(this::toResponse));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }
        userRepository.deleteById(id);
    }

    private void copy(UserRequest request, User user) {
        user.setUserName(request.getUserName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setFullName(request.getFullName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus(request.isStatus());
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUserName(),
            user.getFullName(),
            user.getDateOfBirth(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.isStatus()
        );
    }
}

package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.Role;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.RoleRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.PermissionsResponse;
import com.mycompany.myapp.dto.response.RoleResponse;
import com.mycompany.myapp.repository.RoleRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.RoleService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RoleResponse create(RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        Role saveRole = roleRepository.save(role);
        return roleResponse(saveRole);
    }

    @Override
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy vai trò"));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        Role updateRole = roleRepository.save(role);

        return roleResponse(updateRole);
    }

    @Override
    public Optional<RoleResponse> findById(Long id) {
        return roleRepository.findById(id).map(this::roleResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream().map(this::roleResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> findPage(Pageable pageable) {
        return PageResponse.from(roleRepository.findAll(pageable).map(this::roleResponse));
    }

    @Override
    public void deleteById(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy vai trò");
        }
        roleRepository.deleteById(id);
    }

    @Override
    public void assignRoleToUser(Long roleId, Long userId) {
        Role role = roleRepository
            .findById(roleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy vai trò"));
        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(Long roleId, Long userId) {
        Role role = roleRepository
            .findById(roleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy vai trò"));
        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    public List<User> findUsersByRoleId(Long roleId) {
        Role role = roleRepository
            .findById(roleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy vai trò"));
        return List.copyOf(role.getUsers());
    }

    private RoleResponse roleResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        Set<PermissionsResponse> permissionsResponses = role
            .getPermissions()
            .stream()
            .map(permissions -> new PermissionsResponse(permissions.getId(), permissions.getName(), permissions.getDescription()))
            .collect(Collectors.toSet());
        response.setPermissionsResponses(permissionsResponses);
        return response;
    }
}

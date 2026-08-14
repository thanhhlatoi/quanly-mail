package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.Role;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.RoleRequest;
import com.mycompany.myapp.dto.response.PermissionsResponse;
import com.mycompany.myapp.dto.response.RoleResponse;
import com.mycompany.myapp.repository.RoleRepository;
import com.mycompany.myapp.service.RoleService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

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
        return null;
    }

    @Override
    public Optional<RoleResponse> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Role> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {}

    @Override
    public void assignRoleToUser(Long roleId, Long userId) {}

    @Override
    public void removeRoleFromUser(Long roleId, Long userId) {}

    @Override
    public List<User> findUsersByRoleId(Long roleId) {
        return List.of();
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
        return response;
    }
}

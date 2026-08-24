package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.Permissions;
import com.mycompany.myapp.dto.request.PermissionsRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.PermissionsResponse;
import com.mycompany.myapp.repository.PermissionsRepository;
import com.mycompany.myapp.service.PermissionsService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PermissionsServiceImpl implements PermissionsService {

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Override
    public PermissionsResponse create(PermissionsRequest request) {
        Permissions permissions = new Permissions();
        permissions.setName(request.getName());
        permissions.setDescription(request.getDescription());
        Permissions savePermissions = permissionsRepository.save(permissions);
        return permissionsResponse(savePermissions);
    }

    @Override
    public PermissionsResponse update(Long id, PermissionsRequest request) {
        Permissions permissions = permissionsRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy quyền"));
        permissions.setName(request.getName());
        permissions.setDescription(request.getDescription());
        Permissions savePermissions1 = permissionsRepository.save(permissions);
        return permissionsResponse(savePermissions1);
    }

    @Override
    public Optional<PermissionsResponse> findById(Long id) {
        return permissionsRepository.findById(id).map(this::permissionsResponse);
    }

    @Override
    public List<PermissionsResponse> findAll() {
        return permissionsRepository.findAll().stream().map(this::permissionsResponse).collect(Collectors.toList());
    }

    @Override
    public PageResponse<PermissionsResponse> findPage(Pageable pageable) {
        return PageResponse.from(permissionsRepository.findAll(pageable).map(this::permissionsResponse));
    }

    @Override
    public void deleteById(Long id) {
        if (!permissionsRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy quyền");
        }
        permissionsRepository.deleteById(id);
    }

    private PermissionsResponse permissionsResponse(Permissions permissions) {
        return new PermissionsResponse(permissions.getId(), permissions.getName(), permissions.getDescription());
    }
}

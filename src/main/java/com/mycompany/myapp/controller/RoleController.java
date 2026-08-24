package com.mycompany.myapp.controller;

import com.mycompany.myapp.dto.request.RoleRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.RoleResponse;
import com.mycompany.myapp.service.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/hello")
    public String hello() {
        return "Xin chào ";
    }

    @PostMapping
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<RoleResponse>> getAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(roleService.findPage(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.of(roleService.findById(id));
    }

    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/users/{userId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long roleId, @PathVariable Long userId) {
        roleService.assignRoleToUser(roleId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roleId}/users/{userId}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long roleId, @PathVariable Long userId) {
        roleService.removeRoleFromUser(roleId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{roleId}/users")
    public List<com.mycompany.myapp.domain.User> findUsersByRole(@PathVariable Long roleId) {
        return roleService.findUsersByRoleId(roleId);
    }
}

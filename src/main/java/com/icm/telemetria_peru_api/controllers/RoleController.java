package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.RoleModel;
import com.icm.telemetria_peru_api.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/{id}")
    public ResponseEntity<RoleModel> getRoleById(@PathVariable Long id) {
        Optional<RoleModel> role = roleService.getById(id);
        return role.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RoleModel>> getAllRoles() {
        List<RoleModel> roles = roleService.getAll();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<RoleModel> createRole(@RequestBody RoleModel roleModel) {
        RoleModel createdRole = roleService.createRole(roleModel);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<RoleModel> updateRole(@RequestBody RoleModel roleModel, @PathVariable Long id) {
        RoleModel updatedRole = roleService.updateRole(roleModel, id);
        return updatedRole != null ? new ResponseEntity<>(updatedRole, HttpStatus.OK) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SA')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

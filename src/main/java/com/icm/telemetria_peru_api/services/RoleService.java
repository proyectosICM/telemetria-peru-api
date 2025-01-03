package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.RoleModel;
import com.icm.telemetria_peru_api.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Optional<RoleModel> getById(Long id) {
        return roleRepository.findById(id);
    }

    public List<RoleModel> getAll() {
        return roleRepository.findAll();
    }

    public RoleModel createRole(RoleModel role) {
        return roleRepository.save(role);
    }

    public RoleModel updateRole(RoleModel role, Long id) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setName(role.getName());
                    return roleRepository.save(existingRole);
                })
                .orElse(null);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}

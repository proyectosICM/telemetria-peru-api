package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.RoleModel;
import com.icm.telemetria_peru_api.repositories.RoleRepository;
import com.icm.telemetria_peru_api.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Optional<RoleModel> getById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<RoleModel> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public RoleModel createRole(RoleModel role) {
        return roleRepository.save(role);
    }

    @Override
    public RoleModel updateRole(RoleModel role, Long id) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setName(role.getName());
                    return roleRepository.save(existingRole);
                })
                .orElse(null);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}

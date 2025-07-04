package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.RoleModel;
import com.icm.telemetria_peru_api.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface RoleService {
    Optional<RoleModel> getById(Long id);
    List<RoleModel> getAll();
    RoleModel createRole(RoleModel role);
    RoleModel updateRole(RoleModel role, Long id);
    void deleteRole(Long id);
}

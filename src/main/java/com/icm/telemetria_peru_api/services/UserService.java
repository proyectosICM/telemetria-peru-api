package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.UserModel;
import com.icm.telemetria_peru_api.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface UserService {
    Optional<UserModel> findById(Long userId);
    Optional<UserModel> findByUsername(String username);
    List<UserModel> findAll();
    Page<UserModel> findAll(Pageable pageable);
    List<UserModel> findByStatus(Boolean status);
    Page<UserModel> findByStatus(Boolean status, Pageable pageable);
    List<UserModel> findByCompanyModelId(Long companyId);
    Page<UserModel> findByCompanyModelId(Long companyId, Pageable pageable);
    List<UserModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status);
    Page<UserModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status, Pageable pageable);
    UserModel save(UserModel userModel);
    UserModel updateAllData(Long userId,UserModel userModel);
    UserModel updatePassword(Long userId, UserModel userModel);
    UserModel changeStatus(Long userId);
}

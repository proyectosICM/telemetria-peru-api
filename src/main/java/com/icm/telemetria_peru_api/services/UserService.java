package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.UserModel;
import com.icm.telemetria_peru_api.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<UserModel> findById(Long userId){
        return userRepository.findById(userId);
    }

    /** Retrieves users, as a list and paginated. */
    public List<UserModel> findAll(){
        return userRepository.findAll();
    }
    public Page<UserModel> findAll(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    /** Lists and paginated findByStatus **/
    public List<UserModel> findByStatus(Boolean status){
        return userRepository.findByStatus(status);
    }
    public Page<UserModel> findByStatus(Boolean status, Pageable pageable){
        return userRepository.findByStatus(status, pageable);
    }

    /** Retrieves users by company, as a list and paginated. */
    public List<UserModel> findByCompanyModelId(Long companyId){
        return userRepository.findByCompanyModelId(companyId);
    }
    public Page<UserModel> findByCompanyModelId(Long companyId, Pageable pageable){
        return userRepository.findByCompanyModelId(companyId, pageable);
    }

    /** Retrieves users by company and status, as a list and paginated. */
    public List<UserModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status){
        return userRepository.findByCompanyModelIdAndStatus(companyId, status);
    }
    public Page<UserModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status, Pageable pageable){
        return userRepository.findByCompanyModelIdAndStatus(companyId, status, pageable);
    }

    /** More CRUD methods */
    public UserModel save(@Valid UserModel userModel){
        return userRepository.save(userModel);
    }

    /** Update all data */
    public UserModel updateAllData(Long userId,@Valid UserModel userModel){
        return userRepository.findById(userId)
                .map(existing -> {
                    existing.setUsername(userModel.getUsername());
                    existing.setPassword(userModel.getPassword());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    /** Update password */
    public UserModel updatePassword(Long userId,@Valid UserModel userModel){
        return userRepository.findById(userId)
                .map(existing -> {
                    existing.setPassword(userModel.getPassword());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    /**  status update */
    public UserModel changeStatus(Long userId){
        return userRepository.findById(userId)
                .map(existing -> {
                    existing.setStatus(!existing.getStatus());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("User with id" + userId + " no found"));
    }
}

package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.UserModel;
import com.icm.telemetria_peru_api.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Validates the given UserModel to ensure that the email and username are unique. */
    private void validateUser(UserModel userModel) {
        if (userRepository.existsByEmail(userModel.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByUsername(userModel.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    private UserModel getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    public Optional<UserModel> findById(Long userId){
        return userRepository.findById(userId);
    }

    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username);
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

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    public UserModel save(UserModel userModel) {
        validateUser(userModel);
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        return userRepository.save(userModel);
    }

    /** Update all data */
    public UserModel updateAllData(Long userId,UserModel userModel){
        UserModel existing = getUserById(userId);

        boolean emailChanged = !existing.getEmail().equals(userModel.getEmail());
        boolean usernameChanged = !existing.getUsername().equals(userModel.getUsername());

        if (emailChanged || usernameChanged) {
            validateUser(userModel);
        }

        existing.setEmail(userModel.getEmail());
        existing.setUsername(userModel.getUsername());
        if (userModel.getPassword() != null && !userModel.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(userModel.getPassword()));
        }

        return userRepository.save(existing);
    }

    /** Update password */
    public UserModel updatePassword(Long userId, UserModel userModel){
        UserModel existing = getUserById(userId);
        existing.setPassword(passwordEncoder.encode(userModel.getPassword()));
        return userRepository.save(existing);
    }

    /**  status update */
    public UserModel changeStatus(Long userId){
        UserModel existing = getUserById(userId);
        existing.setStatus(!existing.getStatus());
        return userRepository.save(existing);
    }
}

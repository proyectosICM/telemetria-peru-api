package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.UserModel;
import com.icm.telemetria_peru_api.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserModel> findById(@PathVariable @NotNull Long userId) {
        return userService.findById(userId)
                .map(company -> new ResponseEntity<>(company, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/info/{username}")
    public ResponseEntity<UserModel> findByUsername(@PathVariable("username") String username){
        Optional<UserModel> user = userService.findByUsername(username);

        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Lists and paginated findByStatus **/
    @GetMapping
    public List<UserModel> findAll() {
        return userService.findAll();
    }
    @GetMapping("/paged")
    public ResponseEntity<Page<UserModel>> findById(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserModel> dataModel = userService.findAll(pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves users by company, as a list and paginated. */
    @GetMapping("/findByStatus")
    public ResponseEntity<List<UserModel>> findByStatus(@RequestParam @NotNull Boolean status){
        List<UserModel> dataModel = userService.findByStatus(status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }
    @GetMapping("/findByStatus-paged")
    public ResponseEntity<Page<UserModel>> findByStatusPage(@RequestParam @NotNull Boolean status,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserModel> dataModel = userService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by company, as a list and paginated. */
    @GetMapping("/findByCompany/{companyId}")
    public ResponseEntity<List<UserModel>> findByCompanyModelId(@PathVariable @NotNull Long companyId){
        List<UserModel> dataModel = userService.findByCompanyModelId(companyId);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/findByCompany-paged/{companyId}")
    public ResponseEntity<Page<UserModel>> findByCompanyModelId(@PathVariable @NotNull Long companyId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserModel> dataModel = userService.findByCompanyModelId(companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by company and status, as a list and paginated. */
    @GetMapping("/findByCompanyAndStatus/{companyId}")
    public ResponseEntity<List<UserModel>> findByCompanyModelIdAndStatus(@PathVariable @NotNull Long companyId, @RequestParam Boolean status){
        List<UserModel> dataModel = userService.findByCompanyModelIdAndStatus(companyId, status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/findByCompanyAndStatus-paged/{companyId}")
    public ResponseEntity<Page<UserModel>> findByCompanyModelIdAndStatus(@PathVariable @NotNull Long companyId,
                                                                           @RequestParam Boolean status,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserModel> dataModel = userService.findByCompanyModelIdAndStatus(companyId, status , pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** More CRUD methods */
    @PostMapping
    public ResponseEntity<UserModel> save(@RequestBody @Valid UserModel userModel){
        UserModel dataModel = userService.save(userModel);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Update all data */
    @PutMapping("/{userId}")
    public ResponseEntity<UserModel> updateMainData(@PathVariable @NotNull Long userId, @RequestBody @Valid UserModel userModel){
        UserModel dataModel = userService.updateAllData(userId, userModel);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /** Update password */
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<UserModel> updatePassword(@PathVariable @NotNull Long userId, @RequestBody @Valid UserModel userModel){
        UserModel dataModel = userService.updatePassword(userId, userModel);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/changeStatus/{userId}")
    public ResponseEntity<UserModel> changeStatus(@PathVariable @NotNull Long userId){
        UserModel dataModel = userService.changeStatus(userId);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

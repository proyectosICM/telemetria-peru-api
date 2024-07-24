package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.models.UserModel;
import com.icm.telemetria_peru_api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserModel> findById(@PathVariable Long userId) {
        return userService.findById(userId)
                .map(company -> new ResponseEntity<>(company, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /** Lists and paginated findByStatus **/
    @GetMapping
    public List<UserModel> findAll() {
        return userService.findAll();
    }
    @GetMapping("/page")
    public ResponseEntity<Page<UserModel>> findById(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserModel> dataModel = userService.findAll(pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves users by company, as a list and paginated. */
    @GetMapping("/findByStatus/{status}")
    public ResponseEntity<List<UserModel>> findByStatus(@RequestParam Boolean status){
        List<UserModel> dataModel = userService.findByStatus(status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }
    @GetMapping("/findByStatus-page/{status}")
    public ResponseEntity<Page<UserModel>> findByStatusPage(@RequestParam Boolean status,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserModel> dataModel = userService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by company, as a list and paginated. */
    @GetMapping("/findByCompany/{companyId}")
    public ResponseEntity<List<UserModel>> findByCompanyModelId(@PathVariable Long companyId){
        List<UserModel> dataModel = userService.findByCompanyModelId(companyId);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/findByCompany-page/{companyId}")
    public ResponseEntity<Page<UserModel>> findByCompanyModelId(@PathVariable Long companyId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserModel> dataModel = userService.findByCompanyModelId(companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    /** Retrieves drivers by company and status, as a list and paginated. */
    @GetMapping("/findByCompanyAndStatus/{companyId}")
    public ResponseEntity<List<UserModel>> findByCompanyModelIdAndStatus(@PathVariable Long companyId, @RequestParam Boolean status){
        List<UserModel> dataModel = userService.findByCompanyModelIdAndStatus(companyId, status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/findByCompanyAndStatus-page/{companyId}")
    public ResponseEntity<Page<UserModel>> findByCompanyModelIdAndStatus(@PathVariable Long companyId,
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
    public ResponseEntity<UserModel> updateMainData(@PathVariable Long userId, @RequestBody UserModel userModel){
        UserModel dataModel = userService.updateAllData(userId, userModel);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /** Update password */
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<UserModel> updatePassword(@PathVariable Long userId, @RequestBody UserModel userModel){
        UserModel dataModel = userService.updatePassword(userId, userModel);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/changeStatus/{userId}")
    public ResponseEntity<UserModel> changeStatus(@PathVariable Long userId){
        UserModel dataModel = userService.changeStatus(userId);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

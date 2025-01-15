package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.ImagesCLModel;
import com.icm.telemetria_peru_api.services.CompanyService;
import com.icm.telemetria_peru_api.services.ImagesCLService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/images-cl")
@RequiredArgsConstructor
public class ImagesCLController {

    @Value("${file.image}")
    private String pathimg;

    private final ImagesCLService imagesCLService;


    @GetMapping("/images")
    public ResponseEntity<Resource> serveImage(@RequestParam String filename) {
        try {
            // Construir la ruta personalizada usando los parámetros company e irregularity
            String fullPath = pathimg +  "/" + filename;

            // Crear un Path a partir de la ruta completa
            Path file = Paths.get(fullPath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + fullPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagesCLModel> findById(@PathVariable @NotNull Long id) {
        try {
            ImagesCLModel data = imagesCLService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<ImagesCLModel> findAll() {
        return imagesCLService.findAll();
    }

    @GetMapping("/paged")
    public Page<ImagesCLModel> findAll(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return imagesCLService.findAll(pageable);
    }

    @GetMapping("/by-checklist/{id}")
    public ResponseEntity<?> findByChecklistRecord(@PathVariable Long id){
        try {
            List<ImagesCLModel> data = imagesCLService.findByChecklistRecord(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-checklist-paged/{id}")
    public ResponseEntity<Page<ImagesCLModel>> findByChecklistRecord(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ImagesCLModel> data = imagesCLService.findByChecklistRecord(id, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{clId}")
    public ResponseEntity<?> save(@RequestParam("file") MultipartFile file, @PathVariable Long clId) {
        try {
            ImagesCLModel data = imagesCLService.save(file, clId);
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        } catch (IOException e) {   
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            imagesCLService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

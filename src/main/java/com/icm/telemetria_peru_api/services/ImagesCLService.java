package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import com.icm.telemetria_peru_api.models.ImagesCLModel;
import com.icm.telemetria_peru_api.repositories.ChecklistRecordRepository;
import com.icm.telemetria_peru_api.repositories.ChecklistTypeRepository;
import com.icm.telemetria_peru_api.repositories.ImagesCLRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImagesCLService {

    private final ImagesCLRepository imagesCLRepository;
    private final ChecklistRecordRepository checklistRecordRepository;

    @Value("${file.image}")
    private String fileImagen;

    public ImagesCLModel findById(Long id){
        return imagesCLRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<ImagesCLModel> findAll(){
        return imagesCLRepository.findAll();
    }

    public Page<ImagesCLModel> findAll(Pageable pageable){
        return imagesCLRepository.findAll(pageable);
    }

    public List<ImagesCLModel> findByChecklistRecord(Long clId){
        return imagesCLRepository.findByChecklistRecordModelId(clId);
    }

    public Page<ImagesCLModel> findByChecklistRecord(Long clId, Pageable pageable){
        return imagesCLRepository.findByChecklistRecordModelId(clId, pageable);
    }


    // Guardar imagen con el registro asociado
    public ImagesCLModel save(MultipartFile file, Long clId) throws IOException {
        // Obtener el nombre original del archivo
        String originalFilename = file.getOriginalFilename();

        // Verificar que el nombre no sea nulo o vacío
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        // Extraer la extensión del archivo
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // Definir la ruta base donde se almacenarán las imágenes
        String basePath = fileImagen + "/";

        // Crear directorio si no existe
        File baseDirectory = new File(basePath);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        // Generar un nombre de archivo único usando UUID
        String randomFileName = UUID.randomUUID().toString();

        // Crear la ruta completa del archivo
        String filePath = basePath + randomFileName + fileExtension;
        File newFile = new File(filePath);

        // Manejar la posible colisión de nombres, aunque sea muy raro con UUID
        int i = 1;
        while (newFile.exists()) {
            randomFileName = UUID.randomUUID().toString();
            filePath = basePath + randomFileName + fileExtension;
            newFile = new File(filePath);
            i++;
        }

        // Guardar la imagen en la ruta generada
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        // Crear una instancia del modelo relacionado con la imagen (ImagesCLModel)
        ImagesCLModel imagesCLModel = new ImagesCLModel();
        ChecklistRecordModel cl = new ChecklistRecordModel();
        Optional<ChecklistRecordModel> cl2 = checklistRecordRepository.findById(clId);

        // Establecer el nombre del archivo en el modelo (sin la ruta completa, solo el nombre y extensión)
        imagesCLModel.setUrlImage(randomFileName + fileExtension);
        imagesCLModel.setChecklistRecordModel(cl2.get()); // Asociar la imagen al checklist


        return imagesCLRepository.save(imagesCLModel);
    }
/*
        try {
            // Guardar archivo en el sistema
            file.transferTo(newFile);
            ImagesCLModel imagesCLModel = new ImagesCLModel();
            ChecklistRecordModel cl = new ChecklistRecordModel();
            cl.setId(clId);
            // Establecer la ruta completa del archivo en el objeto de modelo
            imagesCLModel.setUrlImage(randomFileName + fileExtension); // Aquí se guarda la ruta completa de la imagen.
            imagesCLModel.setChecklistRecordModel(cl);
            // Guardar el objeto ImagesCLModel en la base de datos
            return imagesCLRepository.save(imagesCLModel);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
        }

        */

    public void deleteById(Long id) {
        imagesCLRepository.deleteById(id);
    }

}

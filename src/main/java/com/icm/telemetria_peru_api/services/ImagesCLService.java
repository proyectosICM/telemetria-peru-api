package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.models.ImagesCLModel;
import com.icm.telemetria_peru_api.repositories.ImagesCLRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class ImagesCLService {
    @Autowired
    private ImagesCLRepository imagesCLRepository;

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
    public ImagesCLModel save(MultipartFile file, Long clId) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String basePath = fileImagen + "/";

        // Crear directorio si no existe
        File baseDirectory = new File(basePath);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        // Generar un nombre de archivo único
        String randomFileName = UUID.randomUUID().toString();

        // Construir la ruta completa del archivo con el nombre aleatorio
        String filePath = basePath + randomFileName + fileExtension;
        File newFile = new File(filePath);

        // Manejar colisión de nombres (aunque UUID es casi único)
        while (newFile.exists()) {
            randomFileName = UUID.randomUUID().toString();
            filePath = basePath + randomFileName + fileExtension;
            newFile = new File(filePath);
        }

        try {
            // Guardar archivo en el sistema
            file.transferTo(newFile);
            ImagesCLModel imagesCLModel = new ImagesCLModel();
            ChecklistRecordModel cl = new ChecklistRecordModel();
            cl.setId(clId);
            // Establecer la ruta completa del archivo en el objeto de modelo
            System.out.println("ppp" + filePath);
            imagesCLModel.setUrlImage(filePath); // Aquí se guarda la ruta completa de la imagen.
            imagesCLModel.setChecklistRecordModel(cl);
            // Guardar el objeto ImagesCLModel en la base de datos
            return imagesCLRepository.save(imagesCLModel);
            //System.out.println("S" + clId );
            //return "dd";
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
        }
    }


    public void deleteById(Long id) {
        imagesCLRepository.deleteById(id);
    }

}

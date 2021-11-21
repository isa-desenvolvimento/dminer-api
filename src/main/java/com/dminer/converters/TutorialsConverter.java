package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.TutorialsDTO;
import com.dminer.dto.TutorialsRequestDTO;
import com.dminer.entities.Tutorials;
import com.dminer.entities.Category;
import com.dminer.entities.Permission;
import com.dminer.repository.CategoryRepository;
import com.dminer.repository.PermissionRepository;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutorialsConverter {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    
    public TutorialsDTO entityToDTO(Tutorials entity) {
        TutorialsDTO dto = new TutorialsDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent() != null ? entity.getContent() : "");
        dto.setTitle(entity.getTitle() != null ? entity.getTitle() : "");
        dto.setDate(entity.getDate() != null ? UtilDataHora.timestampToString(entity.getDate()) : null);        
        dto.setImage(entity.getImage());
        if (entity.getPermission() != null)
            dto.setPermission(entity.getPermission().getId());
        if (entity.getCategory() != null)
            dto.setCategory(entity.getPermission().getId());        
        return dto;
    }

    public Tutorials dtoToEntity(TutorialsDTO dto) {
        Tutorials c = new Tutorials();
        c.setId(dto.getId());
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        if (dto.getPermission() != null) {
            Optional<Permission> findById = permissionRepository.findById(dto.getPermission());
            if (findById.isPresent())
                c.setPermission(findById.get());
        }
        if (dto.getCategory() != null) {
            Optional<Category> findById2 = categoryRepository.findById(dto.getCategory());
            if (findById2.isPresent())
                c.setCategory(findById2.get());
        }
        c.setImage(dto.getImage());
        return c;
    }

    public Tutorials requestDtoToEntity(TutorialsRequestDTO dto) {
        Tutorials c = new Tutorials();
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null); 
        if (dto.getPermission() != null) {
            Optional<Permission> findById = permissionRepository.findById(dto.getPermission());
            if (findById.isPresent())
                c.setPermission(findById.get());
        }
        if (dto.getCategory() != null) {
            Optional<Category> findById2 = categoryRepository.findById(dto.getCategory());
            if (findById2.isPresent())
                c.setCategory(findById2.get());
        }
        c.setImage(dto.getImage());
        return c;
    }

}

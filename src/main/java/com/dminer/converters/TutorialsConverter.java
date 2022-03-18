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

    public TutorialsDTO entityToDTO(Tutorials entity) {
        TutorialsDTO dto = new TutorialsDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent() != null ? entity.getContent() : "");
        dto.setTitle(entity.getTitle() != null ? entity.getTitle() : "");
        dto.setDate(entity.getDate() != null ? UtilDataHora.timestampToStringOrNow(entity.getDate()) : null);        
        dto.setImage(entity.getImage());
        dto.setPermission(entity.getPermission());
        if (entity.getCategory() != null)
            dto.setCategory(entity.getCategory().getName());        
        return dto;
    }

    public Tutorials dtoToEntity(TutorialsDTO dto) {
        Tutorials c = new Tutorials();
        c.setId(dto.getId());
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        c.setPermission(dto.getPermission());
        if (dto.getCategory() != null) {
            Optional<Category> category = categoryRepository.findById(Integer.parseInt(dto.getCategory()));
            if (category.isPresent())
                c.setCategory(category.get());
        }
        c.setImage(dto.getImage());
        return c;
    }

    public Tutorials requestDtoToEntity(TutorialsRequestDTO dto) {
        Tutorials c = new Tutorials();
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        c.setPermission(dto.getPermission());
        if (dto.getCategory() != null) {
            Optional<Category> category = categoryRepository.findById(dto.getCategory());
            if (category.isPresent())
                c.setCategory(category.get());
        }
        c.setImage(dto.getImage());
        return c;
    }

}

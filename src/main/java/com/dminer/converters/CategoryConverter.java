package com.dminer.converters;

import com.dminer.dto.CategoryDTO;
import com.dminer.dto.CategoryRequestDTO;
import com.dminer.entities.Category;

import org.springframework.stereotype.Service;

@Service
public class CategoryConverter {

    public CategoryDTO entityToDTO(Category entity) {
        CategoryDTO dto = new CategoryDTO();
        if (entity == null) return dto;
        dto.setId(entity.getId());
        dto.setDescrible(entity.getDescrible() != null ? entity.getDescrible() : "");
        return dto;
    }

    public Category dtoToEntity(CategoryDTO dto) {
        Category c = new Category();
        if (dto == null) return c;
        c.setId(dto.getId());
        c.setDescrible(dto.getDescrible() != null ? dto.getDescrible() : "");
        return c;
    }

    public Category requestDtoToEntity(CategoryRequestDTO dto) {
        Category c = new Category();
        if (dto == null) return c;
        c.setDescrible(dto.getDescrible() != null ? dto.getDescrible() : "");
        return c;
    }
}

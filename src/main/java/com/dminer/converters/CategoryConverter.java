package com.dminer.converters;

import com.dminer.dto.CategoryDTO;
import com.dminer.dto.CategoryRequestDTO;
import com.dminer.entities.Category;

import org.springframework.stereotype.Service;

@Service
public class CategoryConverter {

    public CategoryDTO entityToDTO(Category entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setDescrible(entity.getDescrible() != null ? entity.getDescrible() : "");
        return dto;
    }

    public Category dtoToEntity(CategoryDTO dto) {
        Category c = new Category();
        c.setDescrible(dto.getDescrible() != null ? dto.getDescrible() : "");
        return c;
    }

    public Category requestDtoToEntity(CategoryRequestDTO dto) {
        Category c = new Category();
        c.setDescrible(dto.getDescrible() != null ? dto.getDescrible() : "");
        return c;
    }
}

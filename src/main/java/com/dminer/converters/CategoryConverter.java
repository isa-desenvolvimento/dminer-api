package com.dminer.converters;

import com.dminer.dto.CategoryDTO;
import com.dminer.dto.CategoryRequestDTO;
import com.dminer.entities.Category;

import org.springframework.stereotype.Service;

@Service
public class CategoryConverter implements Converter<Category, CategoryDTO, CategoryRequestDTO> {

    @Override
    public Category dtoToEntity(CategoryDTO dto) {
        Category c = new Category();
        if (dto == null) return c;
        c.setId(dto.getId());
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : null);
        return c;
    }

    @Override
    public CategoryDTO entityToDto(Category entity) {
        CategoryDTO dto = new CategoryDTO();
        if (entity == null) return dto;
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle() != null ? entity.getTitle() : null);
        return dto;
    }

    @Override
    public Category dtoRequestToEntity(CategoryRequestDTO requestDto) {
        Category c = new Category();
        if (requestDto == null) return c;
        c.setTitle(requestDto.getTitle() != null ? requestDto.getTitle() : null);
        return c;
    }
}

package com.dminer.converters;

import com.dminer.dto.DocumentDTO;
import com.dminer.dto.DocumentRequestDTO;
import com.dminer.entities.Document;
import com.dminer.enums.Category;
import com.dminer.enums.Permissions;

import org.springframework.stereotype.Service;


@Service
public class DocumentConverter {
    

    public Document dtoToEntity(DocumentDTO dto) {
        Document doc = new Document();
        doc.setId(dto.getId());
        if (containsCategory(dto.getCategory()))
            doc.setCategory(Category.valueOf(dto.getCategory()));
        if (containsPermission(dto.getPermission()))
            doc.setPermission(Permissions.valueOf(dto.getPermission()));
        doc.setContent(dto.getContent());
        doc.setTitulo(dto.getTitulo());
        return doc;
    }

    public Document requestDtoToEntity(DocumentRequestDTO dto) {
        Document doc = new Document();
        if (containsCategory(dto.getCategory()))
            doc.setCategory(Category.valueOf(dto.getCategory()));
        if (containsPermission(dto.getPermission()))
            doc.setPermission(Permissions.valueOf(dto.getPermission()));
        doc.setContent(dto.getContent());
        doc.setTitulo(dto.getTitulo());
        return doc;
    }

    public DocumentDTO entityToDto(Document doc) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(doc.getId());
        dto.setCategory(doc.getCategory().name());
        dto.setPermission(doc.getPermission().name());
        dto.setContent(doc.getContent());
        dto.setTitulo(doc.getTitulo());
        return dto;
    }

    private boolean containsCategory(String category) {
        if (category == null) return false;
        Category[] categories =  Category.values();
        for (Category cat : categories) {
            if (cat.name().equals(category))
                return true;
        }
        return false;
    }

    private boolean containsPermission(String permission) {
        if (permission == null) return false;
        Permissions[] permissions =  Permissions.values();
        for (Permissions per : permissions) {
            if (per.name().equals(permission))
                return true;
        }
        return false;
    }

}

package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.DocumentDTO;
import com.dminer.dto.DocumentRequestDTO;
import com.dminer.entities.Category;
import com.dminer.entities.Document;
import com.dminer.entities.Permission;
import com.dminer.repository.CategoryRepository;
import com.dminer.repository.PermissionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DocumentConverter {
    
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    
    public Document dtoToEntity(DocumentDTO dto) {
        Document doc = new Document();
        doc.setId(dto.getId());
        Optional<Category> findById2 = categoryRepository.findById(dto.getCategory());
        if (findById2.isPresent())
            doc.setCategory(findById2.get());
        Optional<Permission> findById = permissionRepository.findById(dto.getPermission());
        if (findById.isPresent())
            doc.setPermission(findById.get());
        doc.setContentLink(dto.getContentLink() != null ? dto.getContentLink() : null);
        doc.setTitle(dto.getTitle());
        return doc;
    }

    public Document requestDtoToEntity(DocumentRequestDTO dto) {
        Document doc = new Document();
        Optional<Category> findById2 = categoryRepository.findById(dto.getCategory());
        if (findById2.isPresent())
            doc.setCategory(findById2.get());
        Optional<Permission> findById = permissionRepository.findById(dto.getPermission());
        if (findById.isPresent())
            doc.setPermission(findById.get());
        doc.setContentLink(dto.getContentLink() != null ? dto.getContentLink() : null);
        doc.setTitle(dto.getTitle());
        return doc;
    }

    public DocumentDTO entityToDto(Document doc) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(doc.getId());
        dto.setCategory(doc.getCategory().getId());
        dto.setPermission(doc.getPermission().getId());
        dto.setContentLink(doc.getContentLink() != null ? doc.getContentLink() : null);
        dto.setTitle(doc.getTitle());
        return dto;
    }

}

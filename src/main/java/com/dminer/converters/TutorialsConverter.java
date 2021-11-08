package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.TutorialsDTO;
import com.dminer.dto.TutorialsRequestDTO;
import com.dminer.dto.TutorialsRequestDTO;
import com.dminer.entities.Tutorials;
import com.dminer.entities.Tutorials;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.enums.Category;
import com.dminer.enums.Profiles;
import com.dminer.services.PostService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;
import com.dminer.utils.UtilNumbers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TutorialsConverter {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    public TutorialsDTO entityToDTO(Tutorials entity) {
        TutorialsDTO dto = new TutorialsDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent() != null ? entity.getContent() : "");
        dto.setTitle(entity.getTitle() != null ? entity.getTitle() : "");
        dto.setDate(entity.getDate() != null ? UtilDataHora.timestampToString(entity.getDate()) : null);        
        dto.setImage(entity.getImage());        
        dto.setProfiles(entity.getProfile().name());
        dto.setCategory(entity.getCategory().name());
        
        return dto;
    }

    public Tutorials dtoToEntity(TutorialsDTO dto) {
        Tutorials c = new Tutorials();
        c.setId(dto.getId());
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        if (contains(dto.getProfiles()))
            c.setProfile(Profiles.valueOf(dto.getProfiles()));
        if (containsCat(dto.getCategory()))
            c.setCategory(Category.valueOf(dto.getCategory()));
        c.setImage(dto.getImage());
        return c;
    }

    public Tutorials requestDtoToEntity(TutorialsRequestDTO dto) {
        Tutorials c = new Tutorials();
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);        
        if (contains(dto.getProfiles()))
            c.setProfile(Profiles.valueOf(dto.getProfiles()));
        if (containsCat(dto.getCategory()))
            c.setCategory(Category.valueOf(dto.getCategory()));
        c.setImage(dto.getImage());
        return c;
    }

    private boolean contains(String event) {
        Profiles[] events =  Profiles.values();
        for (Profiles eventsTime : events) {
            if (eventsTime.name().equals(event))
                return true;
        }
        return false;
    }

    private boolean containsCat(String event) {
        Category[] events =  Category.values();
        for (Category eventsTime : events) {
            if (eventsTime.name().equals(event))
                return true;
        }
        return false;
    }
}

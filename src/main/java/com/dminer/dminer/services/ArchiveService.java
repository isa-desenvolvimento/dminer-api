package com.dminer.dminer.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dminer.dminer.entities.Post;
import com.dminer.dminer.entities.abstracts.Archive;
import com.dminer.dminer.repository.ArchiveRepository;
import com.dminer.dminer.services.interfaces.IArchiveService;

@Service
public class ArchiveService implements IArchiveService{

	@Autowired
	private ArchiveRepository archiveRepository;
	
	private static final Logger log = LoggerFactory.getLogger(ArchiveService.class);
	
	
	@Override
	public Archive persist(Archive archive) {
		log.info("Persistindo arquivo: {}", archive);
		return archiveRepository.save(archive);
	}
	
	@Override
	public Optional<Archive> findById(int id) {
		log.info("Buscando um arquivo pelo id {}", id);
		return archiveRepository.findById(id);
	}
	
	@Override
	public Optional<List<Archive>> findByPost(Post post) {
		log.info("Buscando um arquivo pelo post {}", post);
		return archiveRepository.findByPost(post);
	}
	
	@Override
	public void delete(int id) {
		log.info("Excluindo um arquivo pelo id {}", id);
		archiveRepository.deleteById(id);
	}
}

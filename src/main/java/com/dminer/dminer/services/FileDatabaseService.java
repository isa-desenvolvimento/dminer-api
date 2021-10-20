package com.dminer.dminer.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dminer.dminer.entities.FileInfo;
import com.dminer.dminer.entities.Post;
import com.dminer.dminer.repository.FilesDatabaseRepository;
import com.dminer.dminer.services.interfaces.IFileDatabaseService;

@Service
public class FileDatabaseService implements IFileDatabaseService {

	private static final Logger log = LoggerFactory.getLogger(FileDatabaseService.class);
	
	@Autowired
	private FilesDatabaseRepository fileDatabaseRepository;
	
	@Override
	public Optional<FileInfo> persist(FileInfo file) {
		log.info("Persistindo arquivo: {}", file);
		return Optional.ofNullable(fileDatabaseRepository.save(file));
	}

	@Override
	public Optional<FileInfo> findById(int id) {
		log.info("Buscando um arquivo pelo id {}", id);
		return fileDatabaseRepository.findById(id);
	}

	@Override
	public Optional<List<FileInfo>> findByPost(Post post) {
		log.info("Buscando um arquivo pelo post {}", post);
		return fileDatabaseRepository.findByPost(post);
	}

	@Override
	public void delete(int id) {
		fileDatabaseRepository.deleteById(id);
	}
	
}

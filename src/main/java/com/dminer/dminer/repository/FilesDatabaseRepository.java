package com.dminer.dminer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dminer.dminer.entities.FileInfo;
import com.dminer.dminer.entities.Post;

@Repository
public interface FilesDatabaseRepository extends JpaRepository<FileInfo, Integer> {

	@Transactional(readOnly = true)
	Optional<List<FileInfo>> findByPost(Post post);
}

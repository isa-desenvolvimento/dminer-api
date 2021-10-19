package com.dminer.dminer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dminer.dminer.entities.Post;
import com.dminer.dminer.entities.abstracts.Archive;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Integer> {

	@Transactional(readOnly = true)
	Optional<List<Archive>> findByPost(Post post);
}

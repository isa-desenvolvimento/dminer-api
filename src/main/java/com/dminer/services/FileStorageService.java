package com.dminer.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dminer.constantes.Constantes;
import com.dminer.entities.Post;
import com.dminer.services.interfaces.IFilesStorageService;

@Service
public class FileStorageService implements IFilesStorageService {

	
	public final Path root = Paths.get(Constantes.ROOT_UPLOADS);
	
	private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
	
	@Override
	public void init() {
		log.info("Criando um diretório uploads");
		try {
			createDirectory(root);
		} catch (IOException e) {
			log.error(e.getCause().getMessage());
		}
	}
	
	@Override
	public boolean save(MultipartFile file, Path path) {
		try {
			Path path2 = path.resolve(file.getOriginalFilename());
			if (! Files.exists(path2)) {
				log.info("Salvando arquivo no diretório: {}", path2.toString());
				Files.copy(file.getInputStream(), path2);
				return true;
			}
			return false;
	    } catch (Exception e) {
	    	throw new RuntimeException("Erro: Não foi possível salvar este arquivo. " + file.getOriginalFilename() + "\t" + e.getMessage());
	    }
	}
	
	@Override
	public Optional<Resource> findById(int id, Path path) {
		try {
			String filename = ""; // buscar no banco
			Path file = path.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return Optional.of(resource);
			} else {
				throw new RuntimeException("Não foi possível ler o arquivo!");
			}
	    } catch (MalformedURLException e) {
	    	throw new RuntimeException("Erro: " + e.getMessage());
	    }
	}	
	
	@Override
	public Optional<Stream<Path>> findByPost(Post post, Path path) {
		try {
			return Optional.ofNullable(Files.walk(path, 1).filter(path2 -> !path2.equals(path)).map(path::relativize));
		} catch (IOException e) {
			throw new RuntimeException("Could not load the files!");
		}
	}
		
	@Override
	public void delete(Path path) {
		log.info("Excluindo um diretório/arquivo {}", path);
		FileSystemUtils.deleteRecursively(path.toFile());
	}

	public void createDirectory(Path path) throws IOException {
		try {			
			if (! Files.exists(path))
				Files.createDirectories(path);
	    } catch (IOException e) {
			e.printStackTrace();
	    	throw e;
	    }
	}

	public boolean existsDirectory(Path path) {
		return Files.exists(path);		
	}
}

package net.kang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Kind;

public interface KindRepository extends MongoRepository<Kind, String>{
	Optional<Kind> findByName(String name);
	List<Kind> findByNameContaining(String name);
	void deleteByNameContaining(String name);
}

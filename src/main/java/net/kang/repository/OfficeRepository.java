package net.kang.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Office;

public interface OfficeRepository extends MongoRepository<Office, String>{
	List<Office> findByNameContaining(String name);
	void deleteByNameContaining(String name);
}

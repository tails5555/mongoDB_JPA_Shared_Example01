package net.kang.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Office;

public interface OfficeRepository extends MongoRepository<Office, String>{
	void deleteByNameContaining(String name);
}

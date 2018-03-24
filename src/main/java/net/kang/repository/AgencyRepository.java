package net.kang.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Agency;

public interface AgencyRepository extends MongoRepository<Agency, String>{
	Optional<Agency> findByName(String name);
}

package net.kang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Agency;
import net.kang.domain.Office;

public interface AgencyRepository extends MongoRepository<Agency, String>{
	void deleteByNameContaining(String name);
	Optional<Agency> findByName(String name);
	List<Agency> findByOffice(Office office);
	List<Agency> findByNameContaining(String name);
}

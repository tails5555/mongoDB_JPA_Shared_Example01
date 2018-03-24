package net.kang.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Park;

public interface ParkRepository extends MongoRepository<Park, String>{
	Optional<Park> findByManageNo(String manageNo);
}

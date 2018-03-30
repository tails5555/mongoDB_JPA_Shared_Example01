package net.kang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Agency;
import net.kang.domain.Kind;
import net.kang.domain.Park;

public interface ParkRepository extends MongoRepository<Park, String>{
	Optional<Park> findByManageNo(String manageNo);
	long countByKind(Kind kind);
	long countByAgency(Agency agency);
	List<Park> findByKind(Kind kind);
	List<Park> findByAgency(Agency agency);
	List<Park> findByNameContaining(String name);
	List<Park> findByAreaBetween(double area1, double area2);
	List<Park> findByCultFacilityContains(String[] cultFacilities);
	List<Park> findByConvFacilityContains(String[] convFacilities);
	void deleteByManageNo(String manageNo);
}

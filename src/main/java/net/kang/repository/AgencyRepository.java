package net.kang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Agency;
import net.kang.domain.Office;

public interface AgencyRepository extends MongoRepository<Agency, String>{
	void deleteByNameContaining(String name); // 이름 포함 삭제
	Optional<Agency> findByName(String name); // 이름으로 찾기. 이름을 UNIQUE로 설정.
	List<Agency> findByOffice(Office office); // 시구청으로 찾기
	List<Agency> findByNameContaining(String name); // 이름 포함 검색
}

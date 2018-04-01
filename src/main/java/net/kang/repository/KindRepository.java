package net.kang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Kind;

public interface KindRepository extends MongoRepository<Kind, String>{
	Optional<Kind> findByName(String name);  // 이름으로 찾기. 이름을 UNIQUE로 설정.
	List<Kind> findByNameContaining(String name); // 이름 포함 검색
	void deleteByNameContaining(String name); // 이름 포함 삭제
}

package net.kang.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.kang.domain.Agency;
import net.kang.domain.Kind;
import net.kang.domain.Park;

public interface ParkRepository extends MongoRepository<Park, String>{
	Optional<Park> findByManageNo(String manageNo); // 관리번호로 검색. 관리번호는 UNIQUE 설정.
	long countByKind(Kind kind); // 종류로 카운팅을 한 결과
	long countByAgency(Agency agency); // 기관으로 카운팅을 한 결과
	List<Park> findByKind(Kind kind); // 종류로 찾기
	List<Park> findByAgency(Agency agency); // 기관으로 찾기
	List<Park> findByNameContaining(String name); // 이름 포함 검색
	List<Park> findByAreaBetween(double area1, double area2); // 넓이 범위 이내 검색. RDBMS의 Between과 같은 개념.
	List<Park> findByCultFacilityContains(String[] cultFacilities); // 문화 시설 포함 여부 검색
	List<Park> findByConvFacilityContains(String[] convFacilities); // 편의 시설 포함 여부 검색
	void deleteByManageNo(String manageNo); // 관리번호로 삭제
}

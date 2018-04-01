package net.kang.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.repository.KindRepository;
import net.kang.repository.ParkRepository;

@Service
public class KindService { // 종류 서비스 클래스 생성
	@Autowired KindRepository kindRepository;
	@Autowired ParkRepository parkRepository;

	public List<Kind> findAll(){ // 종류 전체 목록 반환
		return kindRepository.findAll();
	}

	public Optional<Kind> findOne(String id){ // 종류의 _id로 검색한 결과 반환
		return kindRepository.findById(id);
	}

	public List<Park> findOneAndParkFindAll(String id){ // 종류를 찾고 난 후에 공원 목록 반환
		Optional<Kind> kind=kindRepository.findById(id);
		if(!kind.orElse(new Kind()).equals(new Kind())) {
			return parkRepository.findByKind(kind.get());
		}
		return new ArrayList<Park>(); // 해당되는 종류가 없다면 빈 공원 목록 반환
	}

	public Optional<Kind> findByName(String name){ // 종류의 이름으로 반환
		return kindRepository.findByName(name);
	}

	public List<Kind> findByNameContaining(String name){ // 종류의 이름 포함 반환
		return kindRepository.findByNameContaining(name);
	}

	public boolean insert(Kind kind) { // 종류 추가. 종류에 포함이 되어 있지 않으면 종류 추가 및 true 반환. 아닌 경우에는 false를 반환.
		if(kind.getId()==null) {
			kindRepository.insert(kind);
			return true;
		}
		else if(!kindRepository.existsById(kind.getId())) {
			kindRepository.insert(kind);
			return true;
		}else return false;
	}

	public boolean update(Kind kind) { // 종류 수정. 종류가 있으면 수정 작업 진행 및 true 반환. 아닌 경우에 false 반환.
		if(kindRepository.existsById(kind.getId())) {
			kindRepository.save(kind);
			return true;
		}else return false;
	}

	public boolean delete(String id) { // 종류 삭제. 종류가 있으면 삭제 작업 진행 및 true 반환. 아닌 경우에 false 반환.
		if(kindRepository.existsById(id)) {
			kindRepository.deleteById(id);
			return true;
		}else return false;
	}

	public boolean deleteByNameContaining(String name) { // 종류 이름 포함 삭제. 포함 목록이 있다면 삭제 작업 진행 및 true 반환. 아닌 경우에 false 반환.
		if(!kindRepository.findByNameContaining(name).isEmpty()) {
			kindRepository.deleteByNameContaining(name);
			return true;
		}else return false;
	}
}

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
public class KindService {
	@Autowired KindRepository kindRepository;
	@Autowired ParkRepository parkRepository;
	public List<Kind> findAll(){
		return kindRepository.findAll();
	}
	public Optional<Kind> findOne(String id){
		return kindRepository.findById(id);
	}
	public List<Park> findOneWithParkFindAll(String id){
		Optional<Kind> kind=kindRepository.findById(id);
		if(!kind.orElse(new Kind()).equals(new Kind())) {
			return parkRepository.findByKind(kind.get());
		}
		return new ArrayList<Park>();
	}
	public boolean insert(Kind kind) {
		if(!kindRepository.existsById(kind.getId())) {
			kindRepository.insert(kind);
			return true;
		}else return false;
	}
	public boolean update(Kind kind) {
		if(kindRepository.existsById(kind.getId())) {
			kindRepository.save(kind);
			return true;
		}else return false;
	}
	public boolean delete(String id) {
		if(kindRepository.existsById(id)) {
			kindRepository.deleteById(id);
			return true;
		}else return false;
	}
}

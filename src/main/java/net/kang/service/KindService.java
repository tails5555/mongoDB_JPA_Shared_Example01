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
	public Optional<Kind> findOne(String id){
		return kindRepository.findById(id);
	}
	public List<Kind> findAll(){
		return kindRepository.findAll();
	}
	public List<Park> findOneWithParkFindAll(String id){
		Optional<Kind> kind=kindRepository.findById(id);
		if(!kind.orElse(new Kind()).equals(new Kind())) {
			return parkRepository.findByKind(kind.get());
		}
		return new ArrayList<Park>();
	}
}
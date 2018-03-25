package net.kang.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.kang.domain.Agency;
import net.kang.domain.Park;
import net.kang.repository.AgencyRepository;
import net.kang.repository.ParkRepository;

@Service
public class AgencyService {
	@Autowired AgencyRepository agencyRepository;
	@Autowired ParkRepository parkRepository;
	public List<Agency> findAll(){
		return agencyRepository.findAll();
	}
	public Optional<Agency> findOne(String id){
		return agencyRepository.findById(id);
	}
	public List<Park> findOneAndParkFindAll(String id){
		Optional<Agency> agency=agencyRepository.findById(id);
		if(!agency.orElse(new Agency()).equals(new Agency())) {
			return parkRepository.findByAgency(agency.get());
		}
		return new ArrayList<Park>();
	}
}

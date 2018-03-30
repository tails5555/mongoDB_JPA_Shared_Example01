package net.kang.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.kang.domain.Agency;
import net.kang.domain.Office;
import net.kang.repository.AgencyRepository;
import net.kang.repository.OfficeRepository;

@Service
public class OfficeService {
	@Autowired OfficeRepository officeRepository;
	@Autowired AgencyRepository agencyRepository;

	public List<Office> findAll(){
		return officeRepository.findAll();
	}

	public Optional<Office> findOne(String id){
		return officeRepository.findById(id);
	}

	public List<Agency> findOneAndAgencyFindAll(String id){
		Optional<Office> office=officeRepository.findById(id);
		if(!office.orElse(new Office()).equals(new Office())) {
			return agencyRepository.findByOffice(office.get());
		}
		return new ArrayList<Agency>();
	}

	public List<Office> findByNameContaining(String name){
		return officeRepository.findByNameContaining(name);
	}

	public boolean insert(Office office) {
		if(office.getId()==null) {
			officeRepository.insert(office);
			return true;
		}
		else if(!officeRepository.existsById(office.getId())) {
			officeRepository.insert(office);
			return true;
		}else return false;
	}

	public boolean update(Office office) {
		if(officeRepository.existsById(office.getId())) {
			officeRepository.save(office);
			return true;
		}else return false;
	}

	public boolean delete(String id) {
		if(officeRepository.existsById(id)) {
			officeRepository.deleteById(id);
			return true;
		}else return false;
	}

	public boolean deleteByNameContaining(String name) {
		if(!officeRepository.findByNameContaining(name).isEmpty()) {
			officeRepository.deleteByNameContaining(name);
			return true;
		}else return false;
	}
}

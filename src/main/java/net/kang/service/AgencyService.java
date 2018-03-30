package net.kang.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.kang.domain.Agency;
import net.kang.domain.Office;
import net.kang.domain.Park;
import net.kang.model.AgencyForm;
import net.kang.repository.AgencyRepository;
import net.kang.repository.OfficeRepository;
import net.kang.repository.ParkRepository;

@Service
public class AgencyService {
	@Autowired AgencyRepository agencyRepository;
	@Autowired ParkRepository parkRepository;
	@Autowired OfficeRepository officeRepository;

	public List<Agency> findAll(){
		return agencyRepository.findAll();
	}

	public Optional<Agency> findOne(String id){
		return agencyRepository.findById(id);
	}

	public Optional<Agency> findByName(String name){
		return agencyRepository.findByName(name);
	}

	public List<Agency> findByNameContaining(String name){
		return agencyRepository.findByNameContaining(name);
	}

	public List<Park> findOneAndParkFindAll(String id){
		Optional<Agency> agency=agencyRepository.findById(id);
		if(!agency.orElse(new Agency()).equals(new Agency())) {
			return parkRepository.findByAgency(agency.get());
		}
		return new ArrayList<Park>();
	}

	public AgencyForm agencyToForm(Agency agency) {
		AgencyForm agencyForm=new AgencyForm();
		agencyForm.setAgencyId(agency.getId());
		agencyForm.setName(agency.getName());
		agencyForm.setOfficeId(agency.getOffice().getId());
		return agencyForm;
	}

	public Agency formToAgency(AgencyForm agencyForm, Office office) {
		Agency agency=new Agency();
		agency.setId(agencyForm.getAgencyId());
		agency.setName(agencyForm.getName());
		agency.setOffice(office);
		return agency;
	}

	public boolean insert(AgencyForm agencyForm) {
		Agency agency=new Agency();
		Office office=officeRepository.findById(agencyForm.getOfficeId()).orElse(new Office());
		if(!office.equals(new Office())) {
			agency=formToAgency(agencyForm, office);
			agencyRepository.insert(agency);
			return true;
		}else return false;
	}

	public boolean update(AgencyForm agencyForm) {
		Agency agency=new Agency();
		Office office=officeRepository.findById(agencyForm.getOfficeId()).orElse(new Office());
		if(!agencyRepository.existsById(agencyForm.getAgencyId())) {
			return false;
		}else if(!office.equals(new Office())) {
			agency.setId(agencyForm.getAgencyId());
			agency.setName(agencyForm.getName());
			agency.setOffice(office);
			agencyRepository.save(agency);
			return true;
		}else return false;
	}

	public boolean delete(String id) {
		if(agencyRepository.existsById(id)) {
			agencyRepository.deleteById(id);
			return true;
		}else return false;
	}

	public boolean deleteByNameContaining(String name) {
		if(!agencyRepository.findByNameContaining(name).isEmpty()) {
			agencyRepository.deleteByNameContaining(name);
			return true;
		}else return false;
	}
}

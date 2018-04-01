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
public class AgencyService { // 기관 서비스 클래스 생성
	@Autowired AgencyRepository agencyRepository;
	@Autowired ParkRepository parkRepository;
	@Autowired OfficeRepository officeRepository;

	public List<Agency> findAll(){ // 기관 전체 목록 반환
		return agencyRepository.findAll();
	}

	public Optional<Agency> findOne(String id){ // 기관의 _id 값으로 검색
		return agencyRepository.findById(id);
	}

	public Optional<Agency> findByName(String name){ // 기관의 이름으로 검색
		return agencyRepository.findByName(name);
	}

	public List<Agency> findByNameContaining(String name){ // 기관 이름 포함 검색
		return agencyRepository.findByNameContaining(name);
	}

	public List<Park> findOneAndParkFindAll(String id){ // 기관을 찾고 난 후에 공원 목록 반환
		Optional<Agency> agency=agencyRepository.findById(id);
		if(!agency.orElse(new Agency()).equals(new Agency())) {
			return parkRepository.findByAgency(agency.get());
		}
		return new ArrayList<Park>(); // 기관이 없는 경우에는 그냥 빈 공원 목록 반환
	}

	public AgencyForm agencyToForm(Agency agency) { // 기관 정보를 기관 Form으로 반환
		AgencyForm agencyForm=new AgencyForm();
		agencyForm.setAgencyId(agency.getId());
		agencyForm.setName(agency.getName());
		agencyForm.setOfficeId(agency.getOffice().getId());
		return agencyForm;
	}

	public Agency formToAgency(AgencyForm agencyForm, Office office) { // 기관 Form을 기관으로 반환
		Agency agency=new Agency();
		agency.setId(agencyForm.getAgencyId());
		agency.setName(agencyForm.getName());
		agency.setOffice(office);
		return agency;
	}

	public boolean insert(AgencyForm agencyForm) { // 기관 추가. 관계성 성립까지 확인을 하고 난 후에 추가를 함. 추가가 된다면 true, 실패하면 false 반환.
		Agency agency=new Agency();
		Office office=officeRepository.findById(agencyForm.getOfficeId()).orElse(new Office());
		if(!office.equals(new Office())) {
			agency=formToAgency(agencyForm, office);
			agencyRepository.insert(agency);
			return true;
		}else return false;
	}

	public boolean update(AgencyForm agencyForm) { // 기관 수정. 관계성 성립까지 확인하고 난 후에 수정을 함. 수정이 된다면 true, 실패하면 false 반환.
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

	public boolean delete(String id) { // 기관 삭제. 기관이 존재하면 삭제를 진행하고 true를 반환. 없으면 false를 반환하여 삭제 실패.
		if(agencyRepository.existsById(id)) {
			agencyRepository.deleteById(id);
			return true;
		}else return false;
	}

	public boolean deleteByNameContaining(String name) { // 기관 이름 포함으로 삭제. 이름 포함이 되면 삭제 진행 및 true 반환. 아니면 false로 반환.
		if(!agencyRepository.findByNameContaining(name).isEmpty()) {
			agencyRepository.deleteByNameContaining(name);
			return true;
		}else return false;
	}
}

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
public class OfficeService { // 시구청 서비스 클래스 생성
	@Autowired OfficeRepository officeRepository;
	@Autowired AgencyRepository agencyRepository;

	public List<Office> findAll(){ // 시구청 목록 반환
		return officeRepository.findAll();
	}

	public Optional<Office> findOne(String id){ // 시구청 _id를 검색하여 한 시구청 반환
		return officeRepository.findById(id);
	}

	public List<Agency> findOneAndAgencyFindAll(String id){ // 시구청을 통해 해당 기관 검색. 기관이 존재하면 목록 반환.
		Optional<Office> office=officeRepository.findById(id);
		if(!office.orElse(new Office()).equals(new Office())) {
			return agencyRepository.findByOffice(office.get());
		}
		return new ArrayList<Agency>(); // 시구청 검색이 잘 못된다면 빈 기관 목록 반환
	}

	public List<Office> findByNameContaining(String name){ // 시구청 이름 포함 검색
		return officeRepository.findByNameContaining(name);
	}

	public boolean insert(Office office) { // 시구청 추가. 시구청 데이터 존재 여부 확인 이후 추가할 수 있다면 true 반환, 아닌 경우에는 false 반환.
		if(office.getId()==null) {
			officeRepository.insert(office);
			return true;
		}
		else if(!officeRepository.existsById(office.getId())) {
			officeRepository.insert(office);
			return true;
		}else return false;
	}

	public boolean update(Office office) { // 시구청 수정. 시구청 데이터 존재 여부 확인 이후 수정이 가능하면 true 반환, 아닌 경우에는 false를 반환.
		if(officeRepository.existsById(office.getId())) {
			officeRepository.save(office);
			return true;
		}else return false;
	}

	public boolean delete(String id) { // 시구청 삭제. 시구청 데이터 존재 여부 확인 이후 삭제가 가능하면 true를 반환, 아닌 경우에는 false를 반환.
		if(officeRepository.existsById(id)) {
			officeRepository.deleteById(id);
			return true;
		}else return false;
	}

	public boolean deleteByNameContaining(String name) { // 시구청 이름 포함 삭제. 시구청 이름 포함 데이터 목록 확인 결과에 따라서 존재하면 true, 아닌 경우 false를 반환.
		if(!officeRepository.findByNameContaining(name).isEmpty()) {
			officeRepository.deleteByNameContaining(name);
			return true;
		}else return false;
	}
}

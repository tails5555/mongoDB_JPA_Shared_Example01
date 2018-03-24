package net.kang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.kang.domain.Agency;
import net.kang.domain.Office;
import net.kang.repository.AgencyRepository;
import net.kang.repository.OfficeRepository;

@RestController
@CrossOrigin
@RequestMapping("agency")
public class AgencyController {
	@Autowired AgencyRepository agencyRepository;
	@Autowired OfficeRepository officeRepository;
	@RequestMapping("findAll")
	public List<Agency> findAll(){
		return agencyRepository.findAll();
	}
	@RequestMapping("office")
	public List<Office> findAllOffice(){
		return officeRepository.findAll();
	}
	@RequestMapping("save")
	public void save() {
		Agency agency=new Agency();
		agency.setName("꽈꽈");
		agency.setOffice(officeRepository.findById("5ab6005c4421072258a1bd45").get());
		agencyRepository.insert(agency);
	}
}

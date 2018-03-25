package net.kang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.kang.domain.Agency;
import net.kang.domain.Park;
import net.kang.service.AgencyService;

@RestController
@CrossOrigin
@RequestMapping("agency")
public class AgencyController {
	@Autowired AgencyService agencyService;

	@RequestMapping("findAll") // 현재 저장된 모든 기관들을 검색
	public List<Agency> findAll(){
		return agencyService.findAll();
	}

	@RequestMapping("findOne/{id}") // ID를 통한 기관 검색
	public Agency findOne(@PathVariable("id") String id){
		return agencyService.findOne(id).orElse(new Agency());
	}

	@RequestMapping("findOne/parkList/{id}") // 현재 기관이 관리하는 공원 목록을 ID를 통하여 검색
	public List<Park> findOneWithParkFindAll(@PathVariable("id") String id){
		return agencyService.findOneAndParkFindAll(id);
	}
}

package net.kang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.kang.domain.Agency;
import net.kang.domain.Office;
import net.kang.service.OfficeService;

@RestController
@CrossOrigin
@RequestMapping("office")
public class OfficeController {
	@Autowired OfficeService officeService;

	@RequestMapping("findAll") // 성남시에 존재하는 모든 시-구청 검색
	public List<Office> findAll(){
		return officeService.findAll();
	}

	@RequestMapping("findOne/{id}") // ID를 통하여 시-구청 검색
	public Office findOne(@PathVariable("id") String id) {
		return officeService.findOne(id).orElse(new Office());
	}

	@RequestMapping("findOne/agencyList/{id}") // 시-구청 내에 속하는 기관들을 검색
	public List<Agency> findOneAndAgencyList(@PathVariable("id") String id){
		return officeService.findOneAndAgencyFindAll(id);
	}
}

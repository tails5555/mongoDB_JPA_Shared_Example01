package net.kang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.service.KindService;

@RestController
@CrossOrigin
@RequestMapping("kind")
public class KindController {
	@Autowired KindService kindService;

	@RequestMapping("findAll") // 공원의 종류들을 출력
	public List<Kind> findAll(){
		return kindService.findAll();
	}

	@RequestMapping("findOne/{id}") // ID로 공원의 종류 검색
	public Kind findOne(@PathVariable("id") String id) {
		return kindService.findOne(id).orElse(new Kind());
	}

	@RequestMapping("findOne/parkList/{id}") // ID로 공원의 종류에 소속된 공원 목록들을 출력
	public List<Park> findOneAndParkFindAll(@PathVariable("id") String id){
		return kindService.findOneWithParkFindAll(id);
	}
}

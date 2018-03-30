package net.kang.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.kang.domain.Agency;
import net.kang.domain.Park;
import net.kang.model.AgencyForm;
import net.kang.service.AgencyService;

@RestController
@CrossOrigin
@RequestMapping("agency")
public class AgencyController {
	@Autowired AgencyService agencyService;

	@RequestMapping("findAll") // 현재 저장된 모든 기관들을 검색
	public ResponseEntity<List<Agency>> findAll(){
		List<Agency> agencyList=agencyService.findAll();
		if(agencyList.isEmpty()) {
			return new ResponseEntity<List<Agency>>(agencyList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Agency>>(agencyList, HttpStatus.OK);
	}

	@RequestMapping("findOne/{id}") // ID를 통한 기관 검색
	public ResponseEntity<Agency> findOne(@PathVariable("id") String id){
		Optional<Agency> agency=agencyService.findOne(id);
		Agency result=agency.orElse(new Agency());
		if(result.equals(new Agency())) {
			return new ResponseEntity<Agency>(result, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Agency>(result, HttpStatus.OK);
	}

	@RequestMapping("findOne/parkList/{id}") // 현재 기관이 관리하는 공원 목록을 ID를 통하여 검색
	public ResponseEntity<List<Park>> findOneWithParkFindAll(@PathVariable("id") String id){
		List<Park> parkList=agencyService.findOneAndParkFindAll(id);
		if(parkList.isEmpty()) {
			return new ResponseEntity<List<Park>>(parkList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Park>>(parkList, HttpStatus.OK);
	}

	@RequestMapping("findByName/{name}") // 기관 이름으로 검색. Like가 아니라 유일한 이름으로 검색
	public ResponseEntity<Agency> findByName(@PathVariable("name") String name){
		Optional<Agency> agency=agencyService.findByName(name);
		Agency result=agency.orElse(new Agency());
		if(result.equals(new Agency())) {
			return new ResponseEntity<Agency>(result, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Agency>(result, HttpStatus.OK);
	}

	@RequestMapping("findByNameContaining/{name}") // 기관 이름 중 포함으로 검색.
	public ResponseEntity<List<Agency>> findByNameContaining(@PathVariable("name") String name){
		List<Agency> agencyList=agencyService.findByNameContaining(name);
		if(agencyList.isEmpty()) {
			return new ResponseEntity<List<Agency>>(agencyList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Agency>>(agencyList, HttpStatus.OK);
	}

	@RequestMapping(value="insert", method=RequestMethod.POST) // 기관 Document 추가
	public ResponseEntity<String> insert(@RequestBody AgencyForm agencyForm){
		if(agencyService.insert(agencyForm)) {
			return new ResponseEntity<String>("Agency Inserting is Success.", HttpStatus.CREATED);
		}else {
			return new ResponseEntity<String>("Agency Inserting is Failure. Office's ID is Error.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value="update", method=RequestMethod.PUT) // 기관 Document를 수정
	public ResponseEntity<String> update(@RequestBody AgencyForm agencyForm){
		if(agencyService.update(agencyForm)) {
			return new ResponseEntity<String>("Agency Updating is Success.", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Agency Updating is Failure. Office's ID is Error.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value="delete/{id}", method=RequestMethod.DELETE) // 기관 Document를 삭제
	public ResponseEntity<String> delete(@PathVariable("id") String id){
		if(agencyService.delete(id)) {
			return new ResponseEntity<String>("Agency Deleting is Success.", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Agency Updating is Failure. It is Not Existed.", HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value="deleteByNameContaining/{name}", method=RequestMethod.DELETE) // Agency의 name에 포함된 Document 삭제
	public ResponseEntity<String> deleteByNameContaining(@PathVariable("name") String name){
		if(agencyService.deleteByNameContaining(name)) {
			return new ResponseEntity<String>(String.format("Name Containin' %s Deleting is Success.", name), HttpStatus.OK);
		}else {
			return new ResponseEntity<String>(String.format("Name Containin' %s Deleting is Failure.", name), HttpStatus.NOT_FOUND);
		}
	}
}

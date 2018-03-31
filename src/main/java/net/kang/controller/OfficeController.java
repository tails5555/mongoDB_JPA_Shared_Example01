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
import net.kang.domain.Office;
import net.kang.service.OfficeService;

@RestController
@CrossOrigin
@RequestMapping("office")
public class OfficeController {
	@Autowired OfficeService officeService;

	@RequestMapping("findAll") // 성남시에 존재하는 모든 시-구청 검색
	public ResponseEntity<List<Office>> findAll(){
		List<Office> officeList=officeService.findAll();
		if(officeList.isEmpty()) {
			return new ResponseEntity<List<Office>>(officeList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Office>>(officeList, HttpStatus.OK);
	}

	@RequestMapping("findOne/{id}") // ID를 통하여 시-구청 검색
	public ResponseEntity<Office> findOne(@PathVariable("id") String id) {
		Optional<Office> office=officeService.findOne(id);
		Office result=office.orElse(new Office());
		if(result.equals(new Office())) {
			return new ResponseEntity<Office>(result, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Office>(result, HttpStatus.OK);
	}

	@RequestMapping("findOne/agencyList/{id}") // 시-구청 내에 속하는 기관들을 검색
	public ResponseEntity<List<Agency>> findOneAndAgencyList(@PathVariable("id") String id){
		List<Agency> agencyList=officeService.findOneAndAgencyFindAll(id);
		if(agencyList.isEmpty()) {
			return new ResponseEntity<List<Agency>>(agencyList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Agency>>(agencyList, HttpStatus.OK);
	}

	@RequestMapping("findByNameContaining/{name}") // 이름 포함 검색. Like %문자열%과 같다
	public ResponseEntity<List<Office>> findByNameContaining(@PathVariable("name") String name){
		List<Office> officeList=officeService.findByNameContaining(name);
		if(officeList.isEmpty()) {
			return new ResponseEntity<List<Office>>(officeList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Office>>(officeList, HttpStatus.OK);
	}

	@RequestMapping(value="insert", method=RequestMethod.POST) // 성남에 있는 시-구청, 동주민센터 Document 추가
	public ResponseEntity<String> insert(@RequestBody Office office){
		if(officeService.insert(office)) {
			return new ResponseEntity<String>("Office Inserting is Success.", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<String>("Office Inserting is Failure. It is Existed.", HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value="update", method=RequestMethod.PUT) // 성남에 있는 시-구청, 동주민센터 Document 수정
	public ResponseEntity<String> update(@RequestBody Office office){
		if(officeService.update(office)) {
			return new ResponseEntity<String>("Office Updating is Success.", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Office Updating is Failure. It is Not Existed.", HttpStatus.NOT_MODIFIED);
		}
	}

	@RequestMapping(value="delete/{id}", method=RequestMethod.DELETE) // 성남에 있는 시-구청, 동주민센터 Document 삭제
	public ResponseEntity<String> delete(@PathVariable("id") String id){
		if(officeService.delete(id)) {
			return new ResponseEntity<String>("Office Deleting is Success.", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Office Deleting is Failure. It is Not Existed.", HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value="deleteByNameContaining/{name}") // Office의 name에 포함된 Document 삭제
	public ResponseEntity<String> deleteByNameContaining(@PathVariable("name") String name){
		if(officeService.deleteByNameContaining(name)) {
			return new ResponseEntity<String>(String.format("Name Containin' %s Deleting is Success.", name), HttpStatus.OK);
		}else {
			return new ResponseEntity<String>(String.format("Name Containin' %s Deleting is Failure.", name), HttpStatus.NOT_FOUND);
		}
	}
}

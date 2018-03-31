package net.kang.controller;

import java.io.UnsupportedEncodingException;
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

import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.service.KindService;

@RestController
@CrossOrigin
@RequestMapping("kind")
public class KindController {
	@Autowired KindService kindService;

	@RequestMapping("findAll") // 공원의 종류들을 출력
	public ResponseEntity<List<Kind>> findAll(){
		List<Kind> kindList=kindService.findAll();
		if(kindList.isEmpty()){
			return new ResponseEntity<List<Kind>>(kindList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Kind>>(kindList, HttpStatus.OK);
	}

	@RequestMapping("findOne/{id}") // ID로 공원의 종류 검색
	public ResponseEntity<Kind> findOne(@PathVariable("id") String id) {
		Optional<Kind> kind=kindService.findOne(id);
		Kind result=kind.orElse(new Kind());
		if(result.equals(new Kind())) {
			return new ResponseEntity<Kind>(result, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Kind>(result, HttpStatus.OK);
	}

	@RequestMapping("findOne/parkList/{id}") // ID로 공원의 종류에 소속된 공원 목록들을 출력
	public ResponseEntity<List<Park>> findOneAndParkFindAll(@PathVariable("id") String id){
		List<Park> parkList=kindService.findOneAndParkFindAll(id);
		if(parkList.isEmpty()) {
			return new ResponseEntity<List<Park>>(parkList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Park>>(parkList, HttpStatus.OK);
	}

	@RequestMapping("findByName/{name}") // 단 하나의 이름으로 검색
	public ResponseEntity<Kind> findByName(@PathVariable("name") String name){
		Optional<Kind> kind=kindService.findByName(name);
		Kind result=kind.orElse(new Kind());
		if(result.equals(new Kind())) {
			return new ResponseEntity<Kind>(result, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Kind>(result, HttpStatus.OK);
	}

	@RequestMapping("findByNameContaining/{name}") // 이름이 포함된 종류 목록들을 출력
	public ResponseEntity<List<Kind>> findByNameContaining(@PathVariable("name") String name){
		List<Kind> kindList=kindService.findByNameContaining(name);
		if(kindList.isEmpty()) {
			return new ResponseEntity<List<Kind>>(kindList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Kind>>(kindList, HttpStatus.OK);
	}

	@RequestMapping(value="insert", method=RequestMethod.POST) // 종류 Document를 새로 추가
	public ResponseEntity<String> insert(@RequestBody Kind kind) throws UnsupportedEncodingException{
		String result;
		byte[] eucKrToUtf8;
		if(kindService.insert(kind)) {
			result="Kind Inserting is Success.";
			eucKrToUtf8=result.getBytes("UTF-8");
			return new ResponseEntity<String>(new String(eucKrToUtf8, "UTF-8"), HttpStatus.CREATED);
		}else {
			result="Kind Inserting is Failure. It is Existed.";
			eucKrToUtf8=result.getBytes("UTF-8");
			return new ResponseEntity<String>(new String(eucKrToUtf8, "UTF-8"), HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value="update", method=RequestMethod.PUT) // 종류 Document를 수정
	public ResponseEntity<String> update(@RequestBody Kind kind) throws UnsupportedEncodingException{
		String result;
		byte[] eucKrToUtf8;
		if(kindService.update(kind)) {
			result="Kind Updating is Success.";
			eucKrToUtf8=result.getBytes("UTF-8");
			return new ResponseEntity<String>(new String(eucKrToUtf8, "UTF-8"), HttpStatus.OK);
		}else {
			result="Kind Updating is Failure. It Is Not Existed.";
			eucKrToUtf8=result.getBytes("UTF-8");
			return new ResponseEntity<String>(new String(eucKrToUtf8, "UTF-8"), HttpStatus.NOT_MODIFIED);
		}
	}

	@RequestMapping(value="delete/{id}", method=RequestMethod.DELETE) // 종류 Document를 삭제
	public ResponseEntity<String> delete(@PathVariable("id") String id) throws UnsupportedEncodingException{
		String result;
		byte[] eucKrToUtf8;
		if(kindService.delete(id)) {
			result="Kind Deleting is Success.";
			eucKrToUtf8=result.getBytes("UTF-8");
			return new ResponseEntity<String>(new String(eucKrToUtf8, "UTF-8"), HttpStatus.OK);
		}else {
			result="Kind Updating is Failure. It is Not Existed.";
			eucKrToUtf8=result.getBytes("UTF-8");
			return new ResponseEntity<String>(new String(eucKrToUtf8, "UTF-8"), HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value="deleteByNameContaining/{name}", method=RequestMethod.DELETE) // Kind의 name에 포함된 Document 삭제
	public ResponseEntity<String> deleteByNameContaining(@PathVariable("name") String name) throws UnsupportedEncodingException{
		String result;
		byte[] eucKrToUtf8;
		if(kindService.deleteByNameContaining(name)) {
			result=String.format("Name Containin' %s Deleting is Success.", name);
			eucKrToUtf8=result.getBytes("UTF-8");
			return new ResponseEntity<String>(new String(eucKrToUtf8, "UTF-8"), HttpStatus.OK);
		}else {
			result=String.format("Name Containin' %s Deleting is Failure.", name);
			eucKrToUtf8=result.getBytes("UTF-8");
			return new ResponseEntity<String>(new String(eucKrToUtf8, "UTF-8"), HttpStatus.NOT_FOUND);
		}
	}
}

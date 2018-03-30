
package net.kang.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
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
import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.model.ParkForm;
import net.kang.service.ParkService;

@RestController
@CrossOrigin
@RequestMapping("park")
public class ParkController {
	@Autowired ParkService parkService;

	@RequestMapping(value="excelUpload", method=RequestMethod.POST) // 초기에 엑셀로 업로드를 해서 데이터의 변동이 있는지 확인을 하고 난 후에 없으면 추가, 있으면 수정을 하였음. 공공데이터 내부에서는 데이터 수정, 추가를 할 일이 없기 때문에 엑셀 업로드에 대해서만 반영을 하였음.
	public ResponseEntity<String> excelUpload() throws IOException, ParseException {
		parkService.excelUpload();
		return new ResponseEntity<String>("Park Information Upload Complete", HttpStatus.CREATED);
	}

	@RequestMapping("findAll") // 모든 공원 목록들을 조회
	public ResponseEntity<List<Park>> findAll(){
		List<Park> parkList=parkService.findAll();
		if(parkList.isEmpty()) {
			return new ResponseEntity<List<Park>>(parkList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Park>>(parkList, HttpStatus.OK);
	}

	@RequestMapping("findByManageNo/{manageNo}") // 관리 번호를 통한 검색. 관리 번호는 Unique Index로 설정하였음.
	public ResponseEntity<Park> findByManageNo(@PathVariable("manageNo") String manageNo) {
		Optional<Park> park=parkService.findByManageNo(manageNo);
		Park result=park.orElse(new Park());
		if(result.equals(new Park())) {
			return new ResponseEntity<Park>(result, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Park>(result, HttpStatus.OK);
	}

	@RequestMapping("findByNameContaining/{name}") // 이름이 포함된 공원 목록들을 출력
	public ResponseEntity<List<Park>> findByNameContaining(@PathVariable("name") String name){
		List<Park> parkList=parkService.findByNameContaining(name);
		if(parkList.isEmpty()) {
			return new ResponseEntity<List<Park>>(parkList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Park>>(parkList, HttpStatus.OK);
	}

	@RequestMapping("findByConvFacilityContains/{convFacilities}") // 편의시설들을 통한 검색. 편의 시설 목록을 입력하면 그 편의 시설이 존재하는 데이터들만 나옴.
	public ResponseEntity<List<Park>> findByConvFacilityContains(@PathVariable("convFacilities") String[] convFacilities) {
		List<Park> parkList=parkService.findByConvFacilityContains(convFacilities);
		if(parkList.isEmpty()) {
			return new ResponseEntity<List<Park>>(parkList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Park>>(parkList, HttpStatus.OK);
	}

	@RequestMapping("findByCultFacilityContains/{cultFacilities}") // 문화시설들을 통한 검색. 위에서와 마찬가지.
	public ResponseEntity<List<Park>> findByCultFacilityContains(@PathVariable("cultFacilities") String[] cultFacilities){
		List<Park> parkList=parkService.findByConvFacilityContains(cultFacilities);
		if(parkList.isEmpty()) {
			return new ResponseEntity<List<Park>>(parkList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Park>>(parkList, HttpStatus.OK);
	}

	@RequestMapping("findByAreaBetween/{area1}/{area2}") // 공원 넓이에 따른 검색. 넓이를 각각 입력 받아서 그 사이에 있는 데이터들을 출력.
	public ResponseEntity<?> findByAreaBetween(@PathVariable("area1") double area1, @PathVariable("area2") double area2){
		List<Park> parkList=parkService.findByAreaBetween(area1, area2);
		if(area1>area2) {
			return new ResponseEntity<String>("Range Error!!!", HttpStatus.BAD_REQUEST);
		}else if(parkList.isEmpty()) {
			return new ResponseEntity<List<Park>>(parkList, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Park>>(parkList, HttpStatus.OK);
	}

	@RequestMapping("countByKind") // 공원 종류 별 현존하는 공원의 수를 출력.
	public ResponseEntity<Map<Kind, Long>> countByKind(){
		Map<Kind, Long> kindAndCounter=parkService.countByKind();
		if(kindAndCounter.isEmpty()) {
			return new ResponseEntity<Map<Kind, Long>>(kindAndCounter, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Map<Kind, Long>>(kindAndCounter, HttpStatus.OK);
	}

	@RequestMapping("countByAgency") // 공원 종류 별 현존하는 공원의 수를 출력.
	public ResponseEntity<Map<Agency, Long>> countByAgency(){
		Map<Agency, Long> agencyAndCounter=parkService.countByAgency();
		if(agencyAndCounter.isEmpty()) {
			return new ResponseEntity<Map<Agency, Long>>(agencyAndCounter, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Map<Agency, Long>>(agencyAndCounter, HttpStatus.OK);
	}

	@RequestMapping(value="insert", method=RequestMethod.POST) // 기관 Document 추가
	public ResponseEntity<String> insert(@RequestBody ParkForm parkForm){
		if(parkService.insert(parkForm)) {
			return new ResponseEntity<String>("Park Inserting is Success.", HttpStatus.CREATED);
		}else {
			return new ResponseEntity<String>("Park Inserting is Failure. Kind or Agency's ID is Error.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value="update", method=RequestMethod.PUT) // 기관 Document를 수정
	public ResponseEntity<String> update(@RequestBody ParkForm parkForm){
		if(parkService.update(parkForm)) {
			return new ResponseEntity<String>("Park Updating is Success.", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Park Updating is Failure. Kind or Agency's ID is Error.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value="delete/{id}", method=RequestMethod.DELETE) // 기관 Document를 삭제
	public ResponseEntity<String> delete(@PathVariable("id") String id){
		if(parkService.delete(id)) {
			return new ResponseEntity<String>("Park Deleting is Success.", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Park Updating is Failure. It is Not Existed.", HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value="deleteAll", method=RequestMethod.DELETE) // 기관 Document를 삭제
	public ResponseEntity<String> deleteAll(){
		if(parkService.deleteAll()) {
			return new ResponseEntity<String>("Park All Deleting is Success.", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Park All Updating is Failure. It is Not Existed.", HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value="deleteByManageNo/{manageNo}", method=RequestMethod.DELETE) // 기관 Document를 삭제
	public ResponseEntity<String> deleteByManageNo(@PathVariable("manageNo") String manageNo){
		if(parkService.deleteByManageNo(manageNo)) {
			return new ResponseEntity<String>("Park Deleting with ManageNo is Success.", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Park Updating with ManageNo is Failure. It is Not Existed.", HttpStatus.NOT_FOUND);
		}
	}
}

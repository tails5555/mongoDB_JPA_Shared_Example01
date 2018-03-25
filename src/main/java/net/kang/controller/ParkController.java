package net.kang.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.service.ParkService;

@RestController
@CrossOrigin
@RequestMapping("park")
public class ParkController {
	@Autowired ParkService parkService;

	@RequestMapping(value="excelUpload", method=RequestMethod.POST) // 초기에 엑셀로 업로드를 해서 데이터의 변동이 있는지 확인을 하고 난 후에 없으면 추가, 있으면 수정을 하였음. 공공데이터 내부에서는 데이터 수정, 추가를 할 일이 없기 때문에 엑셀 업로드에 대해서만 반영을 하였음.
	public String excelUpload() throws IOException, ParseException {
		parkService.excelUpload();
		return "Park Information Upload Complete";
	}

	@RequestMapping("findAll") // 모든 공원 목록들을 조회
	public List<Park> findAll(){
		return parkService.findAll();
	}

	@RequestMapping("findByManageNo/{manageNo}") // 관리 번호를 통한 검색. 관리 번호는 Unique Index로 설정하였음.
	public Park findByManageNo(@PathVariable("manageNo") String manageNo) {
		return parkService.findByManageNo(manageNo).orElse(new Park());
	}

	@RequestMapping("findByConvFacilityContains/{convFacilities}") // 편의시설들을 통한 검색. 편의 시설 목록을 입력하면 그 편의 시설이 존재하는 데이터들만 나옴.
	public List<Park> findByConvFacilityContains(@PathVariable("convFacilities") String[] convFacilities) {
		return parkService.findByConvFacilityContains(convFacilities);
	}

	@RequestMapping("findByCultFacilityContains/{cultFacilities}") // 문화시설들을 통한 검색. 위에서와 마찬가지.
	public List<Park> findByCultFacilityContains(@PathVariable("cultFacilities") String[] cultFacilities){
		return parkService.findByCultFacilityContains(cultFacilities);
	}

	@RequestMapping("findByAreaBetween/{area1}/{area2}") // 공원 넓이에 따른 검색. 넓이를 각각 입력 받아서 그 사이에 있는 데이터들을 출력.
	public List<Park> findByAreaBetween(@PathVariable("area1") double area1, @PathVariable("area2") double area2){
		return parkService.findByAreaBetween(area1, area2);
	}

	@RequestMapping("findByNameContaining/{name}") // 공원 이름 중앙으로 검색. SQL의 Like 함수와 같은 맥락.
	public List<Park> findByNameContaining(@PathVariable("name") String name){
		return parkService.findByNameContaining(name);
	}

	@RequestMapping("countByKind") // 공원 종류 별 현존하는 공원의 수를 출력.
	public Map<Kind, Long> countByKind(){
		return parkService.countByKind();
	}

	@RequestMapping(value="deleteAll", method=RequestMethod.DELETE) // 모든 데이터를 삭제. 이는 공공 데이터에 들어간 데이터들을 삭제하고 새로운 데이터들을 추가할 때 쓰게 된다.
	public String deleteAll() {
		parkService.deleteAll();
		return "All Park Delete Complete";
	}
}

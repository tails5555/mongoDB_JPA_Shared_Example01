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
	@RequestMapping(value="excelUpload", method=RequestMethod.POST)
	public String excelUpload() throws IOException, ParseException {
		parkService.excelUpload();
		return "Park Information Upload Complete";
	}
	@RequestMapping("findAll")
	public List<Park> findAll(){
		return parkService.findAll();
	}
	@RequestMapping("findByManageNo/{manageNo}")
	public Park findByManageNo(@PathVariable("manageNo") String manageNo) {
		return parkService.findByManageNo(manageNo).orElse(new Park());
	}
	@RequestMapping("findByConvFacilityContains/{convFacilities}")
	public List<Park> findByConvFacilityContains(@PathVariable("convFacilities") String[] convFacilities) {
		return parkService.findByConvFacilityContains(convFacilities);
	}
	@RequestMapping("findByCultFacilityContains/{cultFacilities}")
	public List<Park> findByCultFacilityContains(@PathVariable("cultFacilities") String[] cultFacilities){
		return parkService.findByCultFacilityContains(cultFacilities);
	}
	@RequestMapping("findByAreaBetween/{area1}/{area2}")
	public List<Park> findByAreaBetween(@PathVariable("area1") double area1, @PathVariable("area2") double area2){
		return parkService.findByAreaBetween(area1, area2);
	}
	@RequestMapping("findByNameContaining/{name}")
	public List<Park> findByNameContaining(@PathVariable("name") String name){
		return parkService.findByNameContaining(name);
	}
	@RequestMapping("countByKind")
	public Map<Kind, Long> countByKind(){
		return parkService.countByKind();
	}
	@RequestMapping(value="deleteAll", method=RequestMethod.DELETE)
	public String deleteAll() {
		parkService.deleteAll();
		return "All Park Delete Complete";
	}
}

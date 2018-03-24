package net.kang.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}

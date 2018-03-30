package net.kang.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.kang.domain.Agency;
import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.model.ParkForm;
import net.kang.model.Position;
import net.kang.repository.AgencyRepository;
import net.kang.repository.KindRepository;
import net.kang.repository.ParkRepository;

@Service
public class ParkService {
	@Autowired AgencyRepository agencyRepository;
	@Autowired KindRepository kindRepository;
	@Autowired ParkRepository parkRepository;

	public List<String> facilityList(String context){
		List<String> tmpFacilityList=new ArrayList<String>();
		if(context.split(", ").length>0) {
			String[] tmpFacility=context.split(", ");
			for(String s : tmpFacility) {
				tmpFacilityList.add(s);
			}
		}else {
			tmpFacilityList.add(context);
		}
		return tmpFacilityList;
	}

	public void excelUpload() throws IOException, ParseException {
		FileInputStream fis=new FileInputStream("C:\\경기도_성남시_도시공원정보.xls");
		HSSFWorkbook workbook=new HSSFWorkbook(fis);
		HSSFSheet sheet=workbook.getSheetAt(0);
		int rows=sheet.getPhysicalNumberOfRows();
		for(int k=1;k<rows;k++) {
			Park park=new Park();
			HSSFRow row=sheet.getRow(k);
			HSSFCell cell=row.getCell(0); // 대부분 공공데이터에서 제공하는 소스 코드는 아쉽게도 XLS 파일로 제공하기 때문에 HSSF를 이용해서 할 수 밖에 없다.
			park.setManageNo(cell.getRichStringCellValue().toString());

			cell=row.getCell(1);
			park.setName(cell.getRichStringCellValue().toString());

			cell=row.getCell(2);
			park.setKind(kindRepository.findByName(cell.getRichStringCellValue().toString()).get());

			cell=row.getCell(3);
			park.setOldAddress(cell.getRichStringCellValue().toString());

			cell=row.getCell(4);
			if(cell!=null) {
				park.setNewAddress(cell.getRichStringCellValue().toString());
			}

			double posX=0.0;
			double posY=0.0;
			cell=row.getCell(5);

			if(!cell.getRichStringCellValue().toString().equals(""))
				posX=Double.parseDouble(cell.getRichStringCellValue().toString());

			cell=row.getCell(6);
			if(!cell.getRichStringCellValue().toString().equals(""))
				posY=Double.parseDouble(cell.getRichStringCellValue().toString());

			park.setPosition(new Position(posX, posY));

			cell=row.getCell(7);
			park.setArea(Double.parseDouble(cell.getRichStringCellValue().toString()));

			cell=row.getCell(8);
			if(cell!=null) {
				park.setJymFacility(facilityList(cell.getRichStringCellValue().toString()));
			}

			cell=row.getCell(9);
			if(cell!=null) {
				park.setPlayFacility(facilityList(cell.getRichStringCellValue().toString()));
			}

			cell=row.getCell(10);
			if(cell!=null) {
				park.setConvFacility(facilityList(cell.getRichStringCellValue().toString()));
			}

			cell=row.getCell(11);
			if(cell!=null) {
				park.setCultFacility(facilityList(cell.getRichStringCellValue().toString()));
			}

			cell=row.getCell(12);
			if(cell!=null) {
				park.setAnotFacility(facilityList(cell.getRichStringCellValue().toString()));
			}

			cell=row.getCell(13);
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			park.setDesignateDate(format.parse(cell.getRichStringCellValue().toString()));

			cell=row.getCell(14);
			park.setAgency(agencyRepository.findByName(cell.getRichStringCellValue().toString()).get());

			cell=row.getCell(15);
			park.setCallPhone(cell.getRichStringCellValue().toString());

			if(parkRepository.findByManageNo(park.getManageNo()).orElse(new Park()).equals(new Park())) {
				parkRepository.insert(park);
			}else {
				Park tmpPark=parkRepository.findByManageNo(park.getManageNo()).get();
				park.setId(tmpPark.getId());
				parkRepository.save(park);
			}
		}
	}

	public List<Park> findAll(){
		return parkRepository.findAll();
	}

	public Optional<Park> findOne(String id){
		return parkRepository.findById(id);
	}

	public Optional<Park> findByManageNo(String manageNo){
		return parkRepository.findByManageNo(manageNo);
	}

	public List<Park> findByConvFacilityContains(String[] convFacilities){
		return parkRepository.findByConvFacilityContains(convFacilities);
	}

	public List<Park> findByCultFacilityContains(String[] cultFacilities){
		return parkRepository.findByCultFacilityContains(cultFacilities);
	}

	public List<Park> findByNameContaining(String name){
		return parkRepository.findByNameContaining(name);
	}

	public List<Park> findByAreaBetween(double area1, double area2){
		return parkRepository.findByAreaBetween(area1, area2);
	}

	public Map<Kind, Long> countByKind(){
		Map<Kind, Long> countMap=new HashMap<Kind, Long>();
		for(Kind k : kindRepository.findAll()) {
			countMap.put(k, parkRepository.countByKind(k));
		}
		return countMap;
	}

	public Map<Agency, Long> countByAgency(){
		Map<Agency, Long> countMap=new HashMap<Agency, Long>();
		for(Agency a : agencyRepository.findAll()) {
			countMap.put(a, parkRepository.countByAgency(a));
		}
		return countMap;
	}

	public Park formToPark(ParkForm parkForm, Agency agency, Kind kind) {
		Park park=new Park();
		park.setName(parkForm.getName());
		park.setManageNo(parkForm.getManageNo());
		park.setKind(kind);
		park.setOldAddress(parkForm.getOldAddress());
		park.setNewAddress(parkForm.getNewAddress());
		park.setPosition(new Position(parkForm.getPosX(), parkForm.getPosY()));
		park.setArea(parkForm.getArea());
		park.setJymFacility(parkForm.getJymFacility());
		park.setPlayFacility(parkForm.getPlayFacility());
		park.setConvFacility(parkForm.getConvFacility());
		park.setCultFacility(parkForm.getCultFacility());
		park.setAnotFacility(parkForm.getAnotFacility());
		park.setDesignateDate(parkForm.getDesignateDate());
		park.setAgency(agency);
		park.setCallPhone(parkForm.getCallPhone());
		return park;
	}

	public ParkForm parkToForm(Park park) {
		ParkForm parkForm=new ParkForm();
		parkForm.setParkId(park.getId());;
		parkForm.setName(park.getName());
		parkForm.setManageNo(park.getManageNo());
		parkForm.setKindId(park.getKind().getId());
		parkForm.setOldAddress(park.getOldAddress());
		parkForm.setNewAddress(park.getNewAddress());
		parkForm.setPosX(park.getPosition().getPosX());
		parkForm.setPosX(park.getPosition().getPosY());
		parkForm.setArea(park.getArea());
		parkForm.setJymFacility(park.getJymFacility());
		parkForm.setPlayFacility(park.getPlayFacility());
		parkForm.setConvFacility(park.getConvFacility());
		parkForm.setCultFacility(park.getCultFacility());
		parkForm.setAnotFacility(park.getAnotFacility());
		parkForm.setDesignateDate(park.getDesignateDate());
		parkForm.setAgencyId(park.getAgency().getId());
		parkForm.setCallPhone(park.getCallPhone());
		return parkForm;
	}

	public boolean insert(ParkForm parkForm) {
		Kind kind=kindRepository.findById(parkForm.getParkId()).orElse(new Kind());
		Agency agency=agencyRepository.findById(parkForm.getAgencyId()).orElse(new Agency());
		if(!kind.equals(new Kind()) && !agency.equals(new Agency())) {
			Park park=formToPark(parkForm, agency, kind);
			parkRepository.insert(park);
			return true;
		}
		else return false;
	}

	public boolean update(ParkForm parkForm) {
		Kind kind=kindRepository.findById(parkForm.getParkId()).orElse(new Kind());
		Agency agency=agencyRepository.findById(parkForm.getAgencyId()).orElse(new Agency());
		if(!parkRepository.existsById(parkForm.getParkId())) {
			return false;
		}else if(!kind.equals(new Kind()) && !agency.equals(new Agency())) {
			Park park=formToPark(parkForm, agency, kind);
			parkRepository.save(park);
			return true;
		}else return false;
	}

	public boolean delete(String id) {
		if(parkRepository.existsById(id)) {
			parkRepository.deleteById(id);
			return true;
		}else return false;
	}

	public boolean deleteAll() {
		if(!parkRepository.findAll().isEmpty()) {
			parkRepository.deleteAll();
			return true;
		}else return false;
	}

	public boolean deleteByManageNo(String manageNo) {
		if(!parkRepository.findByManageNo(manageNo).orElse(new Park()).equals(new Park())) {
			parkRepository.deleteByManageNo(manageNo);
			return true;
		}else return false;
	}
}

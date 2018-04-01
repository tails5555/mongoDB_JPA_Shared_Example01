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
public class ParkService { // 공원 서비스 클래스 생성
	@Autowired AgencyRepository agencyRepository;
	@Autowired KindRepository kindRepository;
	@Autowired ParkRepository parkRepository;

	public List<String> facilityList(String context){ // 시설 목록 추가하기
		List<String> tmpFacilityList=new ArrayList<String>();
		if(context.split(", ").length>0) {
			String[] tmpFacility=context.split(", "); // 시설 목록은 쉼표로 구분되어 있어서 이를 분해하고 난 후에 List<String>으로 반환.
			for(String s : tmpFacility) {
				tmpFacilityList.add(s);
			}
		}else {
			tmpFacilityList.add(context);
		}
		return tmpFacilityList;
	}

	public void excelUpload() throws IOException, ParseException { // 성남시 도시공원정보에 대해서 엑셀 파일을 직접 받아서 MongoDB에 저장.
		FileInputStream fis=new FileInputStream("C:\\경기도_성남시_도시공원정보.xls");
		HSSFWorkbook workbook=new HSSFWorkbook(fis);
		HSSFSheet sheet=workbook.getSheetAt(0);
		int rows=sheet.getPhysicalNumberOfRows();
		for(int k=1;k<rows;k++) {
			Park park=new Park();
			HSSFRow row=sheet.getRow(k);
			HSSFCell cell=row.getCell(0); // 대부분 공공데이터에서 제공하는 소스 코드는 아쉽게도 XLS 파일로 제공하기 때문에 HSSF를 이용해서 할 수 밖에 없다.
			park.setManageNo(cell.getRichStringCellValue().toString()); // 관리 번호 저장

			cell=row.getCell(1);
			park.setName(cell.getRichStringCellValue().toString()); // 공원 이름 저장

			cell=row.getCell(2);
			park.setKind(kindRepository.findByName(cell.getRichStringCellValue().toString()).get()); // Kind 검색 이후 저장

			cell=row.getCell(3);
			park.setOldAddress(cell.getRichStringCellValue().toString()); // 지번 주소 저장

			cell=row.getCell(4);
			if(cell!=null) {
				park.setNewAddress(cell.getRichStringCellValue().toString()); // 도로명 주소 저장.
			}

			double posX=0.0;
			double posY=0.0; // 위도, 경도 저장 데이터를 위한 변수.
			cell=row.getCell(5);

			if(!cell.getRichStringCellValue().toString().equals(""))
				posX=Double.parseDouble(cell.getRichStringCellValue().toString()); // 엑셀에 있는 숫자들은 앞에 '를 붙어서 문자열로 해 둬서 이에 대해 String으로 받고 파싱을 해 줘야 한다.

			cell=row.getCell(6);
			if(!cell.getRichStringCellValue().toString().equals(""))
				posY=Double.parseDouble(cell.getRichStringCellValue().toString());

			park.setPosition(new Position(posX, posY)); // 위도, 경도 저장

			cell=row.getCell(7);
			park.setArea(Double.parseDouble(cell.getRichStringCellValue().toString())); // 면적 저장. 이도 마찬가지로 String으로 받아서 파싱을 해야 한다.

			cell=row.getCell(8);
			if(cell!=null) {
				park.setJymFacility(facilityList(cell.getRichStringCellValue().toString())); // 체육시설 목록 가져와서 List로 저장
			}

			cell=row.getCell(9);
			if(cell!=null) {
				park.setPlayFacility(facilityList(cell.getRichStringCellValue().toString())); // 유희시설 목록 가져와서 List로 저장
			}

			cell=row.getCell(10);
			if(cell!=null) {
				park.setConvFacility(facilityList(cell.getRichStringCellValue().toString())); // 편의시설 목록 가져와서 List로 저장
			}

			cell=row.getCell(11);
			if(cell!=null) {
				park.setCultFacility(facilityList(cell.getRichStringCellValue().toString())); // 문화시설 목록 가져와서 List로 저장
			}

			cell=row.getCell(12);
			if(cell!=null) {
				park.setAnotFacility(facilityList(cell.getRichStringCellValue().toString())); // 기타시설 목록 가져와서 List로 저장
			}

			cell=row.getCell(13);
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			park.setDesignateDate(format.parse(cell.getRichStringCellValue().toString())); // 엑셀에 쓰인 날짜에 대해서 SimpleDateFormat를 이용해서 파싱을 하고 난 후에 지정고시일을 저장

			cell=row.getCell(14);
			park.setAgency(agencyRepository.findByName(cell.getRichStringCellValue().toString()).get()); // 기관에 대해서 검색을 진행한 이후에 저장

			cell=row.getCell(15);
			park.setCallPhone(cell.getRichStringCellValue().toString()); // 공원 연락처 저장

			if(parkRepository.findByManageNo(park.getManageNo()).orElse(new Park()).equals(new Park())) {
				parkRepository.insert(park); // 관리 번호로 검색한 결과가 없는 경우에는 새로 추가를 하고
			}else {
				Park tmpPark=parkRepository.findByManageNo(park.getManageNo()).get();
				park.setId(tmpPark.getId());
				parkRepository.save(park); // 관리 번호로 검색한 결과가 있으면 수정을 해 주도록 한다.
			}
		}
	}

	public List<Park> findAll(){ // 공원 목록 반환
		return parkRepository.findAll();
	}

	public Optional<Park> findOne(String id){ // 공원을 _id로 검색
		return parkRepository.findById(id);
	}

	public Optional<Park> findByManageNo(String manageNo){ // 공원 관리 번호로 검색
		return parkRepository.findByManageNo(manageNo);
	}

	public List<Park> findByConvFacilityContains(String[] convFacilities){ // 편의시설 목록 포함된 공원 목록을 검색
		return parkRepository.findByConvFacilityContains(convFacilities);
	}

	public List<Park> findByCultFacilityContains(String[] cultFacilities){ // 문화시설 목록 포함된 공원 목록을 검색
		return parkRepository.findByCultFacilityContains(cultFacilities);
	}

	public List<Park> findByNameContaining(String name){ // 공원 이름 포함 검색
		return parkRepository.findByNameContaining(name);
	}

	public List<Park> findByAreaBetween(double area1, double area2){ // 면적 범위 적용해 검색
		return parkRepository.findByAreaBetween(area1, area2);
	}

	public Map<Kind, Long> countByKind(){ // 공원 종류 별로 카운팅을 하여 Map으로 반환
		Map<Kind, Long> countMap=new HashMap<Kind, Long>();
		for(Kind k : kindRepository.findAll()) {
			countMap.put(k, parkRepository.countByKind(k));
		}
		return countMap;
	}

	public Map<Agency, Long> countByAgency(){ // 기관 별로 카운팅을 하여 Map으로 반환
		Map<Agency, Long> countMap=new HashMap<Agency, Long>();
		for(Agency a : agencyRepository.findAll()) {
			countMap.put(a, parkRepository.countByAgency(a));
		}
		return countMap;
	}

	public Park formToPark(ParkForm parkForm, Agency agency, Kind kind) { // 공원 Form을 공원 객체로 반환
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

	public ParkForm parkToForm(Park park) { // 공원 객체를 공원 Form으로 반환
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

	public boolean insert(ParkForm parkForm) { // 공원 추가. 공원 추가에 필요한 관계형 데이터(종류, 기관)에 대해 확인 절차 이후 추가. 추가 실패 시 false를 반환.
		Kind kind=kindRepository.findById(parkForm.getParkId()).orElse(new Kind());
		Agency agency=agencyRepository.findById(parkForm.getAgencyId()).orElse(new Agency());
		if(!kind.equals(new Kind()) && !agency.equals(new Agency())) {
			Park park=formToPark(parkForm, agency, kind);
			parkRepository.insert(park);
			return true;
		}
		else return false;
	}

	public boolean update(ParkForm parkForm) { // 공원 수정. 공원 추가에 필요한 관계형 데이터(종류, 기관)에 대해 확인 절차 이후 수정. 추가 실패 시 false를 반환.
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

	public boolean delete(String id) { // 공원 삭제. 공원 존재 여부를 따지고 난 후에 삭제 진행. 삭제 성공 시 true를 반환.
		if(parkRepository.existsById(id)) {
			parkRepository.deleteById(id);
			return true;
		}else return false;
	}

	public boolean deleteAll() { // 공원에 데이터가 어떠한 것들이 존재를 할지언정 모두 삭제를 시킨다. 삭제 성공 시 true를 반환.
		if(!parkRepository.findAll().isEmpty()) {
			parkRepository.deleteAll();
			return true;
		}else return false;
	}

	public boolean deleteByManageNo(String manageNo) { // 관리 번호를 이용해서 삭제를 진행한다. 삭제 성공 시 true를 반환.
		if(!parkRepository.findByManageNo(manageNo).orElse(new Park()).equals(new Park())) {
			parkRepository.deleteByManageNo(manageNo);
			return true;
		}else return false;
	}
}

package net.kang.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.kang.domain.Park;
import net.kang.model.Position;
import net.kang.repository.AgencyRepository;
import net.kang.repository.KindRepository;
import net.kang.repository.ParkRepository;

@Service
public class ParkService {
	@Autowired AgencyRepository agencyRepository;
	@Autowired KindRepository kindRepository;
	@Autowired ParkRepository parkRepository;
	public List<Park> findAll(){
		return parkRepository.findAll();
	}
	public Optional<Park> findByManageNo(String manageNo){
		return parkRepository.findByManageNo(manageNo);
	}
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
			HSSFCell cell=row.getCell(0);
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
}

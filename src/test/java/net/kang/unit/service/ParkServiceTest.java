package net.kang.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import net.kang.config.JUnitConfig;
import net.kang.config.MongoConfig;
import net.kang.domain.Agency;
import net.kang.domain.Kind;
import net.kang.domain.Office;
import net.kang.domain.Park;
import net.kang.model.ParkForm;
import net.kang.model.Position;
import net.kang.repository.AgencyRepository;
import net.kang.repository.KindRepository;
import net.kang.repository.ParkRepository;
import net.kang.service.ParkService;
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class ParkServiceTest {
	static final int PARK_QTY=10;
	static final int AGENCY_QTY=2;
	static final int KIND_QTY=2;
	static final int FACILITY_QTY=2;
	static Random random=new Random();
	MockMvc mockMvc;
	@Mock ParkRepository parkRepository;
	@Mock KindRepository kindRepository;
	@Mock AgencyRepository agencyRepository;
	@InjectMocks ParkService parkService;

	public List<String> facilityList(String facility){
		List<String> facilityList=new ArrayList<String>();
		for(int k=0;k<FACILITY_QTY;k++) {
			facilityList.add(String.format("%s%02d", facility, k));
		}
		return facilityList;
	}

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(parkService).build();
	}

	public List<Kind> kindList(){
		List<Kind> tmpKindList=new ArrayList<Kind>();
		for(int k=0;k<KIND_QTY;k++) {
			Kind kind=new Kind();
			kind.setId(String.format("%d", k+1));
			kind.setName(String.format("종류%02d", k));
			tmpKindList.add(kind);
		}
		return tmpKindList;
	}

	public List<Agency> agencyList(){
		List<Agency> tmpAgencyList=new ArrayList<Agency>();
		Office office=new Office();
		office.setId("1");
		office.setName("시구청01");
		office.setAddress("주소01");
		office.setZipCode("우편번호01");
		office.setHomepage("홈페이지01");
		for(int k=0;k<AGENCY_QTY;k++) {
			Agency agency=new Agency();
			agency.setId(String.format("%d", k+1));
			agency.setName(String.format("기관%02d", k));
			agency.setOffice(office);
			tmpAgencyList.add(agency);
		}
		return tmpAgencyList;
	}

	public List<Park> parkList(){
		List<Park> tmpParkList=new ArrayList<Park>();
		List<Kind> tmpKindList=kindList();
		Agency agency=new Agency();
		agency.setId("1");
		agency.setName("기관01");
		for(int k=0;k<PARK_QTY;k++) {
			Park park=new Park();
			park.setId(String.format("%d", k+1));
			park.setName(String.format("공원%02d", k));
			park.setOldAddress(String.format("지번주소%02d", k));
			park.setNewAddress(String.format("도로명주소%02d", k));
			park.setArea(100.0+random.nextDouble()*100.0);
			park.setManageNo(String.format("00000-000%02d", k+1));
			park.setPosition(new Position(0.1, 0.1));
			park.setCallPhone(String.format("전화번호%02d", k));
			park.setDesignateDate(new Date());
			park.setKind(tmpKindList.get(k%tmpKindList.size()));
			park.setAgency(agency);
			park.setCultFacility(facilityList("문화시설"));
			park.setConvFacility(facilityList("편의시설"));
			tmpParkList.add(park);
		}
		return tmpParkList;
	}

	@Test
	public void findAllTest() {
		List<Park> tmpResult=parkList();
		when(parkRepository.findAll()).thenReturn(tmpResult);
		List<Park> findAllResult=parkService.findAll();
		assertEquals(tmpResult, findAllResult);
	}

	public Park findOnePark() {
		Park park=new Park();
		Kind kind=new Kind();
		kind.setId("1");
		kind.setName("종류01");
		Agency agency=new Agency();
		agency.setId("1");
		agency.setName("기관01");
		park.setId("1");
		park.setName(String.format("공원%02d", 1));
		park.setOldAddress(String.format("지번주소%02d", 1));
		park.setNewAddress(String.format("도로명주소%02d", 1));
		park.setArea(100.0+random.nextDouble()*100.0);
		park.setManageNo(String.format("00000-000%02d", 1));
		park.setPosition(new Position(0.1, 0.1));
		park.setCallPhone(String.format("전화번호%02d", 1));
		park.setDesignateDate(new Date());
		park.setKind(kind);
		park.setAgency(agency);
		park.setCultFacility(facilityList("문화시설"));
		park.setConvFacility(facilityList("편의시설"));
		return park;
	}

	@Test
	public void findOneTest() {
		Park tmpResult=findOnePark();
		when(parkRepository.findById("1")).thenReturn(Optional.of(tmpResult));
		Optional<Park> findOneResult=parkService.findOne("1");
		assertEquals(tmpResult, findOneResult.get());
	}

	@Test
	public void findByManageNoTest() {
		Park tmpResult=findOnePark();
		when(parkRepository.findByManageNo("00000-00001")).thenReturn(Optional.of(tmpResult));
		Optional<Park> findByManageNoResult=parkService.findByManageNo("00000-00001");
		assertEquals(tmpResult, findByManageNoResult.get());
	}

	@Test
	public void findByConvFacilityContainsTest() {
		List<Park> tmpResult=parkList();
		when(parkRepository.findByConvFacilityContains(new String[] {"편의시설00", "편의시설01"})).thenReturn(tmpResult);
		List<Park> findByConvFacilityContainsResult=parkService.findByConvFacilityContains(new String[] {"편의시설00", "편의시설01"});
		assertEquals(tmpResult, findByConvFacilityContainsResult);
	}

	@Test
	public void findByCultFacilityContainsTest() {
		List<Park> tmpResult=parkList();
		when(parkRepository.findByCultFacilityContains(new String[] {"문화시설00", "문화시설01"})).thenReturn(tmpResult);
		List<Park> findByCultFacilityContainsResult=parkService.findByCultFacilityContains(new String[] {"문화시설00", "문화시설01"});
		assertEquals(tmpResult, findByCultFacilityContainsResult);
	}

	@Test
	public void findByNameContainingTest() {
		List<Park> tmpResult=parkList();
		when(parkRepository.findByNameContaining("공원")).thenReturn(tmpResult);
		List<Park> findByNameContainingResult=parkService.findByNameContaining("공원");
		assertEquals(tmpResult, findByNameContainingResult);
	}

	@Test
	public void findByAreaBetweenTest() {
		List<Park> tmpResult=parkList();
		when(parkRepository.findByAreaBetween(100.0, 200.0)).thenReturn(tmpResult);
		List<Park> findByAreaBetweenResult=parkService.findByAreaBetween(100.0, 200.0);
		assertEquals(tmpResult, findByAreaBetweenResult);
	}

	@Test
	public void countByKindTest() {
		List<Kind> tmpKindList=kindList();
		when(kindRepository.findAll()).thenReturn(kindList());
		when(parkRepository.countByKind(tmpKindList.get(0))).thenReturn((long) 5);
		when(parkRepository.countByKind(tmpKindList.get(1))).thenReturn((long) 5);
		Map<Kind, Long> countByKindResult=parkService.countByKind();
		long tmpCount=0;
		for(Kind k : tmpKindList) {
			tmpCount+=countByKindResult.get(k);
		}
		assertEquals(tmpCount, PARK_QTY);
	}

	@Test
	public void countByAgencyTest() {
		List<Agency> tmpAgencyList=agencyList();
		when(agencyRepository.findAll()).thenReturn(agencyList());
		when(parkRepository.countByAgency(tmpAgencyList.get(0))).thenReturn((long) 3);
		when(parkRepository.countByAgency(tmpAgencyList.get(1))).thenReturn((long) 7);
		Map<Agency, Long> countByAgencyResult=parkService.countByAgency();
		long tmpCount=0;
		for(Agency a : tmpAgencyList) {
			tmpCount+=countByAgencyResult.get(a);
		}
		assertEquals(tmpCount, PARK_QTY);
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

	@Test
	public void insertSuccessTest() {
		Park park=findOnePark();
		ParkForm parkForm=parkToForm(park);
		Kind kind=new Kind();
		kind.setId("1");
		kind.setName("종류01");
		Agency agency=new Agency();
		agency.setId("1");
		agency.setName("기관01");
		when(kindRepository.findById("1")).thenReturn(Optional.of(kind));
		when(agencyRepository.findById("1")).thenReturn(Optional.of(agency));
		Park insertAfterPark=formToPark(parkForm, agency, kind);
		when(parkRepository.insert(insertAfterPark)).thenReturn(insertAfterPark);
		assertTrue(parkService.insert(parkForm));
	}

	@Test
	public void insertFailureTest() {
		Park park=findOnePark();
		ParkForm parkForm=parkToForm(park);
		when(kindRepository.findById("1")).thenReturn(Optional.of(new Kind()));
		when(agencyRepository.findById("1")).thenReturn(Optional.of(new Agency()));
		assertFalse(parkService.insert(parkForm));
	}

	@Test
	public void updateSuccessTest() {
		Park park=findOnePark();
		ParkForm parkForm=parkToForm(park);
		double randArea=100.0+random.nextDouble()*100.0;
		Kind kind=new Kind();
		kind.setId("1");
		kind.setName("종류01");
		Agency agency=new Agency();
		agency.setId("1");
		agency.setName("기관01");
		when(kindRepository.findById("1")).thenReturn(Optional.of(kind));
		when(agencyRepository.findById("1")).thenReturn(Optional.of(agency));
		when(parkRepository.existsById(parkForm.getParkId())).thenReturn(true);
		parkForm.setName("공원TEMP");
		parkForm.setOldAddress("지번주소TEMP");
		parkForm.setNewAddress("도로명주소TEMP");
		parkForm.setArea(randArea);
		Park saveAfterPark=formToPark(parkForm, agency, kind);
		when(parkRepository.save(saveAfterPark)).thenReturn(saveAfterPark);
		assertTrue(parkService.update(parkForm));
	}

	@Test
	public void updateFailureTest() {
		Park park=findOnePark();
		ParkForm parkForm=parkToForm(park);
		when(parkRepository.existsById(parkForm.getParkId())).thenReturn(false);
		when(kindRepository.findById("1")).thenReturn(Optional.of(new Kind()));
		when(agencyRepository.findById("1")).thenReturn(Optional.of(new Agency()));
		assertFalse(parkService.update(parkForm));
	}

	@Test
	public void deleteSuccessTest() {
		Park park=findOnePark();
		when(parkRepository.existsById(park.getId())).thenReturn(true);
		doNothing().when(parkRepository).deleteById(park.getId());
		assertTrue(parkService.delete(park.getId()));
	}

	@Test
	public void deleteFailureTest() {
		Park park=findOnePark();
		when(parkRepository.existsById(park.getId())).thenReturn(false);
		assertFalse(parkService.delete(park.getId()));
	}

	@Test
	public void deleteAllSuccessTest() {
		List<Park> findAllResult=parkList();
		when(parkRepository.findAll()).thenReturn(findAllResult);
		doNothing().when(parkRepository).deleteAll();
		assertTrue(parkService.deleteAll());
	}

	@Test
	public void deleteAllFailureTest() {
		when(parkRepository.findAll()).thenReturn(new ArrayList<Park>());
		assertFalse(parkService.deleteAll());
	}

	@Test
	public void deleteByManageNoSuccessTest() {
		Park tmpResult=findOnePark();
		when(parkRepository.findByManageNo(tmpResult.getManageNo())).thenReturn(Optional.of(tmpResult));
		doNothing().when(parkRepository).deleteByManageNo(tmpResult.getManageNo());
		assertTrue(parkService.deleteByManageNo(tmpResult.getManageNo()));
	}

	@Test
	public void deleteByManageNoFailureTest() {
		Park tmpResult=findOnePark();
		when(parkRepository.findByManageNo(tmpResult.getManageNo())).thenReturn(Optional.of(new Park()));
		assertFalse(parkService.deleteByManageNo(tmpResult.getManageNo()));
	}
}

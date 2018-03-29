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
import net.kang.domain.Park;
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
	public void insertSuccessTest() {
		Park park=findOnePark();
		when(parkRepository.existsById(park.getId())).thenReturn(false);
		when(parkRepository.insert(park)).thenReturn(park);
		assertTrue(parkService.insert(park));
	}

	@Test
	public void insertFailureTest() {
		Park park=findOnePark();
		when(parkRepository.existsById(park.getId())).thenReturn(true);
		assertFalse(parkService.insert(park));
	}

	@Test
	public void updateSuccessTest() {
		Park park=findOnePark();
		when(parkRepository.existsById(park.getId())).thenReturn(true);
		park.setName("공원TEMP");
		park.setOldAddress("지번주소TEMP");
		park.setNewAddress("도로명주소TEMP");
		park.setArea(100.0+random.nextDouble()*100.0);
		when(parkRepository.save(park)).thenReturn(park);
		assertTrue(parkService.update(park));
	}

	@Test
	public void updateFailureTest() {
		Park park=findOnePark();
		when(parkRepository.existsById(park.getId())).thenReturn(false);
		assertFalse(parkService.update(park));
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
}

package net.kang.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import net.kang.model.Position;
import net.kang.repository.AgencyRepository;
import net.kang.repository.ParkRepository;
import net.kang.service.AgencyService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class AgencyServiceTest {
	static final int AGENCY_QTY=5;
	static final int OFFICE_QTY=2;
	static final int PARK_QTY=10;
	static Random random=new Random();
	static List<Agency> tmpAgencyList;
	MockMvc mockMvc;
	@Mock AgencyRepository agencyRepository;
	@Mock ParkRepository parkRepository;
	@InjectMocks AgencyService agencyService;

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(agencyService).build();
	}

	public List<Office> officeList(){
		List<Office> officeList=new ArrayList<Office>();
		for(int k=0;k<OFFICE_QTY;k++) {
			Office office=new Office();
			office.setId(String.format("%d", k));
			office.setName(String.format("시청%02d", k));
			office.setAddress(String.format("주소%02d", k));
			office.setHomepage(String.format("홈페이지%02d", k));
			office.setZipCode(String.format("우편번호%02d", k));
			officeList.add(office);
		}
		return officeList;
	}

	public List<Agency> agencyList(){
		List<Agency> agencyList=new ArrayList<Agency>();
		for(int k=0;k<AGENCY_QTY;k++) {
			Agency agency=new Agency();
			agency.setId(String.format("%d", k+1));
			agency.setName(String.format("기관%02d", k));
			Office office=officeList().get(random.nextInt(OFFICE_QTY));
			agency.setOffice(office);
			agencyList.add(agency);
		}
		tmpAgencyList=agencyList;
		return agencyList;
	}

	@Test
	public void findAllTest() {
		List<Agency> tmpResult=agencyList();
		when(agencyRepository.findAll()).thenReturn(tmpResult);
		List<Agency> findAllResult=agencyService.findAll();
		assertEquals(findAllResult, tmpResult);
	}

	public Agency findOneAgency() {
		Agency agency=new Agency();
		agency.setId("1");
		agency.setName("시구청01");
		agency.setOffice(officeList().get(random.nextInt(OFFICE_QTY)));
		return agency;
	}

	@Test
	public void findOneTest() {
		Agency tmpResult=findOneAgency();
		when(agencyRepository.findById("1")).thenReturn(Optional.of(tmpResult));
		Optional<Agency> findOneResult=agencyService.findOne(String.format("%d", 1));
		assertEquals(tmpResult, findOneResult.get());
	}

	public List<Park> parkList(Agency tmpAgency){
		Kind kind=new Kind();
		kind.setId("1");
		kind.setName("종류1");
		Agency agency=tmpAgency;
		List<Park> parkList=new ArrayList<Park>();
		for(int k=0;k<PARK_QTY;k++) {
			Park park=new Park();
			park.setId(String.format("%d", k+1));
			park.setName(String.format("공원%02d", k));
			park.setOldAddress(String.format("지번주소%02d", k));
			park.setNewAddress(String.format("도로명주소%02d", k));
			park.setCallPhone(String.format("전화번호%02d", k));
			park.setPosition(new Position(0.1, 0.1));
			park.setKind(kind);
			park.setArea(0.0);
			park.setManageNo(String.format("00000-000%02d", k));
			park.setAgency(agency);
			park.setDesignateDate(new Date());
			parkList.add(park);
		}
		return parkList;
	}

	@Test
	public void findOneAndParkFindAllIsNotEmptyTest() {
		Agency agency=findOneAgency();
		List<Park> parkFindAll=parkList(agency);
		when(agencyRepository.findById(agency.getId())).thenReturn(Optional.of(agency));
		when(parkRepository.findByAgency(agency)).thenReturn(parkFindAll);
		assertEquals(parkFindAll, agencyService.findOneAndParkFindAll(agency.getId()));
	}

	@Test
	public void findOneAndParkFindAllIsEmptyTest() {
		Agency agency=findOneAgency();
		List<Park> parkFindAll=new ArrayList<Park>();
		when(agencyRepository.findById(agency.getId())).thenReturn(Optional.of(new Agency()));
		assertEquals(parkFindAll, agencyService.findOneAndParkFindAll(agency.getId()));
	}

	@Test
	public void insertSuccessTest() {
		Agency agency=findOneAgency();
		when(agencyRepository.existsById(agency.getId())).thenReturn(false);
		when(agencyRepository.insert(agency)).thenReturn(agency);
		assertTrue(agencyService.insert(agency));
	}

	@Test
	public void insertFailureTest() {
		Agency agency=findOneAgency();
		when(agencyRepository.existsById(agency.getId())).thenReturn(true);
		assertFalse(agencyService.insert(agency));
	}

	@Test
	public void updateSuccessTest() {
		Agency agency=findOneAgency();
		when(agencyRepository.existsById(agency.getId())).thenReturn(true);
		agency.setName("기관TEMP");
		agency.setOffice(officeList().get(random.nextInt(OFFICE_QTY)));
		when(agencyRepository.save(agency)).thenReturn(agency);
		assertTrue(agencyService.update(agency));
	}

	@Test
	public void updateFailureTest() {
		Agency agency=findOneAgency();
		when(agencyRepository.existsById(agency.getId())).thenReturn(false);
		assertFalse(agencyService.update(agency));
	}

	@Test
	public void deleteSuccessTest() {
		Agency agency=findOneAgency();
		when(agencyRepository.existsById(agency.getId())).thenReturn(true);
		doNothing().when(agencyRepository).deleteById(agency.getId());
		assertTrue(agencyService.delete(agency.getId()));
	}

	@Test
	public void deleteFailureTest() {
		Agency agency=findOneAgency();
		when(agencyRepository.existsById(agency.getId())).thenReturn(false);
		assertFalse(agencyService.delete(agency.getId()));
	}
}

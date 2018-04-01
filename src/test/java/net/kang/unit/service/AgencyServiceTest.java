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
import net.kang.model.AgencyForm;
import net.kang.model.Position;
import net.kang.repository.AgencyRepository;
import net.kang.repository.OfficeRepository;
import net.kang.repository.ParkRepository;
import net.kang.service.AgencyService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class AgencyServiceTest { // 기관 서비스 테스팅 클래스 생성.
	static final int AGENCY_QTY=5;
	static final int OFFICE_QTY=2;
	static final int PARK_QTY=10; // 각각 기관, 시구청, 공원 수를 생성.
	MockMvc mockMvc;
	@Mock AgencyRepository agencyRepository;
	@Mock ParkRepository parkRepository;
	@Mock OfficeRepository officeRepository; // repository는 Mock 객체.
	@InjectMocks AgencyService agencyService; // service는 일반 객체

	@Before
	public void initialize() { // Mock 객체 이용을 할 수 있도록 설정함
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(agencyService).build();
	}

	public List<Office> officeList(){ // 시구청 목록에 대한 Mock 데이터 생성
		List<Office> officeList=new ArrayList<Office>();
		for(int k=0;k<OFFICE_QTY;k++) {
			Office office=new Office();
			office.setId(String.format("%d", k+1));
			office.setName(String.format("시청%02d", k));
			office.setAddress(String.format("주소%02d", k));
			office.setHomepage(String.format("홈페이지%02d", k));
			office.setZipCode(String.format("우편번호%02d", k));
			officeList.add(office);
		}
		return officeList;
	}

	public List<Agency> agencyList(){ // 기관 목록에 대한 Mock 데이터 생성
		List<Agency> agencyList=new ArrayList<Agency>();
		for(int k=0;k<AGENCY_QTY;k++) {
			Agency agency=new Agency();
			agency.setId(String.format("%d", k+1));
			agency.setName(String.format("기관%02d", k));
			Office office=officeList().get(0);
			agency.setOffice(office);
			agencyList.add(agency);
		}
		return agencyList;
	}

	@Test
	public void findAllTest() { // findAll 테스팅
		List<Agency> tmpResult=agencyList();
		when(agencyRepository.findAll()).thenReturn(tmpResult);
		List<Agency> findAllResult=agencyService.findAll();
		assertEquals(findAllResult, tmpResult);
	}

	public Agency findOneAgency() { // 기관으로 조회하는 테스팅
		Agency agency=new Agency();
		agency.setId("1");
		agency.setName("기관01");
		agency.setOffice(officeList().get(0));
		return agency;
	}

	@Test
	public void findOneTest() { // _id로 조회하는 테스팅
		Agency tmpResult=findOneAgency();
		when(agencyRepository.findById("1")).thenReturn(Optional.of(tmpResult));
		Optional<Agency> findOneResult=agencyService.findOne(String.format("%d", 1));
		assertEquals(tmpResult, findOneResult.get());
	}

	public List<Park> parkList(Agency tmpAgency){ // 공원 Mock 데이터 생성
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
	public void findOneAndParkFindAllIsNotEmptyTest() { // 공원 조회 성공 테스팅
		Agency agency=findOneAgency();
		List<Park> parkFindAll=parkList(agency);
		when(agencyRepository.findById(agency.getId())).thenReturn(Optional.of(agency));
		when(parkRepository.findByAgency(agency)).thenReturn(parkFindAll);
		assertEquals(parkFindAll, agencyService.findOneAndParkFindAll(agency.getId()));
	}

	@Test
	public void findOneAndParkFindAllIsEmptyTest() { // 공원 조회 실패 테스팅
		Agency agency=findOneAgency();
		List<Park> parkFindAll=new ArrayList<Park>();
		when(agencyRepository.findById(agency.getId())).thenReturn(Optional.of(new Agency()));
		assertEquals(parkFindAll, agencyService.findOneAndParkFindAll(agency.getId()));
	}

	@Test
	public void findByNameContainingTestIsNotEmptyTest() {  // 이름 포함 조회 성공 테스팅
		List<Agency> agencyList=agencyList();
		when(agencyRepository.findByNameContaining("기관")).thenReturn(agencyList);
		assertEquals(agencyList, agencyService.findByNameContaining("기관"));
	}

	@Test
	public void findByNameContainingTestIsEmptyTest() { // 이름 포함 조회 실패 테스팅
		List<Agency> agencyList=new ArrayList<Agency>();
		when(agencyRepository.findByNameContaining("가관")).thenReturn(agencyList);
		assertEquals(agencyList, agencyService.findByNameContaining("가관"));
	}

	public AgencyForm agencyToForm(Agency agency) { // 기관 객체를 기관 Form. 이는 추가, 수정할 때 필요.
		AgencyForm agencyForm=new AgencyForm();
		agencyForm.setAgencyId(agency.getId());
		agencyForm.setName(agency.getName());
		agencyForm.setOfficeId(agency.getOffice().getId());
		return agencyForm;
	}

	public Agency formToAgency(AgencyForm agencyForm, Office office) { // 기관 Form을 기관 객체로 반환
		Agency agency=new Agency();
		agency.setId(agencyForm.getAgencyId());
		agency.setName(agencyForm.getName());
		agency.setOffice(office);
		return agency;
	}

	@Test
	public void insertSuccessTest() { // 삽입 성공 테스팅
		Agency agency=findOneAgency();
		AgencyForm agencyForm=agencyToForm(agency);
		when(officeRepository.findById("1")).thenReturn(Optional.of(officeList().get(0)));
		Agency insertAfterAgency=formToAgency(agencyForm, officeList().get(0));
		when(agencyRepository.insert(insertAfterAgency)).thenReturn(insertAfterAgency);
		assertTrue(agencyService.insert(agencyForm));
	}

	@Test
	public void insertFailureTest() { // 삽입 실패 테스팅
		Agency agency=findOneAgency();
		AgencyForm agencyForm=agencyToForm(agency);
		when(officeRepository.findById("1")).thenReturn(Optional.of(new Office()));
		assertFalse(agencyService.insert(agencyForm));
	}

	@Test
	public void updateSuccessTest() { // 갱신 성공 테스팅
		Agency agency=findOneAgency();
		AgencyForm agencyForm=agencyToForm(agency);
		Office office=new Office();
		office.setId(String.format("%d", 1));
		office.setName(String.format("시청%02d", 1));
		office.setAddress(String.format("주소%02d", 1));
		office.setHomepage(String.format("홈페이지%02d", 1));
		office.setZipCode(String.format("우편번호%02d", 1));
		when(officeRepository.findById(office.getId())).thenReturn(Optional.of(office));
		when(agencyRepository.existsById(agency.getId())).thenReturn(true);
		agencyForm.setName("기관TEMP");
		agencyForm.setOfficeId(office.getId());
		Agency saveAfterAgency=formToAgency(agencyForm, office);
		when(agencyRepository.save(saveAfterAgency)).thenReturn(saveAfterAgency);
		assertTrue(agencyService.update(agencyForm));
	}

	@Test
	public void updateFailureTest() { // 갱신 실패 테스팅
		Agency agency=findOneAgency();
		AgencyForm agencyForm=agencyToForm(agency);
		when(agencyRepository.existsById(agency.getId())).thenReturn(false);
		when(officeRepository.findById("1")).thenReturn(Optional.of(new Office()));
		assertFalse(agencyService.update(agencyForm));
	}

	@Test
	public void deleteSuccessTest() { // 삭제 성공 테스팅
		Agency agency=findOneAgency();
		when(agencyRepository.existsById(agency.getId())).thenReturn(true);
		doNothing().when(agencyRepository).deleteById(agency.getId());
		assertTrue(agencyService.delete(agency.getId()));
	}

	@Test
	public void deleteFailureTest() { // 삭제 실패 테스팅
		Agency agency=findOneAgency();
		when(agencyRepository.existsById(agency.getId())).thenReturn(false);
		assertFalse(agencyService.delete(agency.getId()));
	}

	@Test
	public void deleteByNameContainingSuccessTest() { // 이름 포함 삭제 테스팅
		List<Agency> tmpResult=agencyList();
		when(agencyRepository.findByNameContaining("기관")).thenReturn(tmpResult);
		doNothing().when(agencyRepository).deleteByNameContaining("기관");
		assertTrue(agencyService.deleteByNameContaining("기관"));
	}

	@Test
	public void deleteByNameContainingFailureTest() { // 이름 포함 삭제 실패 테스팅
		when(agencyRepository.findByNameContaining("기관")).thenReturn(new ArrayList<Agency>());
		assertFalse(agencyService.deleteByNameContaining("기관"));
	}
}

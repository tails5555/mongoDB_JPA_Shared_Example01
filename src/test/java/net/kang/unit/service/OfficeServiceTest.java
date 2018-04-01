package net.kang.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import net.kang.domain.Office;
import net.kang.repository.AgencyRepository;
import net.kang.repository.OfficeRepository;
import net.kang.service.OfficeService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class OfficeServiceTest {
	static final int OFFICE_QTY=5;
	static final int AGENCY_QTY=10; // 시구청 데이터의 수를 5개, 기관 데이터의 수를 10개로 설정
	MockMvc mockMvc;
	@Mock OfficeRepository officeRepository;
	@Mock AgencyRepository agencyRepository; // repository는 Mock 객체.
	@InjectMocks OfficeService officeService; // service는 일반 객체

	@Before
	public void initialize() { // Mock 객체 이용을 할 수 있도록 설정함
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(officeService).build();
	}

	public List<Office> officeList(){ // 시구청 목록에 대한 Mock 데이터 생성
		List<Office> officeList=new ArrayList<Office>();
		for(int k=0;k<OFFICE_QTY;k++) {
			Office office=new Office();
			office.setId(String.format("%d", k+1));
			office.setName(String.format("시구청%02d", k));
			office.setAddress(String.format("주소%02d", k));
			office.setHomepage(String.format("홈페이지%02d", k));
			office.setZipCode(String.format("우편번호%02d", k));
			officeList.add(office);
		}
		return officeList;
	}

	@Test
	public void findAllTest() { // findAll 테스팅
		List<Office> tmpResult=officeList();
		when(officeRepository.findAll()).thenReturn(tmpResult);
		List<Office> findAllResult=officeService.findAll();
		assertEquals(tmpResult, findAllResult);
	}

	public Office findOneOffice() { // 시구청 객체 반환
		Office office=new Office();
		office.setId("1");
		office.setName("시구청01");
		office.setAddress("주소01");
		office.setZipCode("우편번호01");
		office.setHomepage("홈페이지01");
		return office;
	}

	@Test
	public void findOneTest() { // _id로 조회하는 테스팅
		Office tmpResult=findOneOffice();
		when(officeRepository.findById("1")).thenReturn(Optional.of(tmpResult));
		Optional<Office> findOneResult=officeService.findOne("1");
		assertEquals(tmpResult, findOneResult.get());
	}

	public List<Agency> agencyList(Office tmpOffice){ // 기관 목록 Mock 데이터 생성
		List<Agency> agencyList=new ArrayList<Agency>();
		for(int k=0;k<AGENCY_QTY;k++) {
			Agency agency=new Agency();
			agency.setId(String.format("%d", k+1));
			agency.setName(String.format("기관%02d", k));
			agency.setOffice(tmpOffice);
			agencyList.add(agency);
		}
		return agencyList;
	}

	@Test
	public void findOneAndAgencyFindAllIsNotEmptyTest() { // 시구청 별 기관 목록 가져오기 성공 테스팅
		Office office=findOneOffice();
		List<Agency> agencyFindAll=agencyList(office);
		when(officeRepository.findById(office.getId())).thenReturn(Optional.of(office));
		when(agencyRepository.findByOffice(office)).thenReturn(agencyFindAll);
		assertEquals(agencyFindAll, officeService.findOneAndAgencyFindAll(office.getId()));
	}

	@Test
	public void findOneAndAgencyFindAllIsEmptyTest() { // 시구청 별 기관 목록 가져오기 실패 테스팅
		Office office=findOneOffice();
		when(officeRepository.findById(office.getId())).thenReturn(Optional.of(new Office()));
		List<Agency> agencyFindAll=new ArrayList<Agency>();
		assertEquals(agencyFindAll, officeService.findOneAndAgencyFindAll(office.getId()));
	}

	@Test
	public void findByNameContainingTestIsNotEmptyTest() { // 이름 포함 조회 성공 테스팅
		List<Office> officeList=officeList();
		when(officeRepository.findByNameContaining("시구청")).thenReturn(officeList);
		assertEquals(officeList, officeService.findByNameContaining("시구청"));
	}

	@Test
	public void findByNameContainingTestIsEmptyTest() { // 이름 포함 조회 실패 테스팅
		List<Office> officeList=new ArrayList<Office>();
		when(officeRepository.findByNameContaining("시고청")).thenReturn(officeList);
		assertEquals(officeList, officeService.findByNameContaining("시고청"));
	}

	@Test
	public void insertSuccessTest() { // 삽입 성공 테스팅
		Office office=findOneOffice();
		when(officeRepository.existsById(office.getId())).thenReturn(false);
		when(officeRepository.insert(office)).thenReturn(office);
		assertTrue(officeService.insert(office));
	}

	@Test
	public void insertFailureTest() { // 삽입 실패 테스팅
		Office office=findOneOffice();
		when(officeRepository.existsById(office.getId())).thenReturn(true);
		assertFalse(officeService.insert(office));
	}

	@Test
	public void updateSuccessTest() { // 갱신 성공 테스팅
		Office office=findOneOffice();
		when(officeRepository.existsById(office.getId())).thenReturn(true);
		office.setName("시구청TEMP");
		office.setAddress("주소TEMP");
		office.setZipCode("우편번호TEMP");
		office.setHomepage("홈페이지TEMP");
		when(officeRepository.save(office)).thenReturn(office);
		assertTrue(officeService.update(office));
	}

	@Test
	public void updateFailureTest() { // 갱신 실패 테스팅
		Office office=findOneOffice();
		when(officeRepository.existsById(office.getId())).thenReturn(false);
		assertFalse(officeService.update(office));
	}

	@Test
	public void deleteSuccessTest() { // 삭제 성공 테스팅
		Office office=findOneOffice();
		when(officeRepository.existsById(office.getId())).thenReturn(true);
		doNothing().when(officeRepository).deleteById(office.getId());
		assertTrue(officeService.delete(office.getId()));
	}

	@Test
	public void deleteFailureTest() { // 삭제 실패 테스팅
		Office office=findOneOffice();
		when(officeRepository.existsById(office.getId())).thenReturn(false);
		assertFalse(officeService.delete(office.getId()));
	}

	@Test
	public void deleteByNameContainingSuccessTest() { // 이름 포함 삭제 테스팅
		List<Office> tmpResult=officeList();
		when(officeRepository.findByNameContaining("시구청")).thenReturn(tmpResult);
		doNothing().when(officeRepository).deleteByNameContaining("시구청");
		assertTrue(officeService.deleteByNameContaining("시구청"));
	}

	@Test
	public void deleteByNameContainingFailureTest() { // 이름 포함 삭제 실패 테스팅
		when(officeRepository.findByNameContaining("시거청")).thenReturn(new ArrayList<Office>());
		assertFalse(officeService.deleteByNameContaining("시거청"));
	}
}

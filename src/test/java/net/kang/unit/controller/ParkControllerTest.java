package net.kang.unit.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.kang.config.JUnitConfig;
import net.kang.config.MongoConfig;
import net.kang.controller.ParkController;
import net.kang.domain.Agency;
import net.kang.domain.Kind;
import net.kang.domain.Office;
import net.kang.domain.Park;
import net.kang.model.ParkForm;
import net.kang.model.Position;
import net.kang.service.ParkService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class ParkControllerTest {
	static final int PARK_QTY=10;
	static final int AGENCY_QTY=2;
	static final int KIND_QTY=2;
	static final int FACILITY_QTY=2;
	static Random random=new Random();
	MockMvc mockMvc;
	@Mock ParkService parkService;
	@InjectMocks ParkController parkController;

	private String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

	private List<String> facilityList(String facility){
		List<String> facilityList=new ArrayList<String>();
		for(int k=0;k<FACILITY_QTY;k++) {
			facilityList.add(String.format("%s%02d", facility, k));
		}
		return facilityList;
	}

	private List<Kind> kindList(){
		List<Kind> tmpKindList=new ArrayList<Kind>();
		for(int k=0;k<KIND_QTY;k++) {
			Kind kind=new Kind();
			kind.setId(String.format("%d", k+1));
			kind.setName(String.format("종류%02d", k));
			tmpKindList.add(kind);
		}
		return tmpKindList;
	}

	private List<Agency> agencyList(){
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

	private List<Park> parkList(){
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

	private Park findOnePark() {
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

	private Park formToPark(ParkForm parkForm, Agency agency, Kind kind) {
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

	private ParkForm parkToForm(Park park) {
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

	private Map<Kind, Long> tmpCountByKind(){
		Map<Kind, Long> countMap=new HashMap<Kind, Long>();
		for(Kind k : kindList()) {
			countMap.put(k, (long)5);
		}
		return countMap;
	}

	private Map<Agency, Long> tmpCountByAgency(){
		Map<Agency, Long> countMap=new HashMap<Agency, Long>();
		for(Agency a : agencyList()) {
			countMap.put(a, (long)5);
		}
		return countMap;
	}

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(parkController).build();
	}

	@Test
	public void findAllSuccessTest() throws Exception{
		List<Park> tmpList=parkList();
		when(parkService.findAll()).thenReturn(tmpList);
		String toJSON=this.jsonStringFromObject(tmpList);
		mockMvc.perform(get("/park/findAll"))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findAll();
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findAllFailureTest() throws Exception{
		List<Park> tmpList=new ArrayList<Park>();
		when(parkService.findAll()).thenReturn(tmpList);
		String toJSON=this.jsonStringFromObject(tmpList);
		mockMvc.perform(get("/park/findAll"))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findAll();
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByManageNoSuccessTest() throws Exception{
		Optional<Park> tmpResult=Optional.of(findOnePark());
		when(parkService.findByManageNo("00000-00001")).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/park/findByManageNo/{manageNo}", "00000-00001"))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByManageNo("00000-00001");
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByManageNoFailureTest() throws Exception{
		Optional<Park> tmpResult=Optional.of(new Park());
		when(parkService.findByManageNo("00000-00001")).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/park/findByManageNo/{manageNo}", "00000-00001"))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByManageNo("00000-00001");
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByNameContainingSuccessTest() throws Exception{
		List<Park> tmpParkList=parkList();
		String param=URLEncoder.encode("공원", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(parkService.findByNameContaining(param)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByNameContaining/{name}", param))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByNameContaining(param);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByNameContainingFailureTest() throws Exception{
		List<Park> tmpParkList=new ArrayList<Park>();
		String param=URLEncoder.encode("공원", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(parkService.findByNameContaining(param)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByNameContaining/{name}", param))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByNameContaining(param);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByConvFacilityContainsSuccessTest() throws Exception{
		List<Park> tmpParkList=parkList();
		String[] requestArray= {URLEncoder.encode("편의시설00", "UTF-8")};
		String param=URLEncoder.encode("편의시설00", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(parkService.findByConvFacilityContains(requestArray)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByConvFacilityContains/{convFacilities}", param))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByConvFacilityContains(requestArray);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByConvFacilityContainsFailureTest() throws Exception{
		List<Park> tmpParkList=new ArrayList<Park>();
		String[] requestArray= {URLEncoder.encode("편의시설00", "UTF-8")};
		String param=URLEncoder.encode("편의시설00", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(parkService.findByConvFacilityContains(requestArray)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByConvFacilityContains/{convFacilities}", param))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByConvFacilityContains(requestArray);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByCultFacilityContainsSuccessTest() throws Exception{
		List<Park> tmpParkList=parkList();
		String[] requestArray= {URLEncoder.encode("문화시설00", "UTF-8")};
		String param=URLEncoder.encode("문화시설00", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(parkService.findByCultFacilityContains(requestArray)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByCultFacilityContains/{cultFacilities}", param))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByCultFacilityContains(requestArray);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByCultFacilityContainsFailureTest() throws Exception{
		List<Park> tmpParkList=new ArrayList<Park>();
		String[] requestArray= {URLEncoder.encode("문화시설00", "UTF-8")};
		String param=URLEncoder.encode("문화시설00", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(parkService.findByCultFacilityContains(requestArray)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByCultFacilityContains/{cultFacilities}", param))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByCultFacilityContains(requestArray);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByAreaBetweenSuccessTest() throws Exception {
		List<Park> tmpParkList=parkList();
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(parkService.findByAreaBetween(99.0, 201.0)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByAreaBetween/{area1}/{area2}", 99.0, 201.0))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByAreaBetween(99.0, 201.0);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByAreaBetweenFailureTest() throws Exception {
		List<Park> tmpParkList=new ArrayList<Park>();
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(parkService.findByAreaBetween(99.0, 201.0)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByAreaBetween/{area1}/{area2}", 99.0, 201.0))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).findByAreaBetween(99.0, 201.0);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void findByAreaBetweenRangeErrorTest() throws Exception {
		List<Park> tmpParkList=new ArrayList<Park>();
		when(parkService.findByAreaBetween(201.0, 99.0)).thenReturn(tmpParkList);
		mockMvc.perform(get("/park/findByAreaBetween/{area1}/{area2}", 201.0, 99.0))
		.andExpect(status().isBadRequest())
		.andDo(print());

		verify(parkService, times(1)).findByAreaBetween(201.0, 99.0);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void countByKindSuccessTest() throws Exception{
		Map<Kind, Long> tmpResult=tmpCountByKind();
		String toJSON=this.jsonStringFromObject(tmpResult);
		when(parkService.countByKind()).thenReturn(tmpResult);
		mockMvc.perform(get("/park/countByKind"))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).countByKind();
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void countByKindFailureTest() throws Exception{
		Map<Kind, Long> tmpResult=new HashMap<Kind, Long>();
		String toJSON=this.jsonStringFromObject(tmpResult);
		when(parkService.countByKind()).thenReturn(tmpResult);
		mockMvc.perform(get("/park/countByKind"))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).countByKind();
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void countByAgencySuccessTest() throws Exception{
		Map<Agency, Long> tmpResult=tmpCountByAgency();
		String toJSON=this.jsonStringFromObject(tmpResult);
		when(parkService.countByAgency()).thenReturn(tmpResult);
		mockMvc.perform(get("/park/countByAgency"))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).countByAgency();
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void countByAgencyFailureTest() throws Exception{
		Map<Agency, Long> tmpResult=new HashMap<Agency, Long>();
		String toJSON=this.jsonStringFromObject(tmpResult);
		when(parkService.countByAgency()).thenReturn(tmpResult);
		mockMvc.perform(get("/park/countByAgency"))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(parkService, times(1)).countByAgency();
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void insertSuccessTest() throws Exception {
		Park park=findOnePark();
		ParkForm tmpForm=parkToForm(park);
		when(parkService.insert(tmpForm)).thenReturn(true);
		String requestPark=this.jsonStringFromObject(tmpForm);

		mockMvc.perform(
				post("/park/insert")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestPark))
		.andExpect(status().isCreated())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).insert(tmpForm);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void insertFailureTest() throws Exception{
		Park park=findOnePark();
		ParkForm tmpForm=parkToForm(park);
		when(parkService.insert(tmpForm)).thenReturn(false);
		String requestPark=this.jsonStringFromObject(tmpForm);

		mockMvc.perform(
				post("/park/insert")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestPark))
		.andExpect(status().isInternalServerError())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).insert(tmpForm);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void updateSuccessTest() throws Exception {
		Park park=findOnePark();
		ParkForm tmpForm=parkToForm(park);
		when(parkService.update(tmpForm)).thenReturn(true);
		String requestPark=this.jsonStringFromObject(tmpForm);

		mockMvc.perform(
				put("/park/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestPark))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).update(tmpForm);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void updateFailureTest() throws Exception{
		Park park=findOnePark();
		ParkForm tmpForm=parkToForm(park);
		when(parkService.update(tmpForm)).thenReturn(false);
		String requestPark=this.jsonStringFromObject(tmpForm);

		mockMvc.perform(
				put("/park/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestPark))
		.andExpect(status().isInternalServerError())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).update(tmpForm);
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void deleteSuccessTest() throws Exception {
		when(parkService.delete("1")).thenReturn(true);
		mockMvc.perform(delete("/park/delete/{id}", 1))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).delete("1");
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void deleteFailureTest() throws Exception{
		when(parkService.delete("1")).thenReturn(false);
		mockMvc.perform(delete("/park/delete/{id}", 1))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).delete("1");
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void deleteAllSuccessTest() throws Exception {
		when(parkService.deleteAll()).thenReturn(true);
		mockMvc.perform(delete("/park/deleteAll"))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).deleteAll();
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void deleteAllFailureTest() throws Exception{
		when(parkService.deleteAll()).thenReturn(false);
		mockMvc.perform(delete("/park/deleteAll"))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).deleteAll();
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void deleteByManageNoSuccessTest() throws Exception {
		when(parkService.deleteByManageNo("00000-00001")).thenReturn(true);
		mockMvc.perform(delete("/park/deleteByManageNo/{manageNo}", "00000-00001"))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).deleteByManageNo("00000-00001");
		verifyNoMoreInteractions(parkService);
	}

	@Test
	public void deleteByManageNoFailureTest() throws Exception{
		when(parkService.deleteByManageNo("00000-00001")).thenReturn(false);
		mockMvc.perform(delete("/park/deleteByManageNo/{manageNo}", "00000-00001"))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(parkService, times(1)).deleteByManageNo("00000-00001");
		verifyNoMoreInteractions(parkService);
	}
}

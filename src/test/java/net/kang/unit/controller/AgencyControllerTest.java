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
import java.util.List;
import java.util.Optional;

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
import net.kang.controller.AgencyController;
import net.kang.domain.Agency;
import net.kang.domain.Office;
import net.kang.domain.Park;
import net.kang.model.AgencyForm;
import net.kang.model.Position;
import net.kang.service.AgencyService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class AgencyControllerTest {
	static final int AGENCY_QTY=5;
	static final int OFFICE_QTY=2;
	static final int PARK_QTY=10;
	MockMvc mockMvc;
	@Mock AgencyService agencyService;
	@InjectMocks AgencyController agencyController;

	private List<Agency> agencyList(){
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

	private Agency findOneAgency() {
		Agency agency=new Agency();
		agency.setId("1");
		agency.setName("기관01");
		agency.setOffice(officeList().get(0));
		return agency;
	}

	private List<Office> officeList(){
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

	private List<Park> parkList(Agency tmpAgency){
		List<Park> parkList=new ArrayList<Park>();
		for(int k=0;k<PARK_QTY;k++) {
			Park park=new Park();
			park.setId(String.format("%d", k+1));
			park.setName(String.format("공원%02d", k));
			park.setOldAddress(String.format("지번주소%02d", k));
			park.setNewAddress(String.format("도로명주소%02d", k));
			park.setCallPhone(String.format("전화번호%02d", k));
			park.setPosition(new Position(0.1, 0.1));
			park.setArea(0.0);
			park.setManageNo(String.format("00000-000%02d", k));
			park.setAgency(tmpAgency);
			park.setDesignateDate(new Date());
			parkList.add(park);
		}
		return parkList;
	}

	private String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(agencyController).build();
	}

	@Test
	public void findAllSuccessTest() throws Exception{
		List<Agency> tmpList=agencyList();
		when(agencyService.findAll()).thenReturn(tmpList);
		String toJSON=this.jsonStringFromObject(tmpList);
		mockMvc.perform(get("/agency/findAll"))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findAll();
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findAllFailureTest() throws Exception{
		List<Agency> tmpList=new ArrayList<Agency>();
		when(agencyService.findAll()).thenReturn(tmpList);
		String toJSON=this.jsonStringFromObject(tmpList);
		mockMvc.perform(get("/agency/findAll"))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findAll();
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findOneSuccessTest() throws Exception{
		Optional<Agency> tmpResult=Optional.of(findOneAgency());
		when(agencyService.findOne("1")).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/agency/findOne/{id}", 1))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findOne("1");
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findOneFailureTest() throws Exception{
		Optional<Agency> tmpResult=Optional.of(new Agency());
		when(agencyService.findOne("1")).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/agency/findOne/{id}", 1))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findOne("1");
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findOneAndParkFindAllSuccessTest() throws Exception {
		Agency tmpAgency=findOneAgency();
		List<Park> tmpParkList=parkList(tmpAgency);
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(agencyService.findOneAndParkFindAll("1")).thenReturn(tmpParkList);
		mockMvc.perform(get("/agency/findOne/parkList/{id}", 1))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findOneAndParkFindAll("1");
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findOneAndParkFindAllFailureTest() throws Exception {
		List<Park> tmpParkList=new ArrayList<Park>();
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(agencyService.findOneAndParkFindAll("1")).thenReturn(tmpParkList);
		mockMvc.perform(get("/agency/findOne/parkList/{id}", 1))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findOneAndParkFindAll("1");
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findByNameSuccessTest() throws Exception{
		Optional<Agency> tmpResult=Optional.of(findOneAgency());
		String param=URLEncoder.encode("기관00", "UTF-8");
		when(agencyService.findByName(param)).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/agency/findByName/{name}", param))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findByName(param);
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findByNameFailureTest() throws Exception{
		Optional<Agency> tmpResult=Optional.of(new Agency());
		String param=URLEncoder.encode("기관00", "UTF-8");
		when(agencyService.findByName(param)).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/agency/findByName/{name}", param))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findByName(param);
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findByNameContainingSuccessTest() throws Exception{
		List<Agency> tmpParkList=agencyList();
		String param=URLEncoder.encode("기관", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(agencyService.findByNameContaining(param)).thenReturn(tmpParkList);
		mockMvc.perform(get("/agency/findByNameContaining/{name}", param))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findByNameContaining(param);
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void findByNameContainingFailureTest() throws Exception{
		List<Agency> tmpParkList=new ArrayList<Agency>();
		String param=URLEncoder.encode("기관", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(agencyService.findByNameContaining(param)).thenReturn(tmpParkList);
		mockMvc.perform(get("/agency/findByNameContaining/{name}", param))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(agencyService, times(1)).findByNameContaining(param);
		verifyNoMoreInteractions(agencyService);
	}

	public AgencyForm agencyToForm(Agency agency) {
		AgencyForm agencyForm=new AgencyForm();
		agencyForm.setAgencyId(agency.getId());
		agencyForm.setName(agency.getName());
		agencyForm.setOfficeId(agency.getOffice().getId());
		return agencyForm;
	}


	public Agency formToAgency(AgencyForm agencyForm, Office office) {
		Agency agency=new Agency();
		agency.setId(agencyForm.getAgencyId());
		agency.setName(agencyForm.getName());
		agency.setOffice(office);
		return agency;
	}

	@Test
	public void insertSuccessTest() throws Exception {
		Agency agency=findOneAgency();
		AgencyForm tmpForm=agencyToForm(agency);
		when(agencyService.insert(tmpForm)).thenReturn(true);
		String requestAgency=this.jsonStringFromObject(tmpForm);

		mockMvc.perform(
				post("/agency/insert")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestAgency))
		.andExpect(status().isCreated())
		.andDo(print()).andReturn();

		verify(agencyService, times(1)).insert(tmpForm);
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void insertFailureTest() throws Exception{
		Agency agency=findOneAgency();
		AgencyForm tmpForm=agencyToForm(agency);
		when(agencyService.insert(tmpForm)).thenReturn(false);
		String requestAgency=this.jsonStringFromObject(tmpForm);

		mockMvc.perform(
				post("/agency/insert")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestAgency))
		.andExpect(status().isInternalServerError())
		.andDo(print()).andReturn();

		verify(agencyService, times(1)).insert(tmpForm);
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void updateSuccessTest() throws Exception {
		Agency agency=findOneAgency();
		AgencyForm tmpForm=agencyToForm(agency);
		when(agencyService.update(tmpForm)).thenReturn(true);
		String requestAgency=this.jsonStringFromObject(tmpForm);

		mockMvc.perform(
				put("/agency/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestAgency))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(agencyService, times(1)).update(tmpForm);
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void updateFailureTest() throws Exception{
		Agency agency=findOneAgency();
		AgencyForm tmpForm=agencyToForm(agency);
		when(agencyService.update(tmpForm)).thenReturn(false);
		String requestAgency=this.jsonStringFromObject(tmpForm);

		mockMvc.perform(
				put("/agency/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestAgency))
		.andExpect(status().isInternalServerError())
		.andDo(print()).andReturn();

		verify(agencyService, times(1)).update(tmpForm);
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void deleteSuccessTest() throws Exception {
		when(agencyService.delete("1")).thenReturn(true);
		mockMvc.perform(delete("/agency/delete/{id}", 1))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(agencyService, times(1)).delete("1");
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void deleteFailureTest() throws Exception{
		when(agencyService.delete("1")).thenReturn(false);
		mockMvc.perform(delete("/agency/delete/{id}", 1))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(agencyService, times(1)).delete("1");
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void deleteByNameContainingSuccessTest() throws Exception {
		String param=URLEncoder.encode("기관", "UTF-8");
		when(agencyService.deleteByNameContaining(param)).thenReturn(true);
		mockMvc.perform(delete("/agency/deleteByNameContaining/{name}", param))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(agencyService, times(1)).deleteByNameContaining(param);
		verifyNoMoreInteractions(agencyService);
	}

	@Test
	public void deleteByNameContainingFailureTest() throws Exception{
		String param=URLEncoder.encode("기관", "UTF-8");
		when(agencyService.deleteByNameContaining(param)).thenReturn(false);
		mockMvc.perform(delete("/agency/deleteByNameContaining/{name}", param))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(agencyService, times(1)).deleteByNameContaining(param);
		verifyNoMoreInteractions(agencyService);
	}

}

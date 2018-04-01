package net.kang.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ParkForm { // 공원 데이터를 추가하기 위한 Form.
	String parkId; // 공원의 _id
	String name; // 공원 이름
	String manageNo; // 공원 관리 번호
	String kindId; // 공원 종류 _id
	String oldAddress; // 공원 지번주소
	String newAddress; // 공원 도로명주소
	double posX; // 공원 위도
	double posY; // 공원 경도
	double area; // 공원 면적
	List<String> jymFacility; // 공원 체육시설
	List<String> playFacility; // 공원 유희시설
	List<String> convFacility; // 공원 편의시설
	List<String> cultFacility; // 공원 문화시설
	List<String> anotFacility; // 공원 기타시설
	Date designateDate; // 공원 지정고시일
	String agencyId; // 공원 관리 기관 _id
	String callPhone; // 공원 연락처
}

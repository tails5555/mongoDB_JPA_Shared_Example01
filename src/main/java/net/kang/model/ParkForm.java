package net.kang.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ParkForm {
	String parkId;
	String name;
	String manageNo;
	String kindId;
	String oldAddress;
	String newAddress;
	double posX;
	double posY;
	double area;
	List<String> jymFacility;
	List<String> playFacility;
	List<String> convFacility;
	List<String> cultFacility;
	List<String> anotFacility;
	Date designateDate;
	String agencyId;
	String callPhone;
}

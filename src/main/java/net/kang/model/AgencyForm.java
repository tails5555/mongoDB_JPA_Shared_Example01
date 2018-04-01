package net.kang.model;

import lombok.Data;

@Data
public class AgencyForm { // Agency를 추가하기 위한 Form.
	String agencyId; // 기관 _id
	String name; // 기관 이름
	String officeId; // 시구청 _id
}

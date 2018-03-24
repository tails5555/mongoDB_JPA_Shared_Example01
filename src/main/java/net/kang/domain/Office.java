package net.kang.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="office")
public class Office {
	@Id
	String id;
	String name; // 시-구청 이름
	String homepage; // 홈페이지
	String zipCode; // 우편번호
	String address; // 주소
}

package net.kang.model;

import lombok.Data;

@Data
public class Position { // Park에서 쓰이는 Position Embedded Document 데이터
	double posX; // 위도
	double posY; // 경도
	public Position(double posX, double posY) { // 공원 위치 생성자
		this.posX=posX;
		this.posY=posY;
	}
}

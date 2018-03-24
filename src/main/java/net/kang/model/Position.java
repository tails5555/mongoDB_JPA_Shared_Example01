package net.kang.model;

import lombok.Data;

@Data
public class Position {
	double posX;
	double posY;
	public Position(double posX, double posY) {
		this.posX=posX;
		this.posY=posY;
	}
}

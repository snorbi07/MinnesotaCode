package com.norbertsram.ecgapi;

public enum EcgLead {

	I(0, "I"),
	II(1, "II"),
	III(2, "III"),
	AVR(3, "aVR"),
	AVL(4, "aVL"),
	AVF(5, "aVF"),
	V1(6, "V1"),
	V2(7, "V2"),
	V3(8, "V3"),
	V4(9, "V4"),
	V5(10, "V5"),
	V6(11, "V6");
	
	EcgLead(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	private final int index;
	private final String name;
}

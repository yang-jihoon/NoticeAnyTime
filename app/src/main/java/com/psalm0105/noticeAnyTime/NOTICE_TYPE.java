package com.psalm0105.noticeAnyTime;

public enum NOTICE_TYPE {
	ALARM(1)
	,SNOOZE(2)
	;
	
	private int typeNum = 0;
	NOTICE_TYPE(int typeNum) {
		typeNum = this.typeNum;
	}
	
	public int getTypeNum() {
		return typeNum;
	}
}

package com.psalm0105.notice.anytime;

public class RuleDomain {
	private int id;
	private String title;
	private RuleType type;
	private String filter;
	private String action;
	private String enable;

	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public RuleType getType() {
		return type;
	}
	public void setType(RuleType type) {
		this.type = type;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
	
}

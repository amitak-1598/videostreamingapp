package com.stream.app.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="yt_courses")
public class Course {

	
	@Id
	private String id;
	private String title;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
//	@OneToMany(mappedBy="course")
//	private List<Video> list = new ArrayList<>();
//	public List<Video> getList() {
//		return list;
//	}
//	public void setList(List<Video> list) {
//		this.list = list;
//	}
	
	
	
	
	
}

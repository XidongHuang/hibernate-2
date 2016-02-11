package com.tony.hibernate.entities;

import java.sql.Blob;
import java.util.Date;


public class News {

	private Integer id;
	private String title;
	private String author;

	private Date date;
	
	
	
	//The fieldis: title: author
	private String desc;
	
	
	//Text
	private String content;
	
	//Binary data
	private Blob image;

	
	
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public News(String title, String author, Date date) {
		super();
		this.title = title;
		this.author = author;
		this.date = date;
	}

	public News() {
		super();
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", title=" + title + ", author=" + author + ", date=" + date + "]";
	}

}

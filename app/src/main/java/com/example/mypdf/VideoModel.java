package com.example.mypdf;

public class VideoModel {
	private String name;
	private String path;
	private long length;
	public VideoModel() {
		super();
	}
	public VideoModel(String name, String path, long length) {
		super();
		this.name = name;
		this.path = path;
		this.length = length;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
}

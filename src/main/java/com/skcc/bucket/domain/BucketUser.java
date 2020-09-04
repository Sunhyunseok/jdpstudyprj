package com.skcc.bucket.domain;

import java.io.File;

public class BucketUser {

	private String userName;
	private File file;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}

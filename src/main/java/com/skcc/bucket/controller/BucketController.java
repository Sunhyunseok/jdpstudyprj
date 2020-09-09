package com.skcc.bucket.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.model.Bucket;
import com.skcc.bucket.domain.BucketUser;
import com.skcc.bucket.service.BucketService;

@RestController

@RequestMapping("/v1/s3/")
public class BucketController {

	private BucketService bucketService;
	
	@Autowired
	BucketController(BucketService bucketService){
		this.bucketService= bucketService;
	}
	
	//버킷 생성
	@PostMapping("/bucket")
	public Bucket createBucket(@RequestBody BucketUser user) throws IOException {
		return this.bucketService.createBucket(user.getUserName());
	}
	
	//버킷 삭제
	//@DeleteMapping("/bucket")
	//public Bucket deleteBucket(@RequestBody BucketUser user) throws IOException {
	//	return this.bucketService.deleteBucket(user.getUserName());
	//}
	
	//s3 버킷 정책 생성
	//@PostMapping("/bucket-policy")
	//public String setBucketPolicy(@RequestBody User user) throws IOException {
	//	return this.bucketService.setBucketPolicy(user.getUserName());
	//}
	
	//s3 파일 업로드
	@PostMapping("/file")
	public String uploadFile(@RequestBody BucketUser user) throws IOException{
		return this.bucketService.uploadFile(user.getUserName(),user.getFile());
	}
	
}

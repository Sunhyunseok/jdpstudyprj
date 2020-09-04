package com.skcc.iam.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skcc.iam.domain.IamUser;
import com.skcc.iam.service.IamService;


@RestController

@RequestMapping("/v1/iam/")
public class IamController {


	private IamService iamService;
	
	@Autowired
	IamController(IamService iamService){
		this.iamService= iamService;
	}
	//user 생성
	@PostMapping("/user")
	public String createIAMUser(@RequestBody IamUser user) throws IOException {
		return this.iamService.createIAMUser(user.getUserName());
	}
	
	//access key 생성 및 할당 
	@PostMapping("/access-key")
	public String createAccessKey(@RequestBody IamUser user) throws IOException {
		return this.iamService.createIAMAccessKey(user.getUserName());
	}
	
	 //policy 생성
	@PostMapping("/iam-policy") 
	 public String createIAMPolicy(@RequestBody IamUser user) throws IOException { 
		 return this.iamService.createIAMPolicy(user.getUserName());
	 }
	 
}

package com.skcc.iam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AttachUserPolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachUserPolicyResult;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;
import com.amazonaws.services.identitymanagement.model.CreatePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreatePolicyResult;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;

import software.amazon.awssdk.services.iam.model.IamException;



@Service
public class IamService {
	
	@Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
	
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
  
 	
	//IAM user create
	public String createIAMUser(String userName) {
		  
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
			 
	 	AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard()
		 			.withCredentials(new AWSStaticCredentialsProvider(credentials))
		            .withRegion(Regions.AP_NORTHEAST_2)
		            .build();
	 	//String username = userName;
	 	//Region region = Region.AWS_GLOBAL;
		//IamClient iam = IamClient.builder().region(region).build();
		
	 	
	 	try {
           
	 		//CreateUserRequest request = CreateUserRequest.builder().userName(username).build();
	 		CreateUserRequest request = new CreateUserRequest().withUserName(userName);
            //CreateUserResponse response = iam.createUser(request);
	 		CreateUserResult response = iam.createUser(request);

	 		return response.getUser().getUserName();

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
	 	return "";
    }
	//User IAM AccessKey create
	public String createIAMAccessKey(String userName) {
		  
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard()
		 			.withCredentials(new AWSStaticCredentialsProvider(credentials))
		            .withRegion(Regions.AP_NORTHEAST_2)
		            .build();
		//String username = userName;
		//Region region = Region.AWS_GLOBAL;
		//IamClient iam = IamClient.builder().region(region).build();
		 
			
	 	try {
	        	
            //CreateAccessKeyRequest request = CreateAccessKeyRequest.builder().userName(username).build();

            //CreateAccessKeyResponse response = iam.createAccessKey(request);
            
            CreateAccessKeyRequest request = new CreateAccessKeyRequest().withUserName(userName);

            CreateAccessKeyResult response = iam.createAccessKey(request);
            
            String keyId = response.getAccessKey().getAccessKeyId();
           //
            // System.out.println(response.accessKey().secretAccessKey());
            return keyId;

	        } catch (IamException e) {
	            System.err.println(e.awsErrorDetails().errorMessage());
	            System.exit(1);
	        }
	        return "";
	    }
		
	public String PolicyDocument(String userName) {
		String policydocument =
				"{" +
                "  \"Version\": \"2012-10-17\",	" +
                "  \"Statement\": [				" +
                "    {" +
                "    	\"Effect\": \"Allow\","+
                "    	\"Action\": [" +
                "			\"s3:GetBucketLocation\", "+
                "      		\"s3:ListAllMyBuckets\" "+
                "  		 ]," +
                "   	\"Resource\": \"arn:aws:s3:::*\" "+
                "	},"+
                "   {"+
                "       \"Effect\": \"Allow\",	" +
                "       \"Action\": \"s3:*\"," +
                "		\"Resource\": ["+
                "			\"arn:aws:s3:::jdpbucket"+userName+"\", "+
                "			\"arn:aws:s3:::jdpbucket"+userName+"/*\""+
				"		 ]"+
                "    }" +
                "   ]" +
                "}";
		
		return policydocument;

	}
			
           
	
	public String createIAMPolicy(String userName) {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
	 	AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard()
		 			.withCredentials(new AWSStaticCredentialsProvider(credentials))
		            .withRegion(Regions.AP_NORTHEAST_2)
		            .build();
		//String username = userName;
		//Region region = Region.AWS_GLOBAL;
		//IamClient iam = IamClient.builder().region(region).build();
		
		String policyName= "JDPpolicy"+userName;
		String policydocument = PolicyDocument(userName);
        try {
              //CreatePolicyRequest request = CreatePolicyRequest.builder()
              //  .policyName(policyName)
              //  .policyDocument(policydocument).build();
              
        	CreatePolicyRequest request = new CreatePolicyRequest()
                      .withPolicyName(policyName)
                      .withPolicyDocument(policydocument);

              //CreatePolicyResponse response = iam.createPolicy(request);
        	CreatePolicyResult response = iam.createPolicy(request);
        	
        	String policyArn = "arn:aws:iam::739913306696:policy/"+policyName;
              
			AmazonIdentityManagement client = AmazonIdentityManagementClientBuilder.standard().build();
			AttachUserPolicyRequest policyrequest = new AttachUserPolicyRequest().withUserName(userName).withPolicyArn(policyArn);
			AttachUserPolicyResult policyresponse = client.attachUserPolicy(policyrequest);
			  
			return response.getPolicy().getArn();
			
         } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
	}
    
}
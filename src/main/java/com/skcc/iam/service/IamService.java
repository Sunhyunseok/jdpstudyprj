package com.skcc.iam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AttachUserPolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachUserPolicyResult;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyRequest;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyResponse;
import software.amazon.awssdk.services.iam.model.CreatePolicyRequest;
import software.amazon.awssdk.services.iam.model.CreatePolicyResponse;
import software.amazon.awssdk.services.iam.model.IamException;

@Service
public class IamService {
	
	@Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;
    
	//IAM user create
	public String createIAMUser(String userName) {

	 	String username = userName;
	 	//Region region = Region.AWS_GLOBAL;
	 	
	 	AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
	 	
	 	AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard()
	 			.withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
	 	
	 	

	 		
	 	//IamClient iam = IamClient.builder().region(region).build();
	 	
	 	try {
            //CreateUserRequest request = CreateUserRequest.builder().userName(username).build();
	 		CreateUserRequest request = new CreateUserRequest().withUserName(username);
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
		 	
		String username = userName;
		Region region = Region.AWS_GLOBAL;
		IamClient iam = IamClient.builder().region(region).build();
 	
			try {
	        	
            CreateAccessKeyRequest request = CreateAccessKeyRequest.builder().userName(username).build();

            CreateAccessKeyResponse response = iam.createAccessKey(request);
            String keyId = response.accessKey().accessKeyId();
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
		
		String username = userName;
		String policyName= "JDPpolicy"+userName;
		Region region = Region.AWS_GLOBAL;
		String policydocument = PolicyDocument(username);
		IamClient iam = IamClient.builder().region(region).build();
        try {
              CreatePolicyRequest request = CreatePolicyRequest.builder()
                .policyName(policyName)
                .policyDocument(policydocument).build();

              CreatePolicyResponse response = iam.createPolicy(request);
              String policyArn = "arn:aws:iam::739913306696:policy/"+policyName;
              
              AmazonIdentityManagement client = AmazonIdentityManagementClientBuilder.standard().build();
              AttachUserPolicyRequest policyrequest = new AttachUserPolicyRequest().withUserName(userName).withPolicyArn(policyArn);
              AttachUserPolicyResult policyresponse = client.attachUserPolicy(policyrequest);
              
              return response.policy().arn();
         } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
	}
    
}
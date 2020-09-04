package com.skcc.bucket.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class BucketService {

	@Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
	
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    
	public  Bucket getBucket(String bucketName) {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(credentials))
        		.withRegion(Regions.AP_NORTHEAST_2).build();
        
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucketName)) {
                named_bucket = b;
            }
        }
        return named_bucket;
    }
	
	public Bucket createBucket(String userName) {
		
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(credentials))
        		.withRegion(Regions.AP_NORTHEAST_2).build();
		
		String bucketName = "jdpbucket"+userName;
        
        Bucket b = null;
        if (s3.doesBucketExistV2(bucketName)) {
            System.out.format("Bucket %s already exists.\n", bucketName);
            b = getBucket(bucketName);
        } else {
            try {
                b = s3.createBucket(bucketName);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
        return b;
    }
	
	//test commit
	public static String getPublicReadPolicy(String bucket_name) {
        Policy bucket_policy = new Policy().withStatements(
                new Statement(Statement.Effect.Allow)
                        .withPrincipals(Principal.AllUsers)
                        .withActions(S3Actions.GetObject)
                        .withResources(new Resource(
                                "arn:aws:s3:::" + bucket_name + "/*")));
        return bucket_policy.toJson();
    }

	public String PolicyDocument(String userName) {
		String policydocument =
				"{" +
                "  \"Version\": \"2012-10-17\",	" +
                "  \"Statement\": [				" +
                "    {" +
                "       \"Effect\": \"Allow\",	" +
                "		\"Principal\": { \"AWS\": \"arn:aws:iam::739913306696:user/"+userName+"\"}," + 
                "       \"Action\": \"s3:*\"," +
                "		\"Resource\": \"arn:aws:s3:::jdpbucket"+userName+"\" "+
                "    }" +
                "   ]" +
                "}";
		
		return policydocument;
		
	}
	
    public String setBucketPolicy(String userName) {
    	
    	AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(credentials))
        		.withRegion(Regions.AP_NORTHEAST_2).build();
        
        String bucket_name= "jdpbucket"+userName;
        String policydocument = PolicyDocument(userName);
        try {
            s3.setBucketPolicy(bucket_name, policydocument);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        return "";
    }
    
    public String uploadFile(String userName, File file) {
    	
    	AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(credentials))
        		.withRegion(Regions.AP_NORTHEAST_2).build();
        
    	String bucketName = "jdpbucket"+userName;
    	
    	PutObjectRequest request = new PutObjectRequest(bucketName,file.getName(),file);
    	
    	s3.putObject(request);
    	
    	return "";
    	
    }
    
   

}  

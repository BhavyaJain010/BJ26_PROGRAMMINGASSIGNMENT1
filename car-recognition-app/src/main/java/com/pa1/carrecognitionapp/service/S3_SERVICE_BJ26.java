package com.pa1.carrecognitionapp.service;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;



public class S3_SERVICE_BJ26 {

    private static String bucketName = "njit-cs-643";


    private S3Client s3Client;

    public S3_SERVICE_BJ26() {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    public S3Client getS3Client() {
        return s3Client;
    }

    public List<S3Object> s3DataFetch(S3Client s3Client){
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            return s3Client.listObjects(listObjects).contents();

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }

        return null;
    }

}

package com.pa1.textdetectionapp.textdetectionapp.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Slf4j
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


    public Image s3FetchByName(String imgKey){
        try {
            return Image.builder().s3Object(S3Object.builder().bucket(bucketName).name(imgKey).build()).build();
        } catch (S3Exception e) {
            log.info(e.awsErrorDetails().errorMessage());
        }
        return null;
    }

}

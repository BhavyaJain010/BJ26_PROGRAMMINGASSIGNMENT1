package com.pa1.textdetectionapp.textdetectionapp.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
/**
 * Service class to handle interactions with Amazon S3.
 */
public class S3_SERVICE_BJ26 {
    // The name of the S3 bucket to fetch data from
    private static final String BUCKET_NAME = "njit-cs-643";

    // AWS SDK S3 client instance
    private final S3Client s3Client;

    /**
     * Constructor initializes the S3Client with the specified region.
     */
    public S3_SERVICE_BJ26() {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    /**
     * Getter method for the S3Client.
     *
     * @return Returns the initialized S3Client.
     */
    public S3Client getS3Client() {
        return s3Client;
    }

    /**
     * Fetches an image from S3 based on the provided image key and constructs an Image object.
     *
     * @param imgKey The key (filename) of the image in the S3 bucket.
     * @return An Image object constructed using the provided imgKey.
     */
    public Image s3FetchByName(String imgKey){
        try {
            // Constructing an Image object using the S3 bucket details and provided imgKey
            return Image.builder().s3Object(S3Object.builder().bucket(BUCKET_NAME).name(imgKey).build()).build();
        } catch (S3Exception e) {
            // Log any exceptions that occur while fetching the image
            log.error("Error fetching image from S3: {}", e.awsErrorDetails().errorMessage());
        }
        return null;
    }
}

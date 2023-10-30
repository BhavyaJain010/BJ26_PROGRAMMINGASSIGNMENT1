package com.pa1.carrecognitionapp.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

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
     * Fetches a list of objects from the specified S3 bucket.
     *
     * @param s3Client S3Client instance.
     * @return List of S3 objects present in the bucket.
     */
    public List<S3Object> s3DataFetch(S3Client s3Client){
        try {
            // Create a request to list all objects in the specified bucket
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(BUCKET_NAME)
                    .build();

            // Return the list of S3 objects present in the bucket
            return s3Client.listObjects(listObjects).contents();

        } catch (S3Exception e) {
            // Print the error message if any exception occurs
            System.err.println(e.awsErrorDetails().errorMessage());
        }

        return null;
    }
}

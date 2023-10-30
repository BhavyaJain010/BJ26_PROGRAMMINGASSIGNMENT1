package com.pa1.carrecognitionapp.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RekognitionService_BJ26 {

    // Instance of the RekognitionClient
    private final RekognitionClient rekognitionClient;

    // Constructor initializes the RekognitionClient with the specified region
    public RekognitionService_BJ26() {
        this.rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    // Getter for the RekognitionClient
    public RekognitionClient getRekognitionClient() {
        return rekognitionClient;
    }

    /**
     * Recognizes whether an image contains a car label using AWS Rekognition.
     *
     * @param rekognitionClient RekognitionClient instance.
     * @param s3Object S3Object representing the image.
     * @param bucketName Name of the S3 bucket containing the image.
     * @return true if the image contains a car label, false otherwise.
     */
    public boolean recognize(RekognitionClient rekognitionClient, S3Object s3Object, String bucketName) {
        try {
            // Constructing the Image object using the S3 bucket details
            Image img = Image.builder()
                    .s3Object(software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                            .bucket(bucketName)
                            .name(s3Object.key())
                            .build())
                    .build();

            // Create a request to detect labels in the image with a minimum confidence of 90%
            DetectLabelsRequest request = DetectLabelsRequest.builder()
                    .image(img)
                    .minConfidence(90.0f)
                    .build();

            // Process the image and obtain the labels
            DetectLabelsResponse result = rekognitionClient.detectLabels(request);

            // Extract the label names using Java Streams
            List<String> labelNames = result.labels().stream()
                    .map(Label::name)
                    .collect(Collectors.toList());

            // Check if one of the labels is "Car"
            return labelNames.contains("Car");

        } catch (RekognitionException e) {
            // Log any recognition errors
            log.error("Error recognizing image: {}", e.getMessage());
        }
        return false;
    }
}

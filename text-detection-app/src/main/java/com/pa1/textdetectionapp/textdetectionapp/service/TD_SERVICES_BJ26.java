package com.pa1.textdetectionapp.textdetectionapp.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.DetectTextResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.TextDetection;

import java.util.List;
import java.util.Map;

@Slf4j
/**
 * Service class for handling text detection using Amazon Rekognition.
 */
public class TD_SERVICES_BJ26 {

    // AWS SDK Rekognition client instance
    private final RekognitionClient rekognitionClient;

    /**
     * Constructor initializes the RekognitionClient with the specified region.
     */
    public TD_SERVICES_BJ26() {
        this.rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    /**
     * Getter method for the RekognitionClient.
     *
     * @return Returns the initialized RekognitionClient.
     */
    public RekognitionClient getRekognitionClient() {
        return rekognitionClient;
    }

    /**
     * Detects text from the provided image using Amazon Rekognition.
     * The detected text is stored in a map with the image key as the identifier.
     *
     * @param rekognitionClient RekognitionClient instance.
     * @param image Image object to process.
     * @param imgKey The key (filename) of the image in the S3 bucket.
     * @param mp Map to store the image key and its detected text.
     */
    public void detectTextFromImage(RekognitionClient rekognitionClient, Image image, String imgKey, Map<String, String> mp) {
        // Create a request to detect text in the provided image
        DetectTextRequest textRequest = DetectTextRequest.builder()
                .image(image)
                .build();

        // Process the image and obtain the detected text
        DetectTextResponse textResponse = rekognitionClient.detectText(textRequest);
        List<TextDetection> textCollection = textResponse.textDetections();
        log.info("Detected text from image {}: {}", imgKey, textCollection.size());

        // Concatenate the detected text into a single string
        StringBuilder s = new StringBuilder();
        for (TextDetection text: textCollection) {
            s.append(text.detectedText()).append(" ");
        }

        // Store the image key and detected text in the provided map
        mp.put(imgKey, s.toString());
    }
}

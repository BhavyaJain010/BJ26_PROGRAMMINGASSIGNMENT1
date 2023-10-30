package com.pa1.carrecognitionapp.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Map;

@Slf4j
/**
 * Service class to handle interactions with Amazon SQS (Simple Queue Service).
 */
public class SQS_SERVICE_BJ26 {

    // AWS SDK SQS client instance
    private final SqsClient sqsClient;

    // The name of the SQS queue
    private static final String QUEUE_NAME = "Car.fifo";

    /**
     * Constructor initializes the SqsClient with the specified region.
     */
    public SQS_SERVICE_BJ26() {
        this.sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    /**
     * Getter method for the SqsClient.
     *
     * @return Returns the initialized SqsClient.
     */
    public SqsClient getSqsClient() {
        return sqsClient;
    }

    /**
     * Fetches the URL for the specified SQS queue. If the queue doesn't exist, it creates one.
     *
     * @param sqsClient SqsClient instance.
     * @return URL of the SQS queue.
     */
    public String getQueueUrl(SqsClient sqsClient) {
        String queueUrl;

        GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(QUEUE_NAME)
                .build();

        try {
            queueUrl = sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl();
        } catch (QueueDoesNotExistException e) {
            // If the queue doesn't exist, create one with the FIFO and ContentBasedDeduplication attributes
            CreateQueueRequest request = CreateQueueRequest.builder()
                    .attributesWithStrings(Map.of("FifoQueue", "true", "ContentBasedDeduplication", "true"))
                    .queueName(QUEUE_NAME)
                    .build();
            sqsClient.createQueue(request);

            // Fetch the queue URL again after creation
            GetQueueUrlRequest getURLQue = GetQueueUrlRequest.builder()
                    .queueName(QUEUE_NAME)
                    .build();
            queueUrl = sqsClient.getQueueUrl(getURLQue).queueUrl();
        }

        return queueUrl;
    }

    /**
     * Pushes a message with the image key to the specified SQS queue.
     *
     * @param sqsClient SqsClient instance.
     * @param imgKey Image key to be used as the message body.
     * @param queueUrl URL of the SQS queue.
     * @return true if the message was pushed successfully, false otherwise.
     */
    public boolean pushMessage(SqsClient sqsClient, String imgKey, String queueUrl) {
        try {
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageGroupId("CarText")
                    .messageBody(imgKey)
                    .build();
            String messageId = sqsClient.sendMessage(sendMsgRequest).sequenceNumber();
            log.info("Sequence number of the message: {}" , messageId);
            return true;
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            return false;
        }
    }
}

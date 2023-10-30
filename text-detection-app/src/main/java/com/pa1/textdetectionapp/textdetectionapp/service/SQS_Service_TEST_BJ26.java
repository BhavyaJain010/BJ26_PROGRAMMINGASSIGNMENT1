package com.pa1.textdetectionapp.textdetectionapp.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Map;

@Slf4j
/**
 * Service class to handle interactions with Amazon SQS (Simple Queue Service).
 */
public class SQS_Service_TEST_BJ26 {

    // AWS SDK SQS client instance
    private final SqsClient sqsClient;

    // The name of the SQS queue
    private static final String QUEUE_NAME = "Car.fifo";

    /**
     * Constructor initializes the SqsClient with the specified region.
     */
    public SQS_Service_TEST_BJ26() {
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
     * Receives a single message from the specified SQS queue.
     *
     * @param sqsClient SqsClient instance.
     * @param queueUrl URL of the SQS queue.
     * @return Message object if present, null otherwise.
     */
    public Message receiveMessage(SqsClient sqsClient, String queueUrl) throws InterruptedException {
        Message message = null;
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(1)
                    .build();

            try {
                message = sqsClient.receiveMessage(receiveMessageRequest).messages().get(0);
            } catch (IndexOutOfBoundsException e) {
                log.info("Queue is empty, waiting for the message.");
            }
            return message;
        } catch (Exception e) {
            // Log any exceptions that occur during message retrieval
            log.error("Error receiving message from SQS: {}", e.getMessage());
        }
        return message;
    }
}

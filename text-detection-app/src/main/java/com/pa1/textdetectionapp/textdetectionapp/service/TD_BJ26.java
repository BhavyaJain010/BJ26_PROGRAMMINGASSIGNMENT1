package com.pa1.textdetectionapp.textdetectionapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class TD_BJ26 {

	public static void main(String[] args) throws InterruptedException {
		// Bootstrap the Spring application
		SpringApplication.run(TD_BJ26.class, args);

		// Initialize services and required clients
		initializeServicesAndClients();
	}

	private static void initializeServicesAndClients() throws InterruptedException {
		boolean isQueueEnd = false;

		// Set up S3 storage service
		S3_SERVICE_BJ26 storageService = new S3_SERVICE_BJ26();

		// Commit: Set up SQS message service
		SQS_Service_TEST_BJ26 messageService = new SQS_Service_TEST_BJ26();
		SqsClient messageClient = messageService.getSqsClient();
		String messageQueueUrl = messageService.getQueueUrl(messageClient);

		// Set up text detection service
		TD_SERVICES_BJ26 textDetectService = new TD_SERVICES_BJ26();
		RekognitionClient imageDetectClient = textDetectService.getRekognitionClient();

		log.info("Queue URL: {}", messageQueueUrl);

		// Process SQS messages and detect text
		processMessages(storageService, messageService, messageClient, messageQueueUrl, textDetectService, imageDetectClient);
	}

	private static void processMessages(S3_SERVICE_BJ26 storageService, SQS_Service_TEST_BJ26 messageService, SqsClient messageClient,
										String messageQueueUrl, TD_SERVICES_BJ26 textDetectService, RekognitionClient imageDetectClient) throws InterruptedException {
		Map<String, String> detectedTextMap = new HashMap<>();
		while(true) {
			Message currentMessage = messageService.receiveMessage(messageClient, messageQueueUrl);
			if(currentMessage == null) {
				Thread.sleep(1000);
				continue;
			}

			// Delete message after processing
			deleteProcessedMessage(messageClient, messageQueueUrl, currentMessage);

			if(currentMessage.body().equals("-1")) {
				break;
			} else {
				// Fetch image and detect text
				Image fetchedImage = storageService.s3FetchByName(currentMessage.body());
				textDetectService.detectTextFromImage(imageDetectClient, fetchedImage, currentMessage.body(), detectedTextMap);
			}
		}

		// Write detected text to file
		writeToFile(detectedTextMap);
	}

	private static void deleteProcessedMessage(SqsClient messageClient, String messageQueueUrl, Message currentMessage) {
		DeleteMessageRequest removalRequest = DeleteMessageRequest.builder()
				.queueUrl(messageQueueUrl)
				.receiptHandle(currentMessage.receiptHandle())
				.build();
		messageClient.deleteMessage(removalRequest);
	}

	private static void writeToFile(Map<String, String> detectedTextMap) {
		FileWriteService fileWriterService = new FileWriteService();
		fileWriterService.fileWrite(detectedTextMap);
	}
}

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
		SpringApplication.run(TD_BJ26.class, args);

		boolean isQueueEnd = false;
		S3_SERVICE_BJ26 storageService = new S3_SERVICE_BJ26();

		SQS_Service_TEST_BJ26 messageService = new SQS_Service_TEST_BJ26();
		SqsClient messageClient = messageService.getSqsClient();
		String messageQueueUrl = messageService.getQueueUrl(messageClient);

		TD_SERVICES_BJ26 textDetectService = new TD_SERVICES_BJ26();
		RekognitionClient imageDetectClient = textDetectService.getRekognitionClient();

		log.info("Queue URL: {}", messageQueueUrl);
		Map<String, String> detectedTextMap = new HashMap<>();

		while(true) {
			Message currentMessage = messageService.receiveMessage(messageClient, messageQueueUrl);
			if(currentMessage == null) {
				Thread.sleep(1000);
				continue;
			}

			DeleteMessageRequest removalRequest = DeleteMessageRequest.builder()
					.queueUrl(messageQueueUrl)
					.receiptHandle(currentMessage.receiptHandle())
					.build();
			messageClient.deleteMessage(removalRequest);

			if(currentMessage.body().equals("-1")) {
				break;
			} else {
				Image fetchedImage = storageService.s3FetchByName(currentMessage.body());
				textDetectService.detectTextFromImage(imageDetectClient, fetchedImage, currentMessage.body(), detectedTextMap);
			}
		}

		FileWriteService fileWriterService = new FileWriteService();
		fileWriterService.fileWrite(detectedTextMap);
	}

}

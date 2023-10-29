package com.pa1.carrecognitionapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.List;

@SpringBootApplication
@Slf4j
public class CR_BJ26 {

	private static final String BUCKET_NAME = "njit-cs-643";

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(CR_BJ26.class, args);

		// Here I have added the recognition of the task
		processRecognitionTask();
	}

	private static void processRecognitionTask() {
		// Here I have fetch the data from S3
		List<S3Object> imageObjects = fetchS3ImageData();

		// Here I have handled the image recognition process
		handleImageRecognition(imageObjects);
	}

	private static List<S3Object> fetchS3ImageData() {
		S3_SERVICE_BJ26 dataStorage = new S3_SERVICE_BJ26();
		S3Client dataClient = dataStorage.getS3Client();

		return dataStorage.s3DataFetch(dataClient);
	}

	private static void handleImageRecognition(List<S3Object> imageObjects) {
		// Here I have done the initialization of SQS service
		SQS_SERVICE_BJ26 queueService = new SQS_SERVICE_BJ26();
		SqsClient queueClient = queueService.getSqsClient();
		String queueLocation = queueService.getQueueUrl(queueClient);

		// Here the initialization of Rekognition service is done
		RekognitionService_BJ26 carDetector = new RekognitionService_BJ26();
		RekognitionClient detectorClient = carDetector.getRekognitionClient();

		log.info("Current Queue URL: {}", queueLocation);

		// Here I have Processed the images using Rekognition
		for (S3Object imageObject : imageObjects) {
			processImageWithRekognition(carDetector, detectorClient, imageObject, queueService, queueClient, queueLocation);
		}

		// This is to show that the signal end of queue processing id done
		signalEndOfQueue(queueService, queueClient, queueLocation);
	}

	private static void processImageWithRekognition(RekognitionService_BJ26 carDetector, RekognitionClient detectorClient,
													S3Object imageObject, SQS_SERVICE_BJ26 queueService,
													SqsClient queueClient, String queueLocation) {

		// Here the Recognition of image for car label is there
		if (carDetector.recognize(detectorClient, imageObject, BUCKET_NAME)) {
			log.info("Image '{}' has been identified with a car label.", imageObject.key());

			// Here is the queries for sending messages to SQS for recognized image
			if (queueService.pushMessage(queueClient, imageObject.key(), queueLocation)) {
				log.info("Successfully sent message for image: '{}'", imageObject.key());
			} else {
				log.info("Failed to send message for image: '{}'", imageObject.key());
			}

		} else {
			log.info("Image '{}' was not identified with a car label.", imageObject.key());
		}
	}

	private static void signalEndOfQueue(SQS_SERVICE_BJ26 queueService, SqsClient queueClient, String queueLocation) {
		queueService.pushMessage(queueClient, "-1", queueLocation);
		log.info("Queue processing completed: -1");
	}
}

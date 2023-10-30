Programming Assignment  1
File Structure: - To Search Java Files in the project
Java Files for car-recognition-app: - car-recognition-app/src/main/java/com.pa1.car-recognition-app.service/
Java Files for text-recognition-app: - text-recognition-app/src/main/java/com.pa1.textdectionapp.textdectionapp.service
For .Jar Files
.Jar Files for car-recognition-app: - car-recognition-app/.mvn/wrapper/
.Jar Files for text-recognition-app: - text-recognition-app/.mvn/wrapper/

Architectural Diagram
![Architectural Diagram](https://github.com/BhavyaJain010/BJ26_PROGRAMMINGASSIGNMENT1/assets/138629017/64c77828-06e7-4370-ae29-4a89b2fa054a)

Here are the Services Which I have used in AWS: -
AWS S3
AWS EC2 Instance
AWS SQS Messaging Service
AWS Rekognition

Now I will guide step by step the process of stepping up the system:
1. AWS Learner's Lab Setup
2. AWS Command Line Interface setup
3. IAM Setup
4. AWS EC2 instance configuration
5. SSH access to EC2 instances
6. AWS SQS configuration
7. Image recognition Java application
8. Java Programming for Text Detection 
9. Java application deployment on EC2 instances

Steps Performed: -
1. AWS Learner's Lab Setup: - 
Get the Access for AWS student Developer Pack
Then access the lab go to modules/Launch AWS Academy Learner Lab/start lab
Then the lab session will supply AWS credentials and indicate the residual student credits.
2. AWS Command Line Interface setup: -
Download AWS CLI from this link: - https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
After installing configure it using aws "configure" command
Go to AWS learners console page click on AWS details their you will see security key access key and session token just past then in the aws configue command
Then we can also create multiple pages using a command "export AWS_PROFILE=<profile_name>"
3. IAM Setup: -
Start by navigating in aws to IAM 
Then click Roles
Then click LabRole
Now just make sure the role encompasses essential policies like AmazonRekognitionFullAccess, AmazonS3FullAccess, and AmazonSQSFullAccess.
4. AWS EC2 instance configuration: -
Start by navigating in aws to Services -> EC2
Click on Instances 
Then Launch Instances
Then Label the instance and choose the AMI as "Amazon Linux 2 Kernel 5.10 AMI 2.0.20230307.0 x86_64 HVM gp2" with a 64-bit x86 architecture.
Click on the t2.micro as instance type.
click vockey as keypair.
Permit SSH, HTTP, and HTTPS traffic by adjusting the security group settings. Next, pick "MyIP" to exclusively allow your IP address access to the EC2 instance via SSH, HTTP, or HTTPS.
Now click on 8 Gib with general purpose SSD as a storage configuration.
The just click on launch instance.
5. SSH access to EC2 instances: -
Download the SSH key from instances
Then change the permissions
then use this code "ssh -i labsuser.pem ec2-user@<EC2_IP>" to launch instance powershell
It will show error java not found
To solve error we have to download java
Download JDK: - wget https://download.oracle.com/java/19/archive/jdk-19.0.1_linux-x64_bin.tar.gz
Extract Files: - tar -xvf jdk-19.0.1_linux-x64_bin.tar.gz
Move to extracted JDK: -sudo mv jdk-19.0.1 /usr/local/
Update environment variables using this commands: -
i.   sudo touch /etc/profile.d/oraclejdk.sh
ii.  sudo chmod +x /etc/profile.d/oraclejdk.sh
iii. sudo vim /etc/profile.d/oraclejdk.sh
Export Files:- export JAVA_HOME=/usr/local/jdk-19.0.1 and export PATH=$JAVA_HOME/bin:$PATH
Check version to make sure it is installed on version: - java --version
6. AWS SQS configuration: -
Open the AWS Management Console in your web browser.
Navigate to the "Services" dropdown and select "SQS" under the "Application Integration" section.
You'll be taken to the SQS dashboard.
Click Create Queue.
Select queue type and provide the queue name. In my case the file name is, Car.fifo
then put the configuration details for SQS.
Now select encryption and access policy. It can be assigned in the LabRole also.
7. Image recognition Java application: -
Retrieve all images from the S3 bucket.
Identify image labels and their confidence scores using the AWS Rekognition service.
Mark images labeled as "Car" that have a confidence score exceeding 90%.
Add the names of these identified images to the AWS SQS message queue.
Conclude by sending a "-1" message as the final message in the sequence.
Compile the JAR file, readying it for deployment.
8. Java Programming for Text Detection: -
Retrieve messages sequentially from the AWS SQS queue.
Utilize AWS Rekognition's text detection service to identify the text within images.
If the queue has no messages, the system pauses, awaiting new ones.
Images containing detected text are recorded in the "ImageText.txt" file alongside their corresponding indexes.
Assemble the JAR file to make it ready for deployment.
9. Java application deployment on EC2 instances: -
Use SSH to connect to EC2 instances through their individual IP addresses.
Set up AWS configurations with the `aws configure` command.
Download openjdk-java-19.0.1 by running: `wget https://download.oracle.com/java/19/archive/jdk-19.0.1_linux-x64_bin.tar.gz`.
Unzip the downloaded files using: `tar zxvf jdk-19.0.1_linux-x64_bin.tar.gz`.
Relocate the extracted file with: `sudo mv jdk-19.0.1 /usr/share`.
Edit the `/etc/profile` file by launching `sudo vim /etc/profile` and switch to insert mode. Save any modifications with `:wq`.
In the `/etc/profile` file, append the following lines:
    - `export PATH=$JAVA_HOME/bin:$PATH`
    - `export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar`.
Verify the installation by checking the Java version.
Transfer the `.jar` file to the EC2 instance using a tool like Cyberduck.
Start the car recognition application with: `java -jar car-recognition-app-0.0.1-SNAPSHOT.jar`. 
Simultaneously, initiate the text detection application with: `java -jar text-detection-app-0.0.1-SNAPSHOT.jar`.
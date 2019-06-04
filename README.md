# Batch application

This is a batch application, it will run every X seconds to process/convert audio files from sfx, 3gp, flat, etc (extensions) to mp3 and next, save them into the file system. After the audio has been processed, sends a confirmation email.

## Version 1

* Built on Java 8 using Spring framework.
* Uses local storage to store audios.
* Uses a relational database.
* Uses a cron expression to run every X seconds, depends on you want.
* Uses Jave-core component to process/convert audios.
* Uses the AWS API to send emails through AWS SES.

## Assumptions

This application was created using:

* Java 8
* Maven 3
* Spring Boot
* Ubuntu 18.4 

## Prerequisites

1) Install Java, follow this [link](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-on-ubuntu-18-04).
2) Install Maven, follow this [link](https://linuxize.com/post/how-to-install-apache-maven-on-ubuntu-18-04/).
3) Install Git, follow this [link](https://www.liquidweb.com/kb/install-git-ubuntu-16-04-lts/)

## Instructions

1) Configure these environment variables:

|Environment variable|Example|Description|
|-|-|-|
|`CONTESTS_DATASOURCE_URL`|jdbc:mysql://localhost:3306/contests|The database connection|
|`CONTESTS_DATASOURCE_USERNAME`|root|The database user|
|`CONTESTS_DATASOURCE_PASSWORD`|12345|The database password|
|`CONTESTS_PATH_CONVERTED_FILES`|/home/audio/converted/|The path where *CONVERTED* audio files will be stored|
|`CONTESTS_MAIL_NOTIFICATION`|javax or AWS-SES|The service used to send emails|
|`CONTESTS_FILE_STORE`|file-system or AWS-S3|The service used to store and retrieve audios|
|`CONTESTS_CRON`|*/60 * * * * *|Runs every 60 seconds. This is the cron by default|
|`AWS_ACCESS_KEY_ID`|-|The AWS credentials|
|`AWS_SECRET_ACCESS_KEY`|-|The AWS credentials|

2) Go to `batch` folder and execute:

```bash
mvn clean install
java -jar ./target/batch-1.0.jar
```

### Cron expressions

Examples:

|Expression|Description|
|-|-|
|`*/30 * * * * *`|Runs every 30 seconds|
|`*/60 * * * * *`|Runs every 60 seconds|
|`0 0,4,8,12,16,20,24,28,32,36,40,44,48,52,56 * * * *`|Runs every 4 minutes, starting at minute **0**|
|`0 1,5,9,13,17,21,25,29,33,37,41,45,49,53,57 * * * *`|Runs every 4 minutes, starting at minute **1**|
|`0 2,6,10,14,18,22,26,30,34,38,42,46,50,54,58 * * * *`|Runs every 4 minutes, starting at minute **2**|
|`0 3,7,11,15,19,23,27,31,35,39,43,47,51,55,59 * * * *`|Runs every 4 minutes, starting at minute **3**|

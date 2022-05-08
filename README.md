# sports-tracker-export
Download your activities from Sports Tracker in one shot

## Prerequisites
Maven and Java 18 installed.

## Getting your authorization token
1. Sign in to Sports Tracker in a browser
2. Go to Diary
3. Open the developer tools of the browser
4. Refresh
5. Search for workouts?limited=true&limit=1000000 request
6. Copy the value of the STTAuthorization request header

## Downloading your activities
1. Create a JAR from the project (mvn clean package)
2. Start the jar by providing the download directory: java -jar target/sports-tracker-export-1.0-SNAPSHOT-jar-with-dependencies.jar $YOUR_DOWNLOAD_DIRECTORY
3. Enter the authorization token

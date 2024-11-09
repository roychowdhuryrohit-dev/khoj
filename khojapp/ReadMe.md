Khoj Application Backend

1) Clone the repository locally 
2) Export these AWS variables in your local environment
   - accessKey: YOUR_AWS_ACCESS_KEY_ID 
   - secretAccessKey: YOUR_AWS_SECRET_ACCESS_KEY
3) To run the backend application, run the following command in the terminal
   - ./gradlew build
   - AWS_ACCESS_KEY_ID=your-access-key AWS_SECRET_ACCESS_KEY=your-secret-key ./gradlew bootRun
   - The application will start on port 8080
   - Additionally to create and run a jar file
     - java -jar build/libs/khoj.jar --AWS_ACCESS_KEY_ID=your-access-key --AWS_SECRET_ACCESS_KEY=your-secret-key
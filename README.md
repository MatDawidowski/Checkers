```
Checkers game

Features:

- Play a local game with another player
- Play online by connecting via the opponent's IP address
- Play against the computer AI

Technologies:

-Java 21
-JavaFx 21

How to Run the Project (Windows):
  Install Maven (if not already installed):
    - Download Maven from:
      https://maven.apache.org/download.cgi
      (choose the binary zip archive)
    - Extract the archive
    - Copy the path to the bin folder inside the extracted directory
    - add the maven to system variables
      - go to "Edit the system environment variables"
      - go to "Environment variables"
      - on "System variables" section, press 2 times on path
      - press on a empty row and paste the copied path
      - clik ok and close
  Run the application:
    - Open your terminal in any folder and run:
      - git clone https://github.com/MatDawidowski/Checkers.git
      - cd Checkers
      - run client:
         - mvn clean javafx:run
      - run server:
         - mvn clean compile
         - java -cp target/classes com.example.hellofx.Server

How to RUn the Project (Linux):
  Install Maven (if not already installed):
    - open terminal and write "sudo apt install maven"
   Run the application:
    - Open your terminal in any folder and run:
      - git clone https://github.com/MatDawidowski/Checkers.git
      - cd Checkers
      - run client:
         - mvn clean javafx:run
      - run server:
         - mvn clean compile
         - java -cp target/classes com.example.hellofx.Server

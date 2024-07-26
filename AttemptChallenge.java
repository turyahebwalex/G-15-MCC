package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;

public class AttemptChallenge {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/nationschools";
    private static final String USER = "root";
    private static final String PASS = "";
    private int scorefinal;
    private int attempts;
    private String status;
    private String username;
    private String title;
    private static final int QUESTION_TIME_LIMIT = 10; // time limit per question in minutes

     void handleChallenge(String challengeTitle,String user) throws ClassNotFoundException {
         MrChallenge obj = new MrChallenge();
       Scanner scanner= new Scanner(System.in);
        Class.forName("com.mysql.cj.jdbc.Driver");
this.title=challengeTitle;
        this.username=user;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            if (isChallengeActive(conn, challengeTitle)) {
                boolean wantToRetake = true;
                this.attempts = 0;
                int bestScore = 0;
                List<String> bestQuestions = new ArrayList<>();
                List<String> bestAnswers = new ArrayList<>();
                List<Long> bestTimesTaken = new ArrayList<>();
                long bestTotalTimeTaken = 0;

                while (wantToRetake && attempts < 3) {
                    attempts++;
                    System.out.println("\nAttempt " + attempts);
                    List<String> questions = getRandomQuestions(conn, challengeTitle, 10);
                    List<String> answers = new ArrayList<>();
                    List<Long> timesTaken = new ArrayList<>();
                    long totalTimeTaken = 0;
                    int score = takeTest(conn, challengeTitle, scanner, questions, answers, timesTaken, totalTimeTaken);

                    if (score > bestScore) {
                        bestScore = score;
                        bestQuestions = new ArrayList<>(questions);
                        bestAnswers = new ArrayList<>(answers);
                        bestTimesTaken = new ArrayList<>(timesTaken);
                        bestTotalTimeTaken = totalTimeTaken;
                    }

                    if (attempts < 3) {
                        System.out.print("Do you want to retake the test? (yes/no): ");
                        String response = scanner.nextLine().trim().toLowerCase();
                        wantToRetake = response.equals("yes");
                    } else {
                        wantToRetake = false;
                    }
                }

                System.out.println("\nYour best score is: " + bestScore);
                displayReport(bestQuestions, bestAnswers, bestTimesTaken, bestTotalTimeTaken, bestScore);
                String pdfpath = "Challenge_Report.pdf";
                obj.generatePDFReport(challengeTitle,bestQuestions,bestAnswers,bestTimesTaken,bestTotalTimeTaken,bestScore,bestAnswers,pdfpath);
                String email = obj.getParticipantEmail(username);
                obj.sendEmailWithAttachment(email,pdfpath);
            } else {
                System.out.println("The challenge is not active.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private  int takeTest(Connection conn, String challengeTitle, Scanner scanner, List<String> questions, List<String> answers, List<Long> timesTaken, long totalTimeTaken) throws SQLException {
        int totalQuestions = questions.size();
        int questionNumber = 1;

        for (String question : questions) {
            System.out.println("\nQuestion " + questionNumber + " of " + totalQuestions);
            if (questionNumber > 1) {
                System.out.println("Total time taken so far: " + formatTime(totalTimeTaken));
            }

            long startTime = System.currentTimeMillis();
            String answer = promptQuestion(question, scanner, totalQuestions - questionNumber + 1);
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            totalTimeTaken += timeTaken;
            timesTaken.add(timeTaken);

            if (answer == null) {
                System.out.println("Time is up! The quiz is now closed.");
                this.status="incomplete";
                break;
            }
            answers.add(answer);
            questionNumber++;
        }

        return calculateScore(conn, challengeTitle, questions, answers);
    }

    private static boolean isChallengeActive(Connection conn, String challengeTitle) throws SQLException {
        String query = "SELECT is_active FROM challenges WHERE title = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, challengeTitle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_active");
            }
        }
        return false;
    }

    private static List<String> getRandomQuestions(Connection conn, String challengeTitle, int limit) throws SQLException {
        List<String> questions = new ArrayList<>();
        String query = "SELECT question FROM questionsmore WHERE challenge_title = ? ORDER BY RAND() LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, challengeTitle);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(rs.getString("question"));
            }
        }
        return questions;
    }

     String promptQuestion(String question, Scanner scanner, int remainingQuestions) {
        System.out.println("Questions remaining: " + remainingQuestions);
        System.out.println(question);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("\nTime is up for this question!");
                System.exit(0);
            }
        };

        timer.schedule(task, QUESTION_TIME_LIMIT * 60 * 1000); // set timer for the question

        System.out.print("Your answer: ");
        String answer = scanner.nextLine();

        timer.cancel(); // cancel the timer if the user answers within the time limit
         this.status="complete";
        System.out.println("Your answer is: " + answer + ". Press Enter to continue...");
        scanner.nextLine(); // wait for the user to press Enter to continue

        return answer;
    }

    private static int calculateScore(Connection conn, String challengeTitle, List<String> questions, List<String> userAnswers) throws SQLException {
        int score = 0;
        String query = "SELECT a.answer FROM questionsmore q JOIN answers a ON q.question_id = a.question_id WHERE q.challenge_title = ? AND q.question = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < questions.size(); i++) {
                stmt.setString(1, challengeTitle);
                stmt.setString(2, questions.get(i));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String correctAnswer = rs.getString("answer");
                    String userAnswer = userAnswers.get(i);

                    if (userAnswer.equals("-")) {
                        // User skipped the question
                        score += 0;
                    } else if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                        // Correct answer
                        score += 5;
                    } else {
                        // Wrong answer
                        score -= 3;
                    }
                }
            }
        }

        // Ensure the score doesn't go below 0
        return Math.max(score, 0);
    }

    private static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void displayReport(List<String> questions, List<String> userAnswers, List<Long> timesTaken, long totalTimeTaken, int finalScore) throws SQLException, ClassNotFoundException {
        System.out.println("\n----- Challenge Report -----");
        for (int i = 0; i < questions.size(); i++) {
            String question = questions.get(i);
            String answer = userAnswers.get(i);
            long timeTaken = timesTaken.get(i);

            System.out.println("Question " + (i + 1) + ": " + question);
            System.out.println("Your answer: " + answer);
            System.out.println("Time taken: " + formatTime(timeTaken));
            System.out.println();
        }
        System.out.println("Total time taken: " + formatTime(totalTimeTaken));
        System.out.println("Your final score: " + finalScore);
        System.out.println("Percentage: " + (finalScore * 2) + "%");
        System.out.println("----------------------------");
        this.scorefinal=finalScore*2;
        records();

    }


    //Method stores records after a participant finishes a challenge
    private void records() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
       String user1=this.username;
        JSONObject result=new JSONObject();
        result.put("score",this.scorefinal);
        result.put("attempt",this.attempts);
        result.put("status",this.status);


        // result.put("status",this.status);
        String sql ="UPDATE challenge_records SET"+ " " +this.title+ " " +"= ? WHERE username=?";
PreparedStatement statement=conn.prepareStatement(sql);

 statement.setObject(1,result.toString());
 statement.setString(2,user1);
        statement.executeUpdate();


        // Method to get participant's email from the database

    }
}

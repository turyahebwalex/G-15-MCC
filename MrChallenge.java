package src;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class MrChallenge {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/nationschools";
    private static final String USER = "root";
    private static final String PASS = "";
    private static final int QUESTION_TIME_LIMIT = 10; // time limit per question in minutes

    public static void attemptChallenge(String challengeTitle, String schoolName, String regNo, String userName, String userEmail) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            if (isChallengeActive(conn, challengeTitle)) {
                boolean wantToRetake = true;
                int attempts = 0;
                int bestScore = 0;
                List<String> bestQuestions = new ArrayList<>();
                List<String> bestAnswers = new ArrayList<>();
                List<Long> bestTimesTaken = new ArrayList<>();
                List<String> bestCorrectAnswers = new ArrayList<>();
                long bestTotalTimeTaken = 0;

                while (wantToRetake && attempts < 3) {
                    attempts++;
                    System.out.println("\nAttempt " + attempts);
                    List<String> questions = getRandomQuestions(conn, challengeTitle, 10);
                    List<String> answers = new ArrayList<>();
                    List<Long> timesTaken = new ArrayList<>();
                    List<String> correctAnswers = new ArrayList<>();
                    long totalTimeTaken = 0;
                    int score = takeTest(conn, challengeTitle, scanner, questions, answers, timesTaken, totalTimeTaken, correctAnswers);

                    if (score > bestScore) {
                        bestScore = score;
                        bestQuestions = new ArrayList<>(questions);
                        bestAnswers = new ArrayList<>(answers);
                        bestTimesTaken = new ArrayList<>(timesTaken);
                        bestCorrectAnswers = new ArrayList<>(correctAnswers);
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
                String pdfPath = "Challenge_Report.pdf";
                generatePDFReport(challengeTitle, bestQuestions, bestAnswers, bestTimesTaken, bestTotalTimeTaken, bestScore, bestCorrectAnswers, pdfPath);
//                sendEmailWithAttachment(userEmail, pdfPath);
            } else {
                System.out.println("The challenge is not active.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int takeTest(Connection conn, String challengeTitle, Scanner scanner, List<String> questions, List<String> answers, List<Long> timesTaken, long totalTimeTaken, List<String> correctAnswers) throws SQLException {
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
                break;
            }
            answers.add(answer);
            questionNumber++;
        }

        return calculateScore(conn, challengeTitle, questions, answers, correctAnswers);
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

    private static String promptQuestion(String question, Scanner scanner, int remainingQuestions) {
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
        System.out.println("Your answer is: " + answer + ". Press Enter to continue...");
        scanner.nextLine(); // wait for the user to press Enter to continue

        return answer;
    }

    private static int calculateScore(Connection conn, String challengeTitle, List<String> questions, List<String> userAnswers, List<String> correctAnswers) throws SQLException {
        int score = 0;
        String query = "SELECT a.answer FROM questionsmore q JOIN answers a ON q.question_id = a.question_id WHERE q.challenge_title = ? AND q.question = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < questions.size(); i++) {
                stmt.setString(1, challengeTitle);
                stmt.setString(2, questions.get(i));
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String correctAnswer = rs.getString("answer");
                    correctAnswers.add(correctAnswer);
                    String userAnswer = userAnswers.get(i);

                    if (userAnswer.equals("-")) {
                        score += 0; // User skipped the question
                    } else if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                        score += 5; // Correct answer
                    } else {
                        score -= 3; // Wrong answer
                    }
                }
            }
        }

        int maxScore = 10 * 5; // 10 questions, each worth 5 points
        return Math.max(score, 0) * 100 / maxScore; // Ensure the score doesn't go below 0
    }

    private static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private static void displayReport(List<String> questions, List<String> userAnswers, List<Long> timesTaken, long totalTimeTaken, int finalScore) {
        System.out.println("\n----- Challenge Report -----");
        for (int i = 0; i < questions.size(); i++) {
            System.out.println("Question " + (i + 1) + ": " + questions.get(i));
            System.out.println("Your answer: " + userAnswers.get(i));
            System.out.println("Time taken: " + formatTime(timesTaken.get(i)));
            System.out.println();
        }
        System.out.println("Total time taken: " + formatTime(totalTimeTaken));
        System.out.println("Your final score: " + finalScore + " out of 100");
        System.out.println("Percentage: " + finalScore + "%");
        System.out.println("----------------------------");
    }

    public static void generatePDFReport(String challengeTitle, List<String> questions, List<String> userAnswers, List<Long> timesTaken, long totalTimeTaken, int finalScore, List<String> correctAnswers, String pdfPath) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            PDType0Font font = PDType0Font.load(document, new File("C:/Windows/Fonts/Arial.ttf"));
            contentStream.setFont(font, 12);

            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Challenge Report");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Challenge: " + challengeTitle);
            contentStream.newLineAtOffset(0, -20);

            for (int i = 0; i < questions.size(); i++) {
                contentStream.showText("Question " + (i + 1) + ": " + questions.get(i));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Your answer: " + userAnswers.get(i));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Correct answer: " + correctAnswers.get(i));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Time taken: " + formatTime(timesTaken.get(i)));
                contentStream.newLineAtOffset(0, -40);
            }

            contentStream.showText("Total time taken: " + formatTime(totalTimeTaken));
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Your final score: " + finalScore + " out of 100");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Percentage: " + finalScore + "%");

            contentStream.endText();
            contentStream.close();

            document.save(pdfPath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendEmailWithAttachment(String to, String pdfPath) {
        String from = "turyahebwalex56@gmail.com";
        String host = "smtp.gmail.com";
        final String username = "turyahebwalex56@gmail.com";
        final String password = "vrcw htzw nold vcbj";

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");


        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Challenge Report");

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Please find the attached PDF report of your challenge.");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(pdfPath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("Challenge_Report.pdf");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the challenge title:");
        String challengeTitle = scanner.nextLine();
        System.out.println("Enter the school name:");
        String schoolName = scanner.nextLine();
        System.out.println("Enter the registration number:");
        String regNo = scanner.nextLine();
        System.out.println("Enter your name:");
        String userName = scanner.nextLine();
        System.out.println("Enter your email address:");
        String userEmail = scanner.nextLine();

        attemptChallenge(challengeTitle, schoolName, regNo, userName, userEmail);
    }
    public String getParticipantEmail(String username) throws SQLException, ClassNotFoundException {
        String email = null;
        String sql = "SELECT email FROM participant WHERE username=?";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools", "root", "");
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                email = resultSet.getString("email");
            }
        }
        return email;
    }

}

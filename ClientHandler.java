package src;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;


public class ClientHandler extends Thread {
       private Socket socket;
      private PrintWriter output;

       public ClientHandler(Socket socket )throws java.io.IOException{
           this.socket=socket;
       }
       @Override
       public void run() {
           try (
                   BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                  PrintWriter  output = new PrintWriter(socket.getOutputStream(), true)
           ) {
               String in;
               while ((in=input.readLine())!=null){

               splitter(in, socket);}
           } catch (IOException | ClassNotFoundException | SQLException | MessagingException e) {
               e.printStackTrace();
           }
       }



        //method 3
       private  void applicants(String username,String firstname,String lastname,String emailAddress
        ,String date_of_birth,String school_reg_no,String image_path)throws java.sql.SQLException,ClassNotFoundException{

            String query ="INSERT INTO applicants (username,firstname,lastname,email,date_of_birth,registrationNumber,image)"+
                    " VALUES(?,?,?,?,?,?,?)";
            String sql="SELECT * FROM applicants;";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools","root","");

            PreparedStatement statement =connection.prepareStatement(query);
                 statement.setString(1,username);
                statement.setString(2,firstname);
                statement.setString(3,lastname);
                statement.setString(4,emailAddress);
                statement.setString( 5,date_of_birth);
                statement.setString(6,school_reg_no);
                statement.setString(7,image_path);


            statement.executeUpdate();
            ResultSet resultSet=statement.executeQuery(sql); }

//method 2
    //this code adds registration details to the database applicants table  and also verifies login
        private  void splitter(String out,Socket socket)throws java.sql.SQLException,ClassNotFoundException,javax.mail.MessagingException
        ,java.io.IOException{
            output= new PrintWriter(socket.getOutputStream(), true);
           String [] str=out.split(" ");
           if (str.length==8){

            String username=str[1];
            String firstname=str[2];
            String lastname=str[3];
            String email=str[4];
            String dob=str[5];
            String reg_no=str[6];
            String image_path=str[7];
            boolean checker=DBcheck(username,email);
            boolean checker2=DBcheck2(username,email);
             if(checker){

                 output.println("You can not register under this school.");
             } else if (checker2) {

                 output.println("Your application is still pending..");

             }
             else{


               applicants(username,firstname,lastname,email,dob,reg_no,image_path);
               System.out.println("New Applicant added...");
               sendEmail(email,firstname,lastname);

               output.println("Details submitted !...It might take sometime to confirm your registration..");}
           }
           else if(str.length==3 && "New".equals(str[0])){
               String Username=str[1];
               String password=str[2];
               boolean bool;
             try{  bool=setPassword(Username,password);
                 if(bool){
                     output.println("true");
                 }
                 else {
                     output.println("false");
                 }
                 }
             catch (java.io.IOException e){
                 System.out.println("java.io.IOException");
             }

           }
            else if("viewApplicants".equals(str[0])){
               String email=str[1];
              viewApplicants(email);
            }
           else if(str.length==3&&"Login".equals(str[0])){

               String email=str[1];
               String password=str[2];

               //Check for details in database
               String sql="SELECT * FROM representatives WHERE email=? AND password=?";
               Class.forName("com.mysql.cj.jdbc.Driver");

               Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools","root","");
               PreparedStatement statement =connection.prepareStatement(sql);
               statement.setString(1,email);
               statement.setString(2,password);
               ResultSet resultSet=statement.executeQuery();
               if(resultSet.next()){
                   output=new PrintWriter(socket.getOutputStream(),true);
                   output.println("true");

               }
               else {
                   output=new PrintWriter(socket.getOutputStream(),true);
                   output.println("false");
               }}
            else if ("viewChallenges".equals(str[0])){
                viewChallenges();
           }

           else if("confirm".equals(str[0])){
               confirmApplicants(out);}

           else{

                   String Username=str[0];
                   String password=str[1];
                   try{ boolean out1= participantLogin(Username,password);
                   if(out1){
                       output=new PrintWriter(socket.getOutputStream(),true);
                       output.println("true"); }
                   else{
                       output=new PrintWriter(socket.getOutputStream(),true);
                       output.println("false");
                   }}
                   catch (java.io.IOException|java.lang.ClassNotFoundException|java.sql.SQLException e){
                       System.out.println("java.io.IOException");
                   }


        }}

    //Method sends email to an applicant about status of their application
        private void sendEmail(String email,String firstname,String lastname)throws javax.mail.MessagingException,java.sql.SQLException,java.lang.ClassNotFoundException{
            String sql="SELECT email FROM  representatives WHERE registrationNumber=(SELECT registrationNumber FROM applicants WHERE email=?)";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools","root","");
            PreparedStatement statement =connection.prepareStatement(sql);
            statement.setString(1,email);
            ResultSet resultSet=statement.executeQuery();
           while(resultSet.next()){ String email2=resultSet.getString("email");


            Properties properties=new Properties();
            properties.put("mail.smtp.auth",true);
            properties.put("mail.smtp.host","smtp.gmail.com");
            properties.put("mail.smtp.port","587");
            properties.put("mail.smtp.starttls.enable",true);
            properties.put("mail.smtp.starttls.required",true);
           properties.put("mail.smtp.ssl.protocols","TLSv1.2");
            properties.put("mail.smtp.ssl.trust","smtp.gmail.com");
            Session session=Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("hermanssenoga@gmail.com","hgqs xvjm rfxl plbd");
                }
            });

            Message message=new MimeMessage(session);
            message.setSubject("New Participant registration");
            message.setText(firstname+ " " +lastname+ " "+"Has applied for the Math Competition.");
            Address Address1=new InternetAddress(email2);

            message.setRecipient(Message.RecipientType.TO,Address1);
            Transport.send(message);
            System.out.println("mail sent!");}
        }



        //Method 4 ..verifies  participant login
        private  boolean participantLogin(String username,String password)throws java.sql.SQLException,
                java.lang.ClassNotFoundException,java.io.IOException {

            String sql="SELECT * FROM participant WHERE username=? AND password=?";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools","root","");

            PreparedStatement statement =connection.prepareStatement(sql);
            statement.setString(1,username);
            statement.setString(2,password);
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){

               return true;
            }
            else {
               return false;
            }

        }

   //method adds a new password for new applicants ..it should check that the password is null in the participants table
    private  boolean setPassword(String username,String password)throws java.sql.SQLException,
            java.lang.ClassNotFoundException,java.io.IOException {
        PreparedStatement statement;

            String sql="SELECT * FROM participant WHERE username=? ";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools","root","");

             statement =connection.prepareStatement(sql);
            statement.setString(1,username);
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){
                String sql1 = "UPDATE participant SET password=? WHERE username=?";
    Class.forName("com.mysql.cj.jdbc.Driver");
   connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools", "root", "");
   statement = connection.prepareStatement(sql1);
    statement.setString(1, password);
    statement.setString(2, username);
     statement.executeUpdate();
                System.out.println("New password added ");
     return true;
     }
            else{
                return false;

            }

}

//Method sends Applicants' details to respective school Representative
private void viewApplicants(String email) throws SQLException, ClassNotFoundException,java.io.IOException {
    String sql = "SELECT * FROM applicants WHERE registrationNumber=(SELECT registrationNumber FROM representatives WHERE email=?)";
    Class.forName("com.mysql.cj.jdbc.Driver");
output=new PrintWriter(socket.getOutputStream(),true);
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools", "root", "");
         PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();


while(resultSet.next()){
                String user = resultSet.getString("username");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String emailAddress = resultSet.getString("email");
                String dateOfBirth = resultSet.getString("date_of_birth");
                String regNo = resultSet.getString("registrationNumber");

                String row = "Username: " + user + " | First Name: " + firstName + " | lastname: " + lastName +
                        " | Email: " + emailAddress + " | Date of Birth: " + dateOfBirth + " | RegNo: " + regNo;

                output.println(row);} // Send each row to the client

            output.println("END"); // Signal end of data transmission

    }


        //Method adds Applicants to Participants table after confirmation by Rep and transfers those rejected to Rejected table
    private void confirmApplicants(String input) throws java.lang.ClassNotFoundException, java.sql.SQLException, javax.mail.MessagingException, IOException {
            String [] in=input.split(" ");
            String username=in[2];
            if("yes".equals(in[1])){
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools", "root", "");

                String sql2= "SELECT * FROM applicants WHERE username=?";
                PreparedStatement statement2 = connection.prepareStatement(sql2);
                statement2.setString(1, username);
                ResultSet resultSet2 = statement2.executeQuery();
                while(resultSet2.next()){
                    String user = resultSet2.getString("username");
                    String firstName = resultSet2.getString("firstName");
                    String lastName = resultSet2.getString("lastName");
                    String emailAddress = resultSet2.getString("email");
                    String dateOfBirth = resultSet2.getString("date_of_birth");
                    String regNo = resultSet2.getString("registrationNumber");
                    String path = resultSet2.getString("image");

                    String imagePath=saverUserImage(path,username);

                    String sql= "INSERT INTO participant (username,firstname,lastname,email,date_" +
                            "of_birth,registrationNumber,image) VALUES(?,?,?,?,?,?,?)";
                    PreparedStatement statement3 = connection.prepareStatement(sql);
                    statement3.setString(1, username);
                    statement3.setString(2, firstName);
                    statement3.setString(3, lastName);
                    statement3.setString(4, emailAddress);
                    statement3.setString(5, dateOfBirth);
                    statement3.setString(6, regNo);
                    statement3.setString(7, imagePath);

                    statement3.executeUpdate();

                    String out="Hello"+ " " +username+ " " +"Your application for the math competition has been confirmed.Please access your account and set" +
                            " your password";
                    confirmationEmail(emailAddress,out);
                    output=new PrintWriter(socket.getOutputStream(),true);
                    output.println("User confirmed successfully!");}
            }

            else{
                String sql = "INSERT INTO rejected (username,firstname,lastname,email," +
                    "date_of_birth,registrationNumber,image) SELECT username,firstname,lastname,email," +
                    "date_of_birth,registrationNumber,image FROM applicants WHERE username=?";
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools", "root", "");

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);

                statement.executeUpdate();
                String sql2 = "SELECT email FROM rejected WHERE username=?";
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools", "root", "");

                PreparedStatement statement2 = connection.prepareStatement(sql2);
                statement2.setString(1, username);
                ResultSet resultSet= statement2.executeQuery();
                while(resultSet.next()){
                    String email= resultSet.getString("email");
                    String out="Sorry but your Application for the Math competition has been rejected";
                    confirmationEmail(email,out);
                    System.out.println("Mail sent!");
                }
                output=new PrintWriter(socket.getOutputStream(),true);
                output.println("Operation successfull!");
                }
        String sql = "DELETE FROM applicants WHERE username=?";
        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools", "root", "");

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);

        statement.executeUpdate();
        }

    //Method sends email to Applicants after Representative confirms/rejects their application
    public static void confirmationEmail(String email,String out)throws javax.mail.MessagingException{
        Properties properties=new Properties();
        properties.put("mail.smtp.auth",true);
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");
        properties.put("mail.smtp.starttls.enable",true);
        properties.put("mail.smtp.starttls.required",true);
        properties.put("mail.smtp.ssl.protocols","TLSv1.2");
        properties.put("mail.smtp.ssl.trust","smtp.gmail.com");
        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("hermanssenoga@gmail.com","hgqs xvjm rfxl plbd");
            }
        });

        Message message=new MimeMessage(session);
        message.setSubject("Math Competition Application Status");
        message.setText(out);
       Address Address4=new InternetAddress(email);
       message.addRecipient(Message.RecipientType.TO,Address4);

        Transport.send(message);
        System.out.println("mail sent!");
    }

    private  boolean DBcheck(String username,String email)throws java.sql.SQLException,
            java.lang.ClassNotFoundException,java.io.IOException {

        String sql="SELECT * FROM rejected WHERE username=? AND email=?";
        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools","root","");

        PreparedStatement statement =connection.prepareStatement(sql);
        statement.setString(1,username);
        statement.setString(2,email);
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){

            return true;
        }
        else {
            return false;
        }}

        private  boolean DBcheck2(String username,String email)throws java.sql.SQLException,
                java.lang.ClassNotFoundException,java.io.IOException {

            String sql="SELECT * FROM applicants WHERE username=? AND email=?";
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools","root","");

            PreparedStatement statement =connection.prepareStatement(sql);
            statement.setString(1,username);
            statement.setString(2,email);
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next()){

                return true;
            }
            else {
                return false;
            }
       }

       private String saverUserImage(String path,String username) throws IOException {
           String serverdirectory="C:/xampp/htdocs/School-Mathematics-Challenge1/participant-images/";
           byte[] imageData = Files.readAllBytes(Paths.get(path));

           File directory=new File(serverdirectory);
           if(!directory.exists()){
               boolean isCreated=directory.mkdirs();
               if(!isCreated){
                   throw new IOException("Failed to create directory:"+serverdirectory);


               }
           }
           String filePath=serverdirectory+username+".png";
           try(FileOutputStream fos=new FileOutputStream(filePath)){
               fos.write(imageData);
           }
           return filePath;

       }

    private void viewChallenges()throws java.sql.SQLException,
        java.lang.ClassNotFoundException,java.io.IOException {
output=new PrintWriter(socket.getOutputStream(),true);
        String sql="SELECT * FROM challenges WHERE is_active=1;";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nationschools","root","");
        Statement statement =connection.createStatement();
        ResultSet resultSet=statement.executeQuery(sql);
        if(resultSet.next()){
while(resultSet.next()){
    String challenge_number=resultSet.getString("title");
    String openingDate=resultSet.getString("start_date");
    String closingDate=resultSet.getString("end_date");
    String duration=resultSet.getString("duration");
    String challenge=(challenge_number+ " " + openingDate + " " +closingDate+ " " +duration);
    output.println(challenge);

}
    output.println("END");
        }
        else {
            output.println("No available challenges");
        }
    }

}






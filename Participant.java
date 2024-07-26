package src;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Participant {
    private String user;
    public static void main(String [] args)throws IOException{
        Socket socket=new Socket("localhost",8000);
        Participant part =new Participant();
        part.handleinput(socket);

    }


//METHOD 1..displays the main menu and recieves a command
    public static String Register ()throws IOException{
    Scanner scanner = new Scanner(System.in);

    System.out.println("Run the command --Register-- to Register and Enter your details in the order below");
    System.out.println("Register username firstname lastname emailAddress date_of_birth school_registration_number image_file.png ");
    System.out.println("Run the command --ViewChallenges-- to view available challenges..");
    System.out.println("Enter command:");
    String input=scanner.nextLine();
    return input;
    }

  //method 3...method sends output and receives input from server
    public static String connectServer(String  details,Socket socket)throws IOException {

        PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
        out.println(details);
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String string=reader.readLine();
        return string;

    }

    //method 2...Method that handles commands and performs given actions
    private void handleinput(Socket socket)throws IOException{
        String input=Register();
      String [] details=input.split(" ");
        if("Register".equals(details[0])){
            if(details.length==8){
                  boolean bool=checkDateFormat(details[5]);
                  if(bool){
                      boolean bool2=validateImagePath(details[7]);
                      if(bool2){
                    String in=connectServer(input,socket);
                    System.out.println(in);
                    Scanner scanner=new Scanner(System.in);
                    String out=scanner.nextLine();
                if("Exit".equals(out)){
                    handleinput(socket);}
                else{
                    System.out.println("Invalid image path!");
                    handleinput(socket);

                }
                }

            } else  {
                      System.out.println("Invalid date format !\nUse yyyy-MM-dd format");
                    handleinput(socket);
                  }

                  }

                  else{
                System.out.println("Missing details.Please make sure there are no missing fields!");
                Scanner scanner=new Scanner(System.in);
                String out=scanner.nextLine();
                if("Exit".equals(out)){
                    handleinput(socket);
                }

            }
        }
        else if("viewChallenges".equals(details[0]) && details.length<2){

            Scanner key=new Scanner(System.in);
            System.out.println("Please log in first");
            System.out.print("Enter 1 if already registered\n" +
                    "Enter 2 to setup your password if new user:");
            String in=key.nextLine();
            if("1".equals(in)){
            System.out.println("Username:");
            String username=key.nextLine();
            System.out.println("Password:");
            String passWord=key.nextLine();
            String user=username+ " " +passWord;
           String out= connectServer(user,socket);
           if("true".equals(out)){
               String view="viewChallenges";
               this.user=username;
               try{ PrintWriter send=new PrintWriter(socket.getOutputStream(),true);
                   send.println(view);}
               catch (java.io.IOException ioException){
                   System.out.println("Error in Viewing Challenges");
               }
               BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
               String row;

               while (!(row=bufferedReader.readLine()).equals("END")){
                   System.out.println(row);}

               Scanner keyboard=new Scanner(System.in);
               String choice=keyboard.nextLine();
               AttemptChallenge attemptChallenge=new AttemptChallenge();
               String [] split=choice.split(" ");
               if("attemptChallenge".equals(split[0])){

               try{ attemptChallenge.handleChallenge(split[1],this.user);}
               catch (java.lang.ClassNotFoundException e){
                   e.printStackTrace();
               }}
               else{
                   handleinput(socket);
               }


           }
           else {
               System.out.println("Invalid username or password");
               Scanner scanner=new Scanner(System.in);
               String ou=scanner.nextLine();
               if("Exit".equals(ou)){
                   handleinput(socket);
               }

           }

            }
            else if("2".equals(in)){
                System.out.println("Username:");
                String username=key.nextLine();
                System.out.println("Password to use:");
                String passWord=key.nextLine();
                String user="New"+ " " +username+ " " +passWord;
               String out= connectServer(user,socket);
                System.out.println(out);
                if("true".equals(out)){
                    String view="viewChallenges";
                    this.user=username;
                    try{ PrintWriter send=new PrintWriter(socket.getOutputStream(),true);
                        send.println(view);}
                    catch (java.io.IOException ioException){
                        System.out.println("Error in Viewing Challenges");
                    }
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String row;

                    while (!(row=bufferedReader.readLine()).equals("END")){
                        System.out.println(row);}

                    Scanner keyboard=new Scanner(System.in);
                    String choice=keyboard.nextLine();
                    AttemptChallenge attemptChallenge=new AttemptChallenge();
                    String [] split=choice.split(" ");
                    System.out.println(split[0]);
                    System.out.println(split[1]);
                    String spl=split[0];
                    if("attemptChallenge".equals(spl)){

                        try{ attemptChallenge.handleChallenge(split[1],this.user);}
                        catch (java.lang.ClassNotFoundException e){
                            e.printStackTrace();
                        }}
                    else{
                        handleinput(socket);
                    }

                }
               else {

                    System.out.println("Operation not successful!");
                Scanner scanner=new Scanner(System.in);
                String ou=scanner.nextLine();
                if("Exit".equals(ou)){
                    handleinput(socket);
                }
               else {
                   handleinput(socket);
                }
               }
            }

            }

        else {
            System.out.println("Unknown command!");
            Scanner scanner=new Scanner(System.in);
            String ou=scanner.nextLine();
            if("Exit".equals(ou)){
                handleinput(socket);
            }

        }
  }

  //Method verifies that birth date is in acceptable Mysql format
  private boolean checkDateFormat(String date){
      SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
      sdf.setLenient(false);
      try{
          Date parsedDate=sdf.parse(date);
          return true;
      }catch(ParseException e){
          return false;
      }

  }

  //Method validates image path
    private boolean validateImagePath(String path){
        File file =new File(path);
        return file.exists() && file.isFile();

    }



}

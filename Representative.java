import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Representative {
    private String email;

    public static void main(String[] args)throws java.io.IOException {

        Socket socket = new Socket("localhost", 8000);
            Representative rep = new Representative();
            rep.LogIn(socket);
    }

    //Method to enable Representative to login
    private void LogIn(Socket socket)throws java.io.IOException{
        String verify;
        //String email;
        System.out.println("------Enter Login details------");
         Scanner scanner=new Scanner(System.in);
        System.out.print("Enter emailAdress:");
        this.email=scanner.nextLine();
     // String password=maskPassword("Enter password:");
//        Console console =System.console();
//        char[] passwordArray=console.readPassword("Enter password(Your password will not be visible on screen:");
//        String password=new String(passwordArray);
        System.out.println("Enter password");
        String password= scanner.nextLine();

         verify=verifyLogIn(socket,email,password);
     if("true".equals(verify))
     {
        System.out.println("You are logged in!");
      menu(email,socket);}
     else
         System.out.println(
                 "Invalid login details");
         LogIn(socket);

    }

    //This method sends login details to the server to verify that details passed to login method exist in the database
    public static String verifyLogIn(Socket socket,String email,String password)throws java.io.IOException{
        String output="Login" + " "+ email+ " " +password;
        PrintWriter input=new PrintWriter(socket.getOutputStream(),true);
        input.println(output);
    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String read=bufferedReader.readLine();
    if("true".equals(read)){
      return "true";}
    else {return "false";}
    }

    //Method requests server for  available applicants' details .
    public static void viewApplicants(String email,Socket socket)throws java.io.IOException{
       try{ PrintWriter input=new PrintWriter(socket.getOutputStream(),true);
           input.println("viewApplicants"+ " " +email);}
       catch (java.io.IOException ioException){
           System.out.println("Error in Viewing Applicants details!");
       }
       BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
       String row;

       while (!(row=bufferedReader.readLine()).equals("END")){
           System.out.println(row);}

    }

    //Method to confirm Applicants
    private void ConfirmApplicant(Socket socket,String in){
        String[]output=in.split(" ");
        String confirm=output[1];
        String username=output[2];
        if("yes".equals(confirm)){
        try{ PrintWriter input=new PrintWriter(socket.getOutputStream(),true);
            input.println("confirm"+ " "+ "yes"+ " " +username );
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String out= reader.readLine();
            System.out.println(out);
        }
        catch (java.io.IOException e){
           e.printStackTrace();
        }}
        else {
            try{ PrintWriter input=new PrintWriter(socket.getOutputStream(),true);
                input.println("confirm"+ " "+ "no"+ " " +username );
                BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String out= reader.readLine();
                System.out.println(out);
            }
            catch (java.io.IOException e){
                e.printStackTrace();

        }


    }}
    private void menu(String email,Socket socket)throws java.io.IOException{
        System.out.println("----Commands-----\n ---viewApplicants--\n---confirmApplicant yes/no username---");
        Scanner scanner=new Scanner(System.in);
        String in =scanner.nextLine();
        String[]output=in.split(" ");
        if("viewApplicants".equals(output[0])&&output.length==1){
            viewApplicants(email,socket);
            String command=scanner.nextLine();
            if("Exit".equals(command)){
                menu(email, socket);
            }
        }
        else if("confirm".equals(output[0])&&output.length==3){

            ConfirmApplicant(socket,in);
            String command=scanner.nextLine();
            if("Exit".equals(command)){
                menu(email, socket);
            }
        }
        else{
            System.out.println("Unknown command");
            String command=scanner.nextLine();
            if("Exit".equals(command)){
                menu(email, socket);
            }

        }

    }



}

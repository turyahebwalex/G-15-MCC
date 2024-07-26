package src;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int port=8000;

    public static void main(String [] args)throws java.io.IOException{

                ServerSocket serversocket=new ServerSocket(port);
                System.out.println("Server waiting.....");
                while(true){
                   Socket socket=serversocket.accept();
                    System.out.println("New client connected!");
                    ClientHandler clientHandler=new ClientHandler(socket);
                    clientHandler.start();}

    }
}


package PROJECTS.WebServer.MultiThreadWebServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {

    public Consumer<Socket> getConsumer(){

//        return new Consumer<Socket>() {
//            @Override
//            public void accept(Socket socket) {
//                try{
//                    Socket clientSocket = null;
//                    PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream());
//                    toClient.println("Hellow from Server");
//                    toClient.close();
//                    clientSocket.close();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };



//        return clientSocket -> {
//            try{
//                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
//                toClient.println("Hellow from Server");
//                toClient.close();
//                clientSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        };



        return (clientSocket) -> {
            try (PrintWriter toSocket = new PrintWriter(clientSocket.getOutputStream(), true)) {
                toSocket.println("Hello from server " + clientSocket.getInetAddress());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };


    }


    public static void main(String[] args) {
        int port = 8018;
        Server server = new Server();

        try{
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(50000);
            System.out.println("Server is listening " + port);

            while(true){
                Socket acceptedSocket = serverSocket.accept();
                Thread thread = new Thread(() -> server.getConsumer().accept(acceptedSocket));
                thread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

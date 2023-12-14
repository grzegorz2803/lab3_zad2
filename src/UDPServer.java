import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer {
    private static final int MAX_CLIENTS = 250;

    public static void main(String[] args) {
       try{
           InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
           int serverPort = 12345;
           DatagramSocket serverSocket = new DatagramSocket(serverPort);
           ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);
           ArrayList<String[]> questionsList = readQuestionsFromFile("bazaPytan.txt");

           System.out.println("Serwer nasłuchuje na "+serverAddress+":"+serverPort);
           while (true){
               try {
                   byte[] receiveData = new byte[1024];
                   DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                   serverSocket.receive(receivePacket);
                   SocketAddress clientAddress = receivePacket.getSocketAddress();
                   executorService.execute(new ClientHandler(serverSocket, clientAddress, questionsList));
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
    private static ArrayList<String[]> readQuestionsFromFile(String fileName) {
        ArrayList<String[]> questionsList = null;
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
            questionsList = new ArrayList<>();
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] question = new String[6];
                question[0] = line;
                for (int i = 0; i < 5; i++) {
                    String option = fileReader.readLine();
                    question[i + 1] = option;
                }
                questionsList.add(question);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsList;
    }




}

import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer {
    private static final int MAX_CLIENTS = 250;
    private static final int TIMEOUT = 20000;
    public static void main(String[] args) {
       try{
           InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
           int serverPort = 12345;
           DatagramSocket serverSocket = new DatagramSocket(serverPort);
           ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);
           ArrayList<String[]> questionsList = readQuestionsFromFile("bazaPytan.txt");
           System.out.println("Serwer nasłuchuje na "+serverAddress+":"+serverPort);
           while (true){
               SocketAddress clientAddress = serverSocket.receive(new DatagramPacket(new byte[1024],1024)).getSocketAddress();
               executorService.execute(new ClientHandler(serverSocket, clientAddress, questionsList));
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
    private static ArrayList<String[]> readQuestionsFromFile(String fileName) {
        ArrayList<String[]> questionsList = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String questionLine = scanner.nextLine();
                String[] questionData = parseQuestionLine(questionLine);
                questionsList.add(questionData);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return questionsList;
    }
    private static String[] parseQuestionLine(String questionLine) {
        // Dzieli linię na pytanie i odpowiedzi
        String[] parts = questionLine.split("\\n");

        String question = parts[0].trim();
        String[] options = {parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()};

        // Wyodrębnia literę poprawnej odpowiedzi
        String correctAnswer = parts[5].trim().toLowerCase();

        // Łączy pytanie z odpowiedziami i poprawną odpowiedzią
        String[] questionData = new String[options.length + 2];
        questionData[0] = question;
        System.arraycopy(options, 0, questionData, 1, options.length);
        questionData[questionData.length - 1] = correctAnswer;

        return questionData;
    }

}

import java.io.File;
import java.io.FileNotFoundException;
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
        String[] parts = questionLine.split("\\*\\*");

        // Sprawdź, czy istnieją odpowiedzi
        if (parts.length < 2) {
            // Może rzucić błąd lub inaczej obsłużyć sytuację
            throw new IllegalArgumentException("Nieprawidłowy format pytania w pliku.");
        }

        String question = parts[0].trim();

        // Pobierz odpowiedzi od pierwszej linii po znakiem '**' do ostatniej linii
        String[] options = Arrays.copyOfRange(parts, 1, parts.length - 1);
        for (int i = 0; i < options.length; i++) {
            options[i] = options[i].trim();
        }

        // Wyodrębnia literę poprawnej odpowiedzi z ostatniej linii
        String correctAnswer = parts[parts.length - 1].trim().toLowerCase();

        // Łączy pytanie z odpowiedziami i poprawną odpowiedzią
        String[] questionData = new String[options.length + 2];
        questionData[0] = question;
        System.arraycopy(options, 0, questionData, 1, options.length);
        questionData[questionData.length - 1] = correctAnswer;

        return questionData;
    }



}

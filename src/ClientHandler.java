import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
private DatagramSocket serverSocket;
private SocketAddress clientAddress;
private ArrayList<String[]> questionsList;
    private static final int TIMEOUT = 20000;

public ClientHandler(DatagramSocket serverSocket, SocketAddress clientAddress, ArrayList<String[]> questionsList){
    this.serverSocket = serverSocket;
    this.clientAddress = clientAddress;
    this.questionsList = questionsList;
}
    @Override
    public void run() {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TIMEOUT);
            FileWriter answersFileWriter = new FileWriter("bazaOdpowiedzi.txt", true);
            for (String[] questionData:questionsList){
                String question = questionData[0];
                String[] options = {questionData[1],questionData[2],questionData[3],questionData[4]};
                String correctAnswer = questionData[5];
                sendQuestionToClient(clientSocket,question,options);
                String answer = receiveAnswerFromClient(clientSocket);
                boolean isCorrect = answer.equals(correctAnswer);
                saveAnswerToFile(answersFileWriter,question,answer,isCorrect);
            }
            clientSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void sendQuestionToClient(DatagramSocket clientSocket, String question, String[] options) throws IOException {
        String questionMessage = formatQuestionMessage(question, options);
        byte[] sendData = questionMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress);
        clientSocket.send(sendPacket);
    }

    private String formatQuestionMessage(String question, String[] options) {
        StringBuilder message = new StringBuilder(question + "\n");
        for (int i = 0; i < options.length; i++) {
            message.append((char) ('A' + i)).append(". ").append(options[i]).append("\n");
        }
        message.append("Odpowiedź: ");
        return message.toString();
    }

    private String receiveAnswerFromClient(DatagramSocket clientSocket) throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength()).toUpperCase();
    }

    private void saveAnswerToFile(FileWriter writer, String question, String answer, boolean isCorrect) throws IOException {
        writer.write("Pytanie: " + question + "\n");
        writer.write("Odpowiedź: " + answer + "\n");
        writer.write("Poprawna: " + (isCorrect ? "Tak" : "Nie") + "\n\n");
        writer.flush();
    }
}

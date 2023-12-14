import java.net.*;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
            int serverPort = 12345;

            DatagramSocket clientSocket = new DatagramSocket();

            Scanner scanner = new Scanner(System.in);
            System.out.print("Podaj imie i nazwisko:");
            String answer = scanner.nextLine().toLowerCase();
            // Wysyłanie odpowiedzi do serwera
            byte[] sendData = answer.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);
            for (int i = 0; i < 10; i++) { // Zakładam 10 pytań, dostosuj do rzeczywistych potrzeb

                // Odbierz potwierdzenie od serwera
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                System.out.println("Otrzymano potwierdzenie od serwera: " + new String(receivePacket.getData(), 0, receivePacket.getLength()));
                answer = scanner.nextLine().toLowerCase();
                // Wysyłanie odpowiedzi do serwera
                 sendData = answer.getBytes();
                 sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                clientSocket.send(sendPacket);




            }

            // Zamknij gniazdo klienta
            clientSocket.close();
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

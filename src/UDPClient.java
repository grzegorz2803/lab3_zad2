import java.net.*;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
            int serverPort = 12345;

            DatagramSocket clientSocket = new DatagramSocket();

            Scanner scanner = new Scanner(System.in);

            for (int i = 0; i < 10; i++) { // Zakładam 10 pytań, dostosuj do rzeczywistych potrzeb
                System.out.print("Podaj odpowiedź (a, b, c, d) na pytanie " + (i + 1) + ": ");
                String answer = scanner.nextLine().toLowerCase();

                // Sprawdź, czy odpowiedź jest poprawna (opcjonalnie)
                if (!answer.matches("[a-d]")) {
                    System.out.println("Błędna odpowiedź. Podaj poprawną odpowiedź.");
                    i--; // Powtórz to samo pytanie
                    continue;
                }

                // Wysyłanie odpowiedzi do serwera
                byte[] sendData = answer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                clientSocket.send(sendPacket);

                // Odbierz potwierdzenie od serwera
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                System.out.println("Otrzymano potwierdzenie od serwera: " + new String(receivePacket.getData(), 0, receivePacket.getLength()));
            }

            // Zamknij gniazdo klienta
            clientSocket.close();
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

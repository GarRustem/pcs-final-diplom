import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try(Socket socket = new Socket(ServerConfig.HOST, ServerConfig.PORT);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println(input.readLine());

            Scanner scanner = new Scanner(System.in);
            output.println(scanner.nextLine());
//            System.out.println(reader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

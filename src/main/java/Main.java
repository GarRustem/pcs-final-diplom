import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {

        BooleanSearchEngine engine = new BooleanSearchEngine(new File("D:\\1. Workplace\\1. Java\\NetologyHW\\pcs-final-diplom\\pdfs"));

        try (ServerSocket server = new ServerSocket(ServerConfig.PORT)) {
            System.out.println("Server is running"); //  онсоль не выводит кириллицу.  одировка помогает грамотно отображать только кириллицу названий файлов.

            while (true) {
                try (Socket client = server.accept();
                     BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                     PrintWriter output = new PrintWriter(client.getOutputStream(), true)) {

                    System.out.println("Client " + client.getPort() + " is patched.");
                    output.println("Enter the text for search.");

                    String word = input.readLine();

                    Gson gson = new Gson();
                    String saveJsonForm = gson.toJson(engine.search(word));
                    System.out.println(saveJsonForm);
                }
            }
        } catch (IOException e) {
            System.out.println("Can't start the server.");
            e.printStackTrace();
        }
    }
}
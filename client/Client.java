package client;

import javax.xml.parsers.DocumentBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by AaronLeon on 5/23/17.
 */
public class Client {
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    DocumentBuilder db;

    public Client(String host, int port, DocumentBuilder db) {
        try {
            this.socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.db = db;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readInput() {
        String input = null;
        try {
            in.readLine();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return input;
    }

    public void writeOutput(String output) {
        try {
            out.println(output);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}

package test;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(String ip, int port){
        try {
            socket = new Socket(ip,port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public String sendRequest(String request) {
        String r = null;
        try {
            out.println(request);
            r = in.readLine();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        finally {
            try{
                in.close();
                out.close();
            }catch (IOException e ){
                System.out.println(e.getMessage());
            }
        }
        return r;
    }
}

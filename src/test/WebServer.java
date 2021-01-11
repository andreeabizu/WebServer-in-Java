// This is a personal academic project. Dear PVS-Studio, please check it.

// PVS-Studio Static Code Analyzer for C, C++, C#, and Java: http://www.viva64.com

package test;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class WebServer {
    private  ServerSocket server;
    private  static int port = 8080;
    private String address = "0.0.0.0/0.0.0.0";
    private static List<String> pages = new ArrayList<>();

    private static void setPortMember(int port)
    {
        if(port < 0 || port > 65535)
            throw new IllegalArgumentException();
        WebServer.port = port;
    }

    public void setPort(int port) { //setam portul si pornim serverul pe portul respectiv

        setPortMember(port);
        Thread initial = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (server != null)//daca exista un alt server, il oprim
                    {
                        server.close();
                    }
                    server = new ServerSocket(port);//instantiem un nou server cu portul specificat
                    address = server.getInetAddress().toString();
                    startServer();//pornim serverul
                } catch (IOException e) {
                    System.out.println(e.getMessage()); }
            }
        });
        initial.start();
    }

    public void startServer() throws IOException{
            WebClientHandler.setStopped(true);//serverul e in starea stopped
            while (true) {//accepta conexiuni, un thread pentru fiecare client
                Socket socket = server.accept();
                (new Thread(new WebClientHandler(socket))).start();
            }
    }


    public void setServer(ServerSocket s)
    { this.server = s; }

    public void wakeUp()//trecem serverul in starea running
    {
        WebClientHandler.setStopped(false);
        WebClientHandler.setMaintenance(false);
    }

    public void maintenance(){//setam serverului starea maintenance
        WebClientHandler.setMaintenance(true);
    }


    public void stop(){ //oprim serverul
        WebClientHandler.setStopped(true);
        WebClientHandler.setMaintenance(false);
    }

    public void changeRoot(File file){
        WebClientHandler.changeRoot(file);
    }

    public void changeMaintenanceRoot(File file){
        WebClientHandler.changeMaintenanceRoot(file);
    }

    public int getPort(){
        return this.port;
    }

    public String getAddress() {
        return address;
    }

    public boolean isStopped(){
        return WebClientHandler.stopped;
    }

    public boolean isRunning(){
        return !WebClientHandler.stopped;
    }

    public boolean isInMaintenance(){
        return WebClientHandler.maintenance;
    }

    public static class WebClientHandler implements Runnable{//clasa folosita pentru a raspunde request-urilor

        private static final String DEFAULT_FILE = "index.html";
        private static final String FILE_NOT_FOUND = "404.html";
        private static  File ROOT = new File("./root");
        private static File  MAINTENANCE = new File("./maintenance");
        private static File ADMINMAINTENANCE = new File("./maintenance");
        private static File  STOPPED = new File("./stop\\stop.html");
        private Socket mySocket;
        private static boolean maintenance;
        private static boolean stopped;
        private String userName = "";

        public WebClientHandler(Socket c)
        {
            mySocket = c;
        }

        public BufferedReader getBufferedReader() throws IOException {
            return new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        }

        public void sendHeader(String message) throws IOException {//trimite header catre client
            PrintWriter out = new PrintWriter(mySocket.getOutputStream());
            out.println(message);
            out.println();
            out.flush();
        }


        @Override
        public void run() {

            BufferedReader in = null;
            BufferedOutputStream outContent = null;
            String path = "";

            try {
                in = getBufferedReader();
                outContent = new BufferedOutputStream(mySocket.getOutputStream());

                if(stopped == true){//daca se afla in starea stopped, serverul raspunde cu pagina Stopped

                    sendStopPage(outContent);
                }
                else
                {   String request = in.readLine();
                    int i,j,k;
                    File file = null;
                    String s = null;

                    if(request != null) {

                        i = request.indexOf(' ');
                        j = request.indexOf(' ', i + 1);
                        k = request.indexOf('=');
                        if(j >= 0)
                        {   s = request.substring(i+1,j);

                            if (k == -1 ) {
                                path = s;
                                path = modifyPath(path);
                            } else {
                                path = request.substring(i + 1, k - 5);
                                path = modifyPath(path);
                                userName = request.substring(k + 1, j).toLowerCase();
                            }
                        }

                        if (maintenance == true) {
                            file = getMaintenancePage(k,path);

                        } else //altfel raspunde cu pagina ceruta
                        {
                            file = getRunningPage(k,path);
                            if(path.endsWith("html"))
                            {
                                pages.add("localhost:"+port+s);
                            }
                        }
                        int length = (int) file.length();
                        byte[] fileData = readData(file, length);
                        sendHeader("HTTP/1.1 " + file.getPath());
                        outContent.write(fileData, 0, length);
                        outContent.flush();
                    }
                }
            } catch (FileNotFoundException e) {//daca pagina nu se gaseste, serverul raspunde cu File_Not_Found
                try {
                    File file = new File("root/", FILE_NOT_FOUND);
                    pages.add("localhost:"+port+"/404.html");
                    int length = (int) file.length();
                    byte[] fileData = readData(file, length);
                    sendHeader("HTTP/1.1 "+file.getPath());
                    outContent.write(fileData, 0, length);
                    outContent.flush();

                } catch (IOException ioException) {
                    System.err.println(ioException.getMessage());
                }

            } catch (IOException ioException) {
                System.err.println("Problem with Communication Server" + ioException.getMessage());
            } finally {
                try {
                    outContent.close();
                    mySocket.close();
                    in.close();
                } catch (Exception e) {
                    System.err.println("Error closing stream" + e.getMessage());
                } }
        }

        private void sendStopPage(BufferedOutputStream outContent) throws IOException
        {   int length = (int) STOPPED.length();
            byte[] fileData = readData(STOPPED, length);
            sendHeader("HTTP/1.1 "+STOPPED.getPath());
            outContent.write(fileData, 0, length);
            outContent.flush();
        }

        private File getMaintenancePage(int k,String path) throws IOException {
            File file = null;
            if (k != -1) {
                if (userName.equals("admin")) {

                    File theFile = new File(ADMINMAINTENANCE, "\\admin.html");
                    file = searchH(theFile);

                } else {
                    file = new File(MAINTENANCE, "\\maintenance.html");
                }
            } else {
                if (path.endsWith("css")) {
                    file = new File(ROOT, path);
                } else {
                    file = new File(ROOT, "\\index.html");
                }
            }

            return file;
        }

        private File getRunningPage(int k, String path){
            File file;
            if (k == -1)
                file = new File(ROOT, path);
            else
                file = new File(ROOT, "\\l.html");

            return file;
        }

        private File searchH(File theFile) throws IOException {
            BufferedReader i = new BufferedReader(new InputStreamReader(new FileInputStream(theFile)));
            char[] a = new char[(int)theFile.length()];
            i.read(a,0,(int)theFile.length());
            String n = String.valueOf(a);

            for(int u=0; u<pages.size();u++) {
                n = n.concat("<p>").concat(pages.get(u)).concat("</p>");

            }

            File file = new File("admin.html");

            FileWriter fw = new FileWriter(file);
            fw.write(n);
            fw.close();
            return file;
        }

        public static void setStopped(boolean value)
        {
            stopped = value;
        }

        public static void changeRoot(File file)
        { ROOT = file;
        }

        public static void changeMaintenanceRoot(File file)
        {
            MAINTENANCE = file;
        }

        public static void setMaintenance(boolean value)
        {
            maintenance = value;
        }

        private byte[] readData(File file, int length) throws IOException {
            FileInputStream in = null;
            byte[] data = new byte[length];
            try{
                in = new FileInputStream(file);
                in.read(data);
            } finally {
                if (in != null)
                    in.close();
            }
            return data;
        }

        private String modifyPath(String path){
            if(path.equals("/")) {
                path = path + DEFAULT_FILE;
            }
            else {
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);}
            }
            return path;
        }

    }

}
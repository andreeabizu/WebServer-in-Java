package WebServer;
import java.io.*;
import java.net.*;

public class WebServer {
    public  ServerSocket server;
    private int port;

    public void setPort(int port){ //setam portul si pornim serverul pe portul respectiv
        try {
            if(server!=null)//daca exista un alt server, il oprim
                server.close();
            this.port = port;
            server = new ServerSocket(port);//instantiem un nou server cu portul specificat
            startServer();//pornim serverul
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void startServer() throws IOException {
        WebClientHandler.setStopped(true);//serverul e in starea stopped
        while (true) {//accepta conexiuni, un thread pentru fiecare client
            Socket socket = server.accept();
            (new Thread(new WebClientHandler(socket))).start();
        }
    }
    public void setServer(ServerSocket s) throws IOException {
        this.server = s;
      }

    public void wakeUp()//trecem serverul in starea running
    {
        WebClientHandler.setStopped(false);
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
        private static  File ROOT = new File("root");
        private static File  MAINTENANCE = new File("maintenance/maintenance.html");
        private static File  STOPPED = new File("stop/stop.html");
        private Socket mySocket;
        private static boolean maintenance;
        private static boolean stopped;

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
            String path = null;

            try {
                in = getBufferedReader();
                outContent = new BufferedOutputStream(mySocket.getOutputStream());

                if(stopped == true){//daca se afla in starea stopped, serverul raspunde cu pagina Stopped
                    int length = (int) STOPPED.length();
                    byte[] fileData = readData(STOPPED, length);
                    sendHeader("HTTP/1.1 "+STOPPED.getPath());
                    outContent.write(fileData, 0, length);
                    outContent.flush();
                }
                else
                {
                    if(maintenance == true){//daca se afla in starea maintenance va raspunde cu pagina Maintenance

                        int length = (int) MAINTENANCE.length();
                        byte[] fileData = readData(MAINTENANCE, length);
                        sendHeader("HTTP/1.1 "+MAINTENANCE.getPath());
                        outContent.write(fileData, 0, length);
                        outContent.flush();
                    }
                    else //altfel raspunde cu pagina ceruta
                    {   String request = in.readLine();
                        int i,j;
                        i = request.indexOf(' ');
                        j = request.indexOf(' ',i+1);
                        path = request.substring(i+1,j);
                        path = modifyPath(path);

                        File file = new File(ROOT, path);
                        int length = (int) file.length();
                        byte[] fileData = readData(file, length);
                        sendHeader("HTTP/1.1 "+ file.getPath());
                        outContent.write(fileData, 0, length);
                        outContent.flush();
                    }
                }
            } catch (FileNotFoundException e) {//daca pagina nu se gaseste, serverul raspunde cu File_Not_Found
                try {
                    File file = new File(ROOT, FILE_NOT_FOUND);
                    int length = (int) file.length();
                    byte[] fileData = readData(file, length);
                    sendHeader("HTTP/1.1 "+file.getPath());
                    outContent.write(fileData, 0, length);
                    outContent.flush();
                } catch (IOException ioException) {
                    System.err.println(ioException.getMessage());
                    System.exit(3);
                }

            } catch (IOException ioException) {
                System.err.println("Problem with Communication Server" + ioException.getMessage());
                System.exit(1);
            } finally {
                try {
                    outContent.close();
                    mySocket.close();
                    in.close();
                } catch (Exception e) {
                    System.err.println("Error closing stream" + e.getMessage());
                    System.exit(2);
                }

            }
        }
        public static void setStopped(boolean value)
        {
            stopped = value;
        }

        public static void changeRoot(File file)
        {
            ROOT = file;
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
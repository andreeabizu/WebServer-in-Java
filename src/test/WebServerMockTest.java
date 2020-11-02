package test;

import WebServer.WebServer;
import org.junit.*;

import java.io.*;
import java.net.*;

import static org.mockito.Mockito.*;

public class WebServerMockTest {

    private WebServer server;

    @Before
    public void setUp() throws IOException {
        server = new WebServer();
    }

    @Test
    public void verifyConection() throws IOException {
        ServerSocket mockServerSocket = spy(ServerSocket.class);
        InetAddress inetAddress = InetAddress.getByName("localhost");
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, 8080);
        mockServerSocket.bind(socketAddress);
        server.setServer(mockServerSocket);

        Thread run = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        run.start();
        new Socket("127.0.0.1",8080);
        verify(mockServerSocket,atLeastOnce()).accept();
    }

    @Test
    public void verifyServerReceivesRequest() throws IOException {
        server.setServer(new ServerSocket(8081));

        Thread run =  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        run.start();
        Socket mockSocket = spy(Socket.class);
        mockSocket.connect(new InetSocketAddress("127.0.0.1", 8081));
        WebServer.WebClientHandler clientHandler = new WebServer.WebClientHandler(mockSocket);

        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream("GET / HTTP/1.1\nHost: localhost".getBytes()));
        String s = clientHandler.getBufferedReader().readLine();
        Assert.assertEquals(s,"GET / HTTP/1.1");
        verify(mockSocket,atLeastOnce()).getInputStream();

    }

    @Test
    public void verifyServerSendResponse() throws IOException {
        server.setServer(new ServerSocket(8082));

        Thread run =  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        run.start();
        Socket mockSocket = spy(Socket.class);
        mockSocket.connect(new InetSocketAddress("127.0.0.1", 8082));
        WebServer.WebClientHandler clientHandler = new WebServer.WebClientHandler(mockSocket);
        clientHandler.sendHeader("");
        verify(mockSocket).getOutputStream();

    }
}

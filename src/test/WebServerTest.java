package test;

import org.junit.*;
import org.junit.runners.MethodSorters;
import WebServer.WebServer;

import java.io.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebServerTest {

    private static WebServer webServer;
    private String ip ="127.0.0.1";

    @BeforeClass
    public static void setUpClass(){
        webServer = new WebServer();
        Thread initial = new Thread(new Runnable() {
            @Override
            public void run() {
                webServer.setPort(8080);

            }
        });
        initial.start();
    }

    @Test
    public void createInstanceOfWebServerObject()
    {
        Assert.assertNotNull(webServer);
    }

    @Test
    public void A_getPortTrue(){
        Assert.assertEquals(webServer.getPort(),8080);
    }


    @Test
    public void A_serverAcceptConnection(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /");
        Assert.assertNotNull(response);
    }

    @Test
    public void A_verifyInitialState() {
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html ");
        Assert.assertEquals(response,"HTTP/1.1 stop\\stop.html");
    }


    @Test
    public void A_verifyRunningStateFileFound(){
        webServer.wakeUp();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html ");
        Assert.assertEquals(response,"HTTP/1.1 root\\index.html");
    }

    @Test
    public void A_verifyRunningStateFileNotFound(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /h.html ");
        Assert.assertEquals(response,"HTTP/1.1 root\\404.html");
    }

    @Test
    public void A_verifyRunningStateUnspecifiedFile(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET / ");
        Assert.assertEquals(response,"HTTP/1.1 root\\index.html");
    }

    @Test
    public void A_verifyRunningStateNameFileEndWithSlash(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html/ ");
        Assert.assertEquals(response,"HTTP/1.1 root\\l.html");
    }

    @Test
    public void A_verifyServerInMaintenanceState() {
        webServer.maintenance();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html ");
        Assert.assertEquals(response,"HTTP/1.1 maintenance\\maintenance.html");
    }

    @Test
    public void A_verifyTheResponseAfterStoppingTheServer(){
        webServer.stop();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html ");
        Assert.assertEquals(response,"HTTP/1.1 stop\\stop.html");
    }



    @Test
    public void B_verifyRunningStateAfterChangingRootFileFound(){
        webServer.wakeUp();
        webServer.changeRoot(new File("pages"));
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html ");
        Assert.assertEquals(response,"HTTP/1.1 pages\\index.html");
    }

    @Test
    public void B_verifyRunningStateAfterChangingRootFileNotFound(){
        Client client = new Client();
        webServer.changeRoot(new File("pages"));
        client.connect(ip,8080);
        String response = client.sendRequest("GET /h.html ");
        Assert.assertEquals(response,"HTTP/1.1 pages\\404.html");
    }

    @Test
    public void B_verifyRunningStateAfterChangingRootUnspecifiedFile(){
        Client client = new Client();
        webServer.changeRoot(new File("pages"));
        client.connect(ip,8080);
        String response = client.sendRequest("GET / ");
        Assert.assertEquals(response,"HTTP/1.1 pages\\index.html");
    }

    @Test
    public void B_verifyRunningStateAfterChangingRootNameFileEndWithSlash(){
        Client client = new Client();
        webServer.changeRoot(new File("pages"));
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html/ ");
        Assert.assertEquals(response,"HTTP/1.1 pages\\l.html");
    }

    @Test
    public void B_verifyServerInMaintenanceStateAfterChangingMaintenanceRoot() {
        webServer.maintenance();
        webServer.changeMaintenanceRoot(new File("pages/maintenance.html"));
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html ");
        Assert.assertEquals(response,"HTTP/1.1 pages\\maintenance.html");
    }


    @Test
    public void B_verifyServerIsStopped()
    {  webServer.stop();
        Assert.assertTrue(webServer.isStopped());
    }

    @Test
    public void C_verifyServerIsRunning()
    {  webServer.wakeUp();
        Assert.assertTrue(webServer.isRunning());
    }

    @Test
    public void D_verifyServerIsInMaintenance()
    {  webServer.maintenance();
        Assert.assertTrue(webServer.isInMaintenance());
    }

}
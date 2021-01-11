// This is a personal academic project. Dear PVS-Studio, please check it.

// PVS-Studio Static Code Analyzer for C, C++, C#, and Java: http://www.viva64.com


package test;

import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebServerTest {

    private static WebServer webServer;
    private String ip ="127.0.0.1";

    @BeforeClass
    public static void setUpClass(){
        webServer = new WebServer();
        webServer.setPort(8080);
    }

    @Test
    public void createInstanceOfWebServerObject()
    {
        Assert.assertNotNull(webServer);
    }
    @Test
    public void A_getAddress(){
        Assert.assertEquals(webServer.getAddress(),"0.0.0.0/0.0.0.0");
    }

    @Test
    public void A_getPortTrue(){
        Assert.assertEquals(webServer.getPort(),8080);
    }

    @Test
    public void A_getPortFalse(){
        Assert.assertNotEquals(webServer.getPort(),8082);
    }

    @Test
    public void A_serverAcceptConnection(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /");
        Assert.assertNotNull(response);
    }

    @Test
    public void A_verifyInitialStateTrue() {
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html ");
        Assert.assertEquals(response,"HTTP/1.1 .\\stop\\stop.html");
    }

    @Test
    public void A_verifyInitialStateFalse() {
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html ");
        Assert.assertNotEquals(response,"HTTP/1.1 .\\root\\index.html");
    }


    @Test
    public void A_verifyRunningStateFileFound(){
        webServer.wakeUp();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html ");
        Assert.assertEquals(response,"HTTP/1.1 .\\root\\index.html");
    }

    @Test
    public void A_verifyRunningStateFileNotFoundTrue(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /h.html ");
        Assert.assertEquals(response,"HTTP/1.1 root\\404.html");
    }

    @Test
    public void A_verifyRunningStateFileNotFoundFalse(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /h.html ");
        Assert.assertNotEquals(response,"HTTP/1.1 .\\h.html");
    }

    @Test
    public void A_verifyRunningStateUnspecifiedFile(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET / ");
        Assert.assertEquals(response,"HTTP/1.1 .\\root\\index.html");
    }

    @Test
    public void A_verifyRunningStateNameFileEndWithSlash(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html/ ");
        Assert.assertEquals(response,"HTTP/1.1 .\\root\\l.html");
    }

    @Test
    public void A_verifyRunningStateWithNameRequest(){
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html?name=Ana ");
        Assert.assertEquals(response,"HTTP/1.1 .\\root\\l.html");
    }

    @Test
    public void A_verifyServerInMaintenanceState() {
        webServer.maintenance();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html ");
        Assert.assertEquals(response,"HTTP/1.1 .\\root\\index.html");
    }

    @Test
    public void A_verifyServerInMaintenanceStateCss() {
        webServer.maintenance();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.css ");
        Assert.assertEquals(response,"HTTP/1.1 .\\root\\index.css");
    }

    @Test
    public void A_verifyServerInMaintenanceStateAdminTrue() {
        webServer.maintenance();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html?name=admin ");
        Assert.assertEquals(response,"HTTP/1.1 admin.html");
    }

    @Test
    public void A_verifyServerInMaintenanceStateUser() {
        webServer.maintenance();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html?name=Ana ");
        Assert.assertEquals(response,"HTTP/1.1 .\\maintenance\\maintenance.html");
    }

    @Test
    public void A_verifyTheResponseAfterStoppingTheServerTrue(){
        webServer.stop();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html ");
        Assert.assertEquals(response,"HTTP/1.1 .\\stop\\stop.html");
    }

    @Test
    public void A_verifyTheResponseAfterStoppingTheServerFalse(){
        webServer.stop();
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /l.html ");
        Assert.assertNotEquals(response,"HTTP/1.1 .\\root\\l.html");

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
        Assert.assertEquals(response,"HTTP/1.1 root\\404.html");
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
        //webServer.changeMaintenanceRoot(new File("C:\\Users\\unu\\vvs\\WebServer\\pages"));
        webServer.changeMaintenanceRoot(new File("pages"));
        Client client = new Client();
        client.connect(ip,8080);
        String response = client.sendRequest("GET /index.html?name=Ana ");
        Assert.assertEquals(response,"HTTP/1.1 pages\\maintenance.html");
    }

    @Test
    public void B_verifyServerIsStoppedFalse()
    {  webServer.stop();
        Assert.assertFalse(webServer.isRunning());
    }

    @Test
    public void B_verifyServerIsStoppedTrue()
    {
       Assert.assertTrue(webServer.isStopped());
    }

    @Test
    public void C_verifyServerIsRunningFalse()
    {   webServer.wakeUp();
        Assert.assertFalse(webServer.isInMaintenance());
    }

    @Test
    public void C_verifyServerIsRunningTrue()
    {
       Assert.assertTrue(webServer.isRunning());
    }

    @Test
    public void D_verifyServerIsInMaintenanceFalse()
    {   webServer.maintenance();
        Assert.assertFalse(webServer.isStopped());
    }

    @Test
    public void D_verifyServerIsInMaintenanceTrue()
    {
       Assert.assertTrue(webServer.isInMaintenance());
    }

    @Test
    public void D_verifyServerPortAfterChangeIt(){
        webServer.setPort(8081);
        int port = webServer.getPort();
        Assert.assertEquals(port,8081);
    }

    @Test(expected = NullPointerException.class)
    public void E_TryToStartANullServer() throws IOException
    {    webServer.setServer(null);
         webServer.startServer();
    }

    @Test(expected = IllegalArgumentException.class)
    public void F_SetNegativeValueToPort() throws IOException {
        webServer.setPort(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void G_SetOutOfRangeValue() throws IOException {
        webServer.setPort(65536);
    }



}
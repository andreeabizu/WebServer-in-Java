package WebServer;
public class Main {

    public static void main(String[] arg) throws InterruptedException {


          WebServer server = new WebServer();
          Thread initial = new Thread(new Runnable() {
                @Override
                public void run() {
                    server.setPort(8080);

                }
            });
            initial.start();

            Thread.sleep(4000);
            System.out.println("acum");

        Thread.sleep(1000);
           server.wakeUp();
        Thread.sleep(4000);
        server.maintenance();
        Thread.sleep(4000);
        server.stop();
        Thread.sleep(4000);
        Thread initial1 = new Thread(new Runnable() {
            @Override
            public void run() {
                server.setPort(8081);

            }
        });
        initial1.start();
       Thread.sleep(4000);
        System.out.println("acum");

        Thread.sleep(1000);
        Thread initial2 = new Thread(new Runnable() {
            @Override
            public void run() {
                server.setPort(8080);

            }
        });
        initial2.start();

    }
}


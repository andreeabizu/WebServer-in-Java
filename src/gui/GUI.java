package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import test.WebServer;

public class GUI extends JFrame {

  private WebServer webServer;
  private JLabel status;
  private JLabel address;
  private JLabel port;
  private String state = "Stopped";
  private JCheckBox checkBox;
  private JTextField portConfig;
  private JTextField rootDir;
  private JTextField maintenanceDir;
  private JButton load_root;
  private JButton load_maintenance;


  public GUI() {

    webServer = new WebServer();
    webServer.setPort(webServer.getPort());
    Container cp = this.getContentPane();
    cp.setLayout(new BorderLayout());

    JPanel info = new JPanel(new GridLayout(3,2,10,10));
    JPanel control = new JPanel(new GridLayout(2,1,30,15));
    JPanel configuration = new JPanel(new GridLayout(3,4,10,30));


    TitledBorder infoTitle = BorderFactory.createTitledBorder("Web Server information");
    info.setBorder(infoTitle);

    JLabel labelStatus = new JLabel("Server status:");
    status = new JLabel("not running");
    JLabel labelAddress = new JLabel("Server address:");
    address = new JLabel("not running");
    JLabel labelPort = new JLabel("Server listening port:");
    port = new JLabel("not running");


    info.add(labelStatus);
    info.add(status);
    info.add(labelAddress);
    info.add(address);
    info.add(labelPort);
    info.add(port);


    TitledBorder controlTitle = BorderFactory.createTitledBorder("Web Server control");
    control.setBorder(controlTitle);

    JButton stateButton = new JButton("Start Server");
    checkBox = new JCheckBox("Switch to maintenance mode");
    checkBox.setEnabled(false);


    control.add(stateButton);
    control.add(checkBox);

    TitledBorder configurationTitle = BorderFactory.createTitledBorder("Web Server configuration");
    configuration.setBorder(configurationTitle);

    JLabel portConfigLabel  = new JLabel("Server listening on port ");
    portConfig = new JTextField(webServer.getPort()+"");
    portConfig.setEnabled(true);


    JLabel rootDirLabel = new JLabel("Web root directory");
    rootDir = new JTextField("C:\\Users\\unu\\vvs\\WebServer\\root");
    rootDir.setEnabled(true);

    JLabel maintenanceDirLabel = new JLabel("Maintenance directory");
    maintenanceDir = new JTextField("C:\\Users\\unu\\vvs\\WebServer\\maintenance");
    maintenanceDir.setEnabled(true);

    load_root = new JButton("...");
    load_maintenance = new JButton("...");

    JLabel x = new JLabel("X");
    x.setForeground(Color.RED);
    JLabel checkMark = new JLabel("✓");
    checkMark.setForeground(Color.GREEN);

    configuration.add(portConfigLabel);
    configuration.add(portConfig);
    JLabel error = new JLabel("");
    configuration.add(error);
    configuration.add(new JLabel(""));
    configuration.add(rootDirLabel);
    configuration.add(rootDir);
    configuration.add(load_root);
    configuration.add(checkMark);
    configuration.add(maintenanceDirLabel);
    configuration.add(maintenanceDir);
    configuration.add(load_maintenance);
    configuration.add(x);

    stateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String s = stateButton.getText();

        if(s.equals("Start Server")) {
          webServer.wakeUp();
          portConfig.setText(webServer.getPort()+"");
          error.setText("");
          change("running...",false,false,true,true,"Running");
          stateButton.setText("Stop Server");
          address.setText(webServer.getAddress());
          port.setText(webServer.getPort()+"");
        }
        else
        if(s.equals("Stop Server")){
          webServer.stop();
          change("not running",true,true,false,true,"Stopped");
          stateButton.setText("Start Server");
          address.setText("not running");
          port.setText("not running");
          checkBox.setSelected(false);
        }
      }
    });

    checkBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(checkBox.isSelected()){
          webServer.maintenance();
          address.setText(webServer.getAddress());
          change("maintenance",true,false,true,false,"Maintenance"); }
        else{
          webServer.wakeUp();
          change("running",false,false,true,true,"Running");
          address.setText(webServer.getAddress());
          port.setText(webServer.getPort()+"");
        }
      }
    });


    portConfig.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int port = 0;
        error.setText("");
        try {
          port = Integer.parseInt(portConfig.getText());
          webServer.setPort(port);
        }catch(NumberFormatException numberException) {
          error.setForeground(Color.red);
          error.setText("It is not a number");
        } catch(IllegalArgumentException arg) {
          error.setForeground(Color.red);
          error.setText("Out of range");
        }
      }
    });

    load_root.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setFolder(load_root,rootDir);
        webServer.changeRoot(new File(rootDir.getText()));
      }
    });

    load_maintenance.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setFolder(load_maintenance,maintenanceDir);
        webServer.changeMaintenanceRoot(new File(maintenanceDir.getText()));
      }
    });



    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(info);
    panel.add(control);


    cp.add(panel,BorderLayout.NORTH);
    cp.add(configuration,BorderLayout.CENTER);

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setTitle("VVS WebServer - ["+state+"]");
    this.setSize(600,300);
    this.setVisible(true);
  }

  private void setFolder(JButton button, JTextField tf) {

    JFileChooser fileChooser = new JFileChooser(new File("C:\\Users\\unu\\vvs\\WebServer"));
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int result = fileChooser.showOpenDialog(button);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      tf.setText(selectedFile.toString());
    }
  }

  private void change(String status,boolean root,boolean port,boolean checkBox,boolean maintenance,String state)
  {
    this.status.setText(status);
    rootDir.setEnabled(root);
    load_root.setEnabled(root);
    portConfig.setEnabled(port);
    load_maintenance.setEnabled(maintenance);
    maintenanceDir.setEnabled(maintenance);
    this.checkBox.setEnabled(checkBox);
    this.state = state;
    setTitle("VVS WebServer - ["+state+"]");

  }
  public static void main(String[] args){
    new GUI();
  }

}

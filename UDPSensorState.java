import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

public class UDPSensorState extends JFrame {
    static public final long serialVersionUID = 1L;
    static HashMap<String,JRadioButton> indicators = new HashMap<String,JRadioButton>();
    InetSocketAddress destination_address;

    public static void main( String[] args ) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UDPSensorState();
            }
        });
    }

    public UDPSensorState() {
        super("Send Text via UDP");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel( );
        content.setLayout( new BoxLayout( content, BoxLayout.Y_AXIS) );
        SocketAddressPanel svr = new SocketAddressPanel();
        content.add( svr );
        String[] sw ={"SW2", "SW3"};
        content.add( new SwPanel("MBED", sw) );

        String[] appsw ={"Up","Down","Left","Right","Center"};
        content.add( new SwPanel("App Shield", appsw) );

        this.setContentPane(content);
        this.pack();
        this.setVisible(true);
        new Thread( svr ).start();
    }

    static class SocketAddressPanel extends JPanel implements Runnable {
        public static final long serialVersionUID = 1L;
        static public JLabel ip;
        static private JLabel port;
        static InetAddress Iam;
        static public InetSocketAddress getSocketAddress() {
            return new InetSocketAddress(
                            ip.getText(),
                            Integer.parseInt( port.getText() )
                        );
        }

        public SocketAddressPanel() {
            super( new FlowLayout(FlowLayout.LEFT, 5, 0) );
            try{
            setBorder( BorderFactory.createTitledBorder("Internet Socket Address (listening)") );

            add( new JLabel("IP:") );
            ip = new JLabel("192.168.");
            Iam = InetAddress.getLocalHost();
            ip.setText(Iam.getHostAddress());

            add(ip);
            add( new JLabel(" port:") );
            port = new JLabel("65500");
            add(port);
        }catch(Exception e){}}

        public void run(){
            try{
                byte[] buffer = new byte[512];
                DatagramSocket socket = new DatagramSocket(getSocketAddress());
                while(true){
                    DatagramPacket msg = new DatagramPacket(buffer, buffer.length);
                    System.out.println("waiting...");
                    socket.receive(msg);
                    String message = new String(msg.getData());
                    System.out.println(message);
                    message.trim();
                    String[] kv = message.split(":");
                    System.out.println("key="+kv[0]+"   ");
                    System.out.println("value="+kv[1]+"  ");
                    boolean state = kv[1].startsWith("pressed");
                    indicators.get(kv[0]).setSelected(state);
                }
            }catch(Exception e){
                System.err.println(e);
            }
        }
    }

    class SwPanel extends JPanel  {
        public static final long serialVersionUID = 1L;
        String title;
        public SwPanel(String title, String[] sw) {
            this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
            this.setBorder( BorderFactory.createTitledBorder(title) );
            this.title = title;

            for(String d : sw){
                JRadioButton b = new JRadioButton(d);
                indicators.put(d,b);
                this.add(b);
            }
        }
    }
}

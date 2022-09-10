import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Smtp{

    public static void main(String[] args) throws UnknownHostException {

        InetAddress addr = InetAddress.getLocalHost();
        String ehlo = "EHLO "+addr.getHostAddress();
        String mailfrom = "MAIL FROM: <president@whitehouse.gov>";
        String recptto = "RCPT TO: <professor@example.com>";
        String data = "DATA";
        String end = ".";

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try{
            Socket socket = new Socket(hostName, portNumber);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            System.out.println(br.readLine());
            String[] output1 = br.readLine().split(" ");
            if(output1[0].equals("220")){
                pw.println (ehlo);
                String reply1 = br.readLine ();
                System.out.println (reply1);
                String[] output2 = reply1.split("-");
                if(output2[0].equals("250")){
                    pw.println (mailfrom);
                    String reply2 = br.readLine ();
                    System.out.println (reply2);
                    String[] output3 = reply2.split(" ");
                    if(output3[0].equals("250")){
                        pw.println (recptto);
                        String reply3 = br.readLine ();
                        System.out.println (reply3);
                        String[] output4 = reply3.split(" ");
                        if(output4[0].equals("250")){
                            pw.println (data);
                            String reply4 = br.readLine ();
                            System.out.println (reply4);
                            String[] output5 = reply4.split(" ");
                            if(output5[0].equals("354")){
                                pw.println (end);
                                String reply5 = br.readLine ();
                                System.out.println (reply5);
                            }
                        }
                    }
                }
                pw.flush();
                br.close();
                os.close();
                pw.close();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
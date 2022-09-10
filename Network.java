import java.io.*;
import java.net.Socket;

class Network {
    public static void main(String[] args) {
        Socket socket;
        try {
            socket = new Socket("http://cr.yp.to", 80);
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info;
            while((info = br.readLine()) != null){
                System.out.println(info);
            }
            br.close();
            is.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Httpproxy{
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true){
            Socket socket = serverSocket.accept();
            try {
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String path = bufferedReader.readLine().split(" ")[1].substring(1);
                if (path.equals("login")){
                    HttpClient client = HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.ALWAYS)
                            .build();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8081/login"))
                            .GET()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());

                    HttpClient client1 = HttpClient.newHttpClient();
                    String encodedAuth = Base64.getEncoder()
                            .encodeToString(("username" + ":" + "password")
                                    .getBytes(StandardCharsets.UTF_8));
                    HttpRequest request1 = HttpRequest.newBuilder()
                            .uri(URI.create("http://cr.yp.to/"))
                            .header("Authorization", "Basic␣" + encodedAuth)
                            .build();
                    HttpResponse<String> response1 = client1.send(request1, HttpResponse.BodyHandlers.ofString());

                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

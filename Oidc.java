import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Oidc {
    public static void main(String[] args) throws IOException {

        String RHOST = args[0];
        int RPORT = Integer.parseInt(args[1]);
        String PHOST = args[2];
        int PPORT = Integer.parseInt(args[3]);
        String client_id = "oidc";
        String client_secret = "app-secret";
        String redirect_uri = "http://"+RHOST+":"+RPORT+"/auth/callback";
        String code_uri = "http://"+PHOST+":"+PPORT+"/authorize?client_id="+client_id+"&redirect_uri="+redirect_uri+"&response_type=code&scope=openid&state=STATE"+"";
        String token_uri = "http://"+PHOST+":"+PPORT+"/oauth/token HTTP/1.1?grant_type=authorization_code&client_id="+client_id+"&client_secret="+client_secret+"&code=CODE&redirect_uri="+redirect_uri+"";
        String user_info_uri = "http://"+PHOST+":"+PPORT+"/userinfo";

        ServerSocket serverSocket = new ServerSocket(RPORT);
        while (true){
            Socket socket = serverSocket.accept();
            try {
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String request = bufferedReader.readLine().split(" ")[0];
                if (request.equals("GET")){
                    HttpClient client = HttpClient.newBuilder().build();
                    HttpRequest getCode = HttpRequest.newBuilder()
                            .uri(URI.create(code_uri))
                            .GET()
                            .build();
                    HttpResponse<String> code = client.send(getCode, HttpResponse.BodyHandlers.ofString());
                    if (code.statusCode() == 302){
                        String body = code.body();
                        System.out.println(body);
                        if (body.contains("/login")){
                            HttpRequest getToken = HttpRequest.newBuilder()
                                    .uri(URI.create(token_uri))
                                    .header("Content-Type","application/x-www-form-urlencoded")
                                    .POST(HttpRequest.BodyPublishers.ofString(""))
                                    .build();
                            HttpResponse<String> token = client.send(getToken, HttpResponse.BodyHandlers.ofString());
                            String encodedAuth = Base64.getEncoder()
                                    .encodeToString(("oidc" + ":" + "app-secret")
                                    .getBytes(StandardCharsets.UTF_8));
                            HttpRequest getInfo = HttpRequest.newBuilder()
                                    .uri(URI.create(user_info_uri))
                                    .header("Authorization", "Basic " + token.body() + encodedAuth)
                                    .build();
                            HttpResponse<String> info = client.send(getInfo, HttpResponse.BodyHandlers.ofString());
                            System.out.println(info.body());
                        }
                    }
                }
                } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

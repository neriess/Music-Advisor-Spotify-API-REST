package music.advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OAuthServer {

    private boolean isAuthorized;
    private HttpServer server;
    private final String clientID = "b51b331290d24b3680681aa0990f552c";
    private final String clientSecret = "b0d7b287987f40749747158bd6807940";
    private final String spotifyAccessUri = "https://accounts.spotify.com";
    private final String redirectUri = "http://localhost:8080";
    private final String grantType = "authorization_code";

    public OAuthServer() {
        isAuthorized = false;
    }

    public void authorizeUser() throws IOException {
        startServer();
        getCode();
        httpHandler();
    }

    public void httpHandler() {
        server.createContext("/",
                exchange -> {
                    String clientMessage;
                    String query = exchange.getRequestURI().getQuery();

                    String code = query.substring(query.indexOf("=") + 1);
                    if (code.contains("denied")) {
                        clientMessage = "Authorization code not found. Try again.";
                        System.out.println("access denied");
                    } else {
                        clientMessage = "Got the code. Return back to your program.";
                        System.out.println("code received");
                        getAccessToken(code);
                    }
                    exchange.sendResponseHeaders(200, clientMessage.length());
                    exchange.getResponseBody().write(clientMessage.getBytes());
                    exchange.getResponseBody().close();
                    stopServer();
                }
        );
    }

    private void getAccessToken(String code) throws IOException {
        System.out.println("making http request for access_token...");

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(spotifyAccessUri + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "client_id=" + clientID
                                + "&client_secret=" + clientSecret
                                + "&redirect_uri=" +  redirectUri
                                + "&code=" + code
                                + "&grant_type=" + grantType
                ))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            SpotifyClient.setAccessToken(jo.get("access_token").getAsString());
            System.out.println("Success!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isAuthorized = true;
    }

    private void startServer() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.start();
    }

    private void stopServer() {
        server.stop(1);
    }

    private void getCode() {
        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=" +
                "%s&redirect_uri=%s&response_type=code%n", spotifyAccessUri, clientID, redirectUri);
        System.out.println("waiting for code...");
    }

    public void notAuthorized() {
        System.out.println("Please, provide access for application.");
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}

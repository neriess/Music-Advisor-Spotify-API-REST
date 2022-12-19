package music.advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SpotifyClient {

    private static String accessToken;
    private final String spotifyApiPath = "https://api.spotify.com";
    private final String NEW_RELEASE_ENDPOINT = "/v1/browse/new-releases";
    private final String FEATURED_ENDPOINT = "/v1/browse/featured-playlists";
    private final String CATEGORIES_ENDPOINT = "/v1/browse/categories";
    private final String PLAYLIST_ENDPOINT = "/v1/browse/categories/%s/playlists";
    HttpClient httpClient;

    public SpotifyClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    private JsonObject request(String endPoint) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(endPoint))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public JsonArray requestNewRelease() {
        String endPoint = spotifyApiPath + NEW_RELEASE_ENDPOINT;
        JsonObject object = request(endPoint);
        return object.getAsJsonObject("albums")
                .getAsJsonArray("items");
    }

    public JsonArray requestFeatured() {
        String endPoint = spotifyApiPath + FEATURED_ENDPOINT;
        JsonObject object = request(endPoint);
        return object.getAsJsonObject("playlists")
                .getAsJsonArray("items");
    }

    public JsonArray requestCategories() {
        String endPoint = spotifyApiPath + CATEGORIES_ENDPOINT;
        JsonObject object = request(endPoint);
        return object.getAsJsonObject("categories")
                .getAsJsonArray("items");
    }

    public JsonArray requestPlaylist(String categoryId) {
        String endPoint = spotifyApiPath + String.format(PLAYLIST_ENDPOINT, categoryId);
        JsonObject object = request(endPoint);
        try {
            return object.getAsJsonObject("playlists")
                    .getAsJsonArray("items");
        } catch (Exception e) {
            System.out.println("Specified id doesn't exist");
        }
        return null;
    }

    public static void setAccessToken(String accessToken) {
        SpotifyClient.accessToken = accessToken;
    }
}

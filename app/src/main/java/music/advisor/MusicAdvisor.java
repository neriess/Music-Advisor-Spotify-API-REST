package music.advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class MusicAdvisor implements IMusicAdvisor{

    SpotifyClient spotifyClient;

    public MusicAdvisor() {
        this.spotifyClient = new SpotifyClient();
    }

    @Override
    public void showNew() {
        JsonArray array = spotifyClient.requestNewRelease();
        for (JsonElement elem : array) {
            String name = elem.getAsJsonObject().get("name").getAsString();
            System.out.println(name);
            JsonArray artistList = elem.getAsJsonObject().getAsJsonArray("artists");
            StringBuilder strb = new StringBuilder("[");
            for (JsonElement artist : artistList) {
                strb.append(artist.getAsJsonObject().get("name").getAsString());
                strb.append(", ");
            }
            strb.append("]");
            System.out.println(strb.toString().replace(", ]", "]"));
            String url = elem.getAsJsonObject().get("external_urls")
                    .getAsJsonObject().get("spotify")
                    .getAsString();
            System.out.println(url);
            System.out.println();
        }
    }

    @Override
    public void showFeatured() {
        JsonArray array = spotifyClient.requestFeatured();
        for (JsonElement elem : array) {
            String name = elem.getAsJsonObject().get("name").getAsString();
            System.out.println(name);
            String url = elem.getAsJsonObject().get("external_urls")
                    .getAsJsonObject().get("spotify")
                    .getAsString();
            System.out.println(url);
            System.out.println();
        }
    }

    @Override
    public void showCategories() {
        JsonArray array = spotifyClient.requestCategories();
        for (JsonElement elem : array) {
            String name = elem.getAsJsonObject().get("name").getAsString();
            System.out.println(name);
        }
    }

    @Override
    public void showPlaylist(String categoryName) {
        JsonArray categoryArray = spotifyClient.requestCategories();
        String categoryId = null;
        for (JsonElement elem : categoryArray) {
            String name = elem.getAsJsonObject().get("name").getAsString();
            if (name.equalsIgnoreCase(categoryName)) {
                categoryId = elem.getAsJsonObject().get("id").getAsString();
                break;
            }
        }
        if (categoryId == null) {
            System.out.println("Unknown category name.");
        } else {
            JsonArray playlistArray = spotifyClient.requestPlaylist(categoryId);
            if (playlistArray == null) return; //vymazat tento riadok
            for (JsonElement elem : playlistArray) {
                String name = elem.getAsJsonObject().get("name").getAsString();
                System.out.println(name);
                String url = elem.getAsJsonObject().get("external_urls")
                        .getAsJsonObject().get("spotify")
                        .getAsString();
                System.out.println(url);
                System.out.println();
            }
        }
    }

    @Override
    public void exit() {
        System.out.println("---GOODBYE!---");
        System.exit(0);
    }
}

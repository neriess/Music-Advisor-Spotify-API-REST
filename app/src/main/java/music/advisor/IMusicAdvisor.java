package music.advisor;

public interface IMusicAdvisor {
    void showNew();
    void showFeatured();
    void showCategories();
    void showPlaylist(String type);
    void exit();
}

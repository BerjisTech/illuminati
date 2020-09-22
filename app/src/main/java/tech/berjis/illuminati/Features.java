package tech.berjis.illuminati;

public class Features {
    String title, description, id, image, author;
    long time;

    public Features(String title, String description, String id, String image, String author, long time) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.image = image;
        this.author = author;
        this.time = time;
    }

    public Features() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

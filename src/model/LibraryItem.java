package model;

public abstract class LibraryItem {
    private String itemId;
    private String title;
    private String author;
    private String yearPublished;
    private boolean isAvailable;
    /**
     * Constructor of a Library Item
     * @param itemId The unique ID of item
     * @param title The title of item
     * @param author The author of item
     * @param yearPublished The year item was published
     */
    public LibraryItem(String itemId, String title, String author, String yearPublished) {
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
        this.isAvailable = true;
    }

    public abstract String getItemType();
    /**
     * Getter for item ID
     * @return The ID of library item
     */
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(String yearPublished) {
        this.yearPublished = yearPublished;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "[" + getItemType() + "] "
                + "ID: " + itemId
                + "| Title: " + title
                + "| Author: " + author
                + "| Year Published: " + yearPublished
                + "| Available: " + (isAvailable ? "Yes" : "No");
    }

    public String toFileString() {
        return itemId + "," + title + "," + author + "," + yearPublished + "," + isAvailable;
    }
}

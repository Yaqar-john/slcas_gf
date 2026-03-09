package model;

import java.time.LocalDate;

public class Magazine extends LibraryItem implements Borrowable {
    private int issueNumber;
    private String monthYear;
    private int numberOfPages;

    private String borrowedByUserId;
    private String dueDate;
    private int borrowCount;

    public Magazine (String itemId, String title, String author, String yearPublished,
                String genre, String isbn, int numberOfPages) {
        super(itemId, title, author, yearPublished);
        this.genre = genre;
        this.isbn = isbn;
        this.numberOfPages = numberOfPages;

        this.borrowedByUserId = null;
        this.dueDate = null;
        this.borrowCount = 0;
    }

    public String getItemType() { return "Magazine"; }

    public void borrowItem(String userId) {
        setAvailable(false);
        this.borrowedByUserId = userId;
        LocalDate dueDateObj = LocalDate.now().plusDays(14);
        this.dueDate = dueDateObj.toString();
        this.borrowCount++;
    }

    public void returnItem() {
        setAvailable(true);
        this.borrowedByUserId = null;
        this.dueDate = null;
    }

    // Getters
    public String getBorrowedByUserID() { return borrowedByUserId; }
    public String getDueDate() { return dueDate; }
    public int getBorrowCount() { return borrowCount; }

    public String getGenre() { return genre; }
    public String getIsbn() { return isbn; }
    public int getNumberOfPages() { return numberOfPages; }

    // Setters
    public void setGenre(String genre) { this.genre = genre; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setNumberOfPages(int numberOfPages) { this.numberOfPages = numberOfPages; }

    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }
    public void setBorrowedByUserId(String borrowedByUserId) { this.borrowedByUserId = borrowedByUserId; }

    @Override
    public String toString() {
        return super.toString()
                + "| Genre: " + genre
                + "| ISBN: " + isbn
                + "| Pages: " + numberOfPages
                + "| BorrowedBy: " + (borrowedByUserId != null ? borrowedByUserId : "None")
                + "| DueDate: " + (dueDate != null ? dueDate : "None")
                + "| BorrowCount: " + borrowCount;
    }

    /**
     * FileHandler uses this to save Magazine data
     * @return CSV format of Magazine
     */
    @Override
    public String toFileString() {
        return "Magazine,"
                + super.toFileString()
                + "," + genre
                + "," + isbn
                + "," + numberOfPages
                + "," + (borrowedByUserId != null ? borrowedByUserId : "null")
                + "," + (dueDate != null ? dueDate : "null")
                + "," + borrowCount;
    }

}

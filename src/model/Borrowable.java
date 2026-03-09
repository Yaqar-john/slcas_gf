package model;

public interface Borrowable {
    void borrowItem(String userId);
    void returnItem();
    String getBorrowedByUserID();
    String getDueDate();
    int getBorrowCount();
}

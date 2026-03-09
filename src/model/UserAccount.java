package model;

import java.util.ArrayList;

public class UserAccount {
    private String userId;
    private String fullName;
    private String email;
    private String password;
    private boolean isAdmin;

    private ArrayList<String> currentlyBorrowedIds;
    private ArrayList<String> borrowingHistory;

    public static final int MAX_BORROW_LIMIT = 5;

    public UserAccount(String userId, String fullName, String email,
                       String password, boolean isAdmin) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;

        this.currentlyBorrowedIds = new ArrayList<>();
        this.borrowingHistory = new ArrayList<>();
    }

    public void borrowItem(String itemId) {
        currentlyBorrowedIds.add(itemId);
        borrowingHistory.add(itemId);
    }

    public void returnItem(String itemId) {
        currentlyBorrowedIds.remove(itemId);
    }

    public boolean canBorrow() {
        return currentlyBorrowedIds.size() < MAX_BORROW_LIMIT;
    }

    public boolean hasBorrowed(String itemId) {
        return currentlyBorrowedIds.contains(itemId);
    }

    public int getCurrentBorrowCount() {
        return currentlyBorrowedIds.size();
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    // File handler
    public void addToCurrentlyBorrowed(String itemId) {
        currentlyBorrowedIds.add(itemId);
    }
    public void addToHistory(String itemId) {
        borrowingHistory.add(itemId);
    }

    @Override
    public String toString() {
        return "UserAccount{"
                + "userId='" + userId + ", fullName=" + fullName + ", email=" + email
    }

    public String toFileString() {
        StringBuilder currentBorrowed = new StringBuilder();
        for (int i = 0; i < currentlyBorrowedIds.size(); i++) {
            currentBorrowed.append(currentlyBorrowedIds.get(i));
            if (i !=  currentlyBorrowedIds.size() - 1) {
                currentBorrowed.append("|");
            }
        }

        StringBuilder history = new StringBuilder();
        for (int i = 0; i < borrowingHistory.size(); i++) {
            history.append(borrowingHistory.get(i));
            if (i !=  borrowingHistory.size() - 1) {
                history.append("|");
            }
        }

        return userId
                + ", " + fullName
                + ", " + email
                + ", " + password
                + ", " + isAdmin
                + ", " + (!currentBorrowed.isEmpty() ? currentBorrowed.toString() : "none")
                + ", " + (!history.isEmpty() ? history.toString() : "none");
    }

}

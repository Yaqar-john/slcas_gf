package controller;

import model.LibraryItem;
import model.UserAccount;
import java.util.ArrayList;

public class LibraryDatabase {

    private ArrayList<LibraryItem> items;
    private ArrayList<UserAccount> users;

    public LibraryDatabase() {
        items = new ArrayList<>();
        users = new ArrayList<>();
    }

    public void addItem(LibraryItem item) {
        items.add(item);
    }

    public void addUser(UserAccount user) {
        users.add(user);
    }

    public ArrayList<LibraryItem> getAllItems() {
        return items;
    }

    public ArrayList<UserAccount> getAllUsers() {
        return users;
    }

    public LibraryItem searchItemById(String itemId) {
        for (LibraryItem item : items) {
            if (item.getItemId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    public UserAccount searchUserById(String userId) {
        for (UserAccount user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public boolean removeItem(String itemId) {
        LibraryItem item = searchItemById(itemId);
        if (item != null) {
            items.remove(item);
            return true;
        }
        return false;
    }

    public boolean removeUser(String userId) {
        UserAccount user = searchUserById(userId);
        if (user != null) {
            users.remove(user);
            return true;
        }
        return false;
    }
}
package controller;

import model.LibraryItem;
import java.util.ArrayList;

public class SearchEngine {

    private LibraryDatabase database;

    public SearchEngine(LibraryDatabase database) {
        this.database = database;
    }

    // search by title using linear search
    public ArrayList<LibraryItem> linearSearchByTitle(String query) {
        ArrayList<LibraryItem> results = new ArrayList<>();
        ArrayList<LibraryItem> items = database.getAllItems();

        for (LibraryItem item : items) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                results.add(item);
            }
        }
        return results;
    }

    // search by author using linear search
    public ArrayList<LibraryItem> linearSearchByAuthor(String query) {
        ArrayList<LibraryItem> results = new ArrayList<>();
        ArrayList<LibraryItem> items = database.getAllItems();

        for (LibraryItem item : items) {
            if (item.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                results.add(item);
            }
        }
        return results;
    }

    // search by item type e.g Book, Magazine, Journal
    public ArrayList<LibraryItem> linearSearchByType(String type) {
        ArrayList<LibraryItem> results = new ArrayList<>();
        ArrayList<LibraryItem> items = database.getAllItems();

        for (LibraryItem item : items) {
            if (item.getItemType().equalsIgnoreCase(type)) {
                results.add(item);
            }
        }
        return results;
    }

    // binary search by title - list must be sorted before calling this
    public LibraryItem binarySearchByTitle(ArrayList<LibraryItem> sortedItems, String query) {
        int low = 0;
        int high = sortedItems.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            String midTitle = sortedItems.get(mid).getTitle().toLowerCase();
            int comparison = midTitle.compareTo(query.toLowerCase());

            if (comparison == 0) {
                return sortedItems.get(mid);
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    // recursive search by title
    public LibraryItem recursiveSearchByTitle(ArrayList<LibraryItem> items, String query, int index) {
        if (index >= items.size()) {
            return null;
        }
        if (items.get(index).getTitle().toLowerCase().contains(query.toLowerCase())) {
            return items.get(index);
        }
        return recursiveSearchByTitle(items, query, index + 1);
    }

    // recursive search by author
    public LibraryItem recursiveSearchByAuthor(ArrayList<LibraryItem> items, String query, int index) {
        if (index >= items.size()) {
            return null;
        }
        if (items.get(index).getAuthor().toLowerCase().contains(query.toLowerCase())) {
            return items.get(index);
        }
        return recursiveSearchByAuthor(items, query, index + 1);
    }
}
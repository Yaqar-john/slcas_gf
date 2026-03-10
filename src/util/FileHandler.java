package util;

import model.Book;
import model.Journal;
import model.LibraryItem;
import model.Magazine;
import model.UserAccount;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;


public class FileHandler {

    // Base folder for your CSV files
    private static final String DATA_DIR = "data/";

    public static void saveItem (LibraryItem item) {
        String itemType = item.getItemType();
        String fileName = "";
        switch (itemType) {
            case "Book":
                fileName = "books.csv";
                break;
            case "Magazine":
                fileName = "magazines.csv";
                break;
            case "Journal":
                fileName = "journals.csv";
                break;
        }
        try{
            FileWriter fw = new FileWriter(DATA_DIR + fileName, true);
            PrintWriter pw = new PrintWriter (fw);
            pw.println(item.toFileString());
            pw.close();
        }
        catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    public static void saveUser (UserAccount user) {
        String fileName = "users.csv";
        try{
            FileWriter fw = new FileWriter(DATA_DIR + fileName, true);
            PrintWriter pw = new PrintWriter (fw);
            pw.println(user.toFileString());
            pw.close();
        }
        catch (IOException e) {
            System.out.println("Error saving file: " + e);
        }
    }

    public static ArrayList<UserAccount> loadUsers (){
        ArrayList<UserAccount> users = new ArrayList<>();
        try{
            File file = new File(DATA_DIR + "users.csv");
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] userData = line.split(",");
                UserAccount user = new UserAccount(userData[0].trim(), userData[ 1].trim(), userData[2].trim(), userData[3].trim(), Boolean.parseBoolean(userData[4].trim()));
                users.add(user);
            }
            sc.close();
        }
        catch (IOException e) {
        System.out.println("Error loading file:" + e);
        }
        return users;
    }

    public static ArrayList<LibraryItem> loadItems (){
        ArrayList<LibraryItem> items = new ArrayList<>();
        try{
            String[] fileNames = {"books.csv", "magazines.csv", "journals.csv"};
            for (String fileName : fileNames) {
                File file = new File(DATA_DIR + fileName);
                if (file.exists()) {
                    Scanner sc = new Scanner(file);
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        String[] itemData = line.split(",");
                        switch (itemData[0].trim()) {
                            case "Book":
                                Book book = new Book(itemData[1].trim(), itemData[2].trim(), itemData[3].trim(), itemData[4].trim(), itemData[5].trim(), itemData[6].trim(), Integer.parseInt(itemData[7].trim()));
                                items.add(book);
                                break;
                            case "Magazine":
                                Magazine magazine = new Magazine(itemData[1].trim(), itemData[2].trim(), itemData[3].trim(), itemData[4].trim(), itemData[5].trim(), itemData[6].trim(), Integer.parseInt(itemData[7].trim()));
                                items.add(magazine);
                                break;
                            case "Journal":
                                Journal journal = new Journal(itemData[1].trim(), itemData[2].trim(), itemData[3].trim(), itemData[4].trim(), itemData[5].trim(), itemData[6].trim(), Integer.parseInt(itemData[7].trim()));
                                items.add(journal);
                                break;
                        }
                    }
                    sc.close();
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error loading file:" + e);
        }
        return items;
    }
}
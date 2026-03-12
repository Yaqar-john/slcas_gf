package controller;


import model.LibraryItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortEngine {


    /**
     * Sorts the list using Merge Sort algorithm.
     */
    public static void mergeSort(List<LibraryItem> items, Comparator<LibraryItem> comp) {
        if (items == null || comp == null || items.size() <= 1) return;
        int mid = items.size() / 2;
        List<LibraryItem> left = new ArrayList<>(items.subList(0, mid));
        List<LibraryItem> right = new ArrayList<>(items.subList(mid, items.size()));

        mergeSort(left, comp);
        mergeSort(right, comp);
        merge(items, left, right, comp);
    }

    private static void merge(List<LibraryItem> items, List<LibraryItem> left, List<LibraryItem> right, Comparator<LibraryItem> comp) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (comp.compare(left.get(i), right.get(j)) <= 0) {
                items.set(k++, left.get(i++));
            } else {
                items.set(k++, right.get(j++));
            }
        }
        while (i < left.size()) {
            items.set(k++, left.get(i++));
        }
        while (j < right.size()) {
            items.set(k++, right.get(j++));
        }
    }

    /**
     * Sorts the list using Quick Sort algorithm.
     */


    public static void quickSort(List<LibraryItem> items, Comparator<LibraryItem> comp, int low, int high) {
        if (low < high) {
            int pi = partition(items, comp, low, high);
            quickSort(items, comp, low, pi - 1);
            quickSort(items, comp, pi + 1, high);
        }
    }

    private static int partition(List<LibraryItem> items, Comparator<LibraryItem> comp, int low, int high) {
        LibraryItem pivot = items.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comp.compare(items.get(j), pivot) <= 0) {
                i++;
                Collections.swap(items, i, j);
            }
        }
        Collections.swap(items, i + 1, high);
        return i + 1;
    }

    public static void selectionSort(List<LibraryItem> items, Comparator<LibraryItem> comp) {
    }

    void main() {
    }

}


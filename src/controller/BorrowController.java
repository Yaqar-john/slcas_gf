package controller;

import model.*;
import util.IDGenerator;
import util.FileHandler;

import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Controller responsible for managing all borrowing and return operations,
 * including waitlist queues, overdue processing, and transaction history.
 */
public class BorrowController {
    private static BorrowController instance;
    
    // Core data structures
    private Map<String, BorrowTransaction> activeBorrowings;
    private Map<String, Queue<Reservation>> reservationQueues;
    private Stack<BorrowOperation> undoStack;
    private List<BorrowTransaction> borrowingHistory;
    
    // Configuration
    private static final int MAX_BORROW_DAYS = 14;
    private static final double OVERDUE_FINE_PER_DAY = 0.50;
    private static final int MAX_BORROW_LIMIT = 5;
    
    /**
     * Private constructor for singleton pattern
     */
    private BorrowController() {
        this.activeBorrowings = new HashMap<>();
        this.reservationQueues = new HashMap<>();
        this.undoStack = new Stack<>();
        this.borrowingHistory = new ArrayList<>();
        
        // Load existing data if available
        loadBorrowingData();
    }
    
    /**
     * Singleton instance getter
     */
    public static synchronized BorrowController getInstance() {
        if (instance == null) {
            instance = new BorrowController();
        }
        return instance;
    }
    
    /**
     * Borrow a library item
     */
    public BorrowResult borrowItem(String userId, String itemId) {
        try {
            // Validate user and item
            UserAccount user = findUserById(userId);
            LibraryItem item = findItemById(itemId);
            
            if (user == null) {
                return new BorrowResult(false, "User not found", null);
            }
            
            if (item == null) {
                return new BorrowResult(false, "Item not found", null);
            }
            
            // Check if user has reached borrowing limit
            if (getActiveBorrowCount(userId) >= MAX_BORROW_LIMIT) {
                return new BorrowResult(false, 
                    "User has reached maximum borrowing limit of " + MAX_BORROW_LIMIT, null);
            }
            
            // Check item availability
            if (!item.isAvailable()) {
                // Add to reservation queue
                addToReservationQueue(userId, itemId);
                return new BorrowResult(false, 
                    "Item not available. Added to reservation queue.", null);
            }
            
            // Check if item is borrowable
            if (!(item instanceof Borrowable)) {
                return new BorrowResult(false, 
                    "This item type cannot be borrowed", null);
            }
            
            // Create borrowing transaction
            BorrowTransaction transaction = new BorrowTransaction(
                IDGenerator.generateId("borrowTransaction", 3),
                userId,
                itemId,
                LocalDate.now(),
                LocalDate.now().plusDays(MAX_BORROW_DAYS)
            );
            
            // Update item status
            item.setAvailable(false);
            item.setBorrowerId(userId);
            
            // Add to active borrowings
            activeBorrowings.put(transaction.getTransactionId(), transaction);
            borrowingHistory.add(transaction);
            
            // Add to user's borrowing history
            user.addToBorrowingHistory(transaction);
            
            // Record for undo
            BorrowOperation operation = new BorrowOperation(
                BorrowOperation.Type.BORROW, transaction);
            undoStack.push(operation);
            
            // Save data
            saveBorrowingData();
            
            return new BorrowResult(true, "Item borrowed successfully", transaction);
            
        } catch (Exception e) {
            return new BorrowResult(false, "Error during borrowing: " + e.getMessage(), null);
        }
    }
    
    /**
     * Return a borrowed item
     */
    public BorrowResult returnItem(String transactionId) {
        try {
            BorrowTransaction transaction = activeBorrowings.get(transactionId);
            
            if (transaction == null) {
                return new BorrowResult(false, "Transaction not found", null);
            }
            
            LibraryItem item = findItemById(transaction.getItemId());
            UserAccount user = findUserById(transaction.getUserId());
            
            if (item == null) {
                return new BorrowResult(false, "Item not found in library", null);
            }
            
            // Calculate overdue fines if any
            LocalDate returnDate = LocalDate.now();
            double fine = calculateOverdueFine(transaction.getDueDate(), returnDate);
            
            // Update transaction
            transaction.setReturnDate(returnDate);
            transaction.setFine(fine);
            
            // Update item status
            item.setAvailable(true);
            item.setBorrowerId(null);
            
            // Remove from active borrowings
            activeBorrowings.remove(transactionId);
            
            // Check reservation queue for next user
            processReservationQueue(item.getId());
            
            // Record for undo
            BorrowOperation operation = new BorrowOperation(
                BorrowOperation.Type.RETURN, transaction);
            undoStack.push(operation);
            
            // Save data
            saveBorrowingData();
            
            String message = fine > 0 ? 
                "Item returned. Overdue fine: $" + String.format("%.2f", fine) : 
                "Item returned successfully";
            
            return new BorrowResult(true, message, transaction);
            
        } catch (Exception e) {
            return new BorrowResult(false, "Error during return: " + e.getMessage(), null);
        }
    }
    
    /**
     * Add user to reservation queue for an item
     */
    public void addToReservationQueue(String userId, String itemId) {
        reservationQueues.computeIfAbsent(itemId, k -> new LinkedList<>());
        
        Reservation reservation = new Reservation(
            IDGenerator.generateId("borrowController", 2),
            userId,
            itemId,
            LocalDate.now()
        );
        
        reservationQueues.get(itemId).offer(reservation);
        saveReservationData();
    }
    
    /**
     * Process reservation queue for an item when it becomes available
     */
    private void processReservationQueue(String itemId) {
        Queue<Reservation> queue = reservationQueues.get(itemId);
        
        if (queue != null && !queue.isEmpty()) {
            Reservation nextReservation = queue.poll();
            // In a real system, we'd send notification to user
            System.out.println("Item " + itemId + " is now available for user: " 
                + nextReservation.getUserId());
            
            // Auto-borrow for the next user? Or just notify?
            // For this implementation, we'll just notify
        }
        
        saveReservationData();
    }
    
    /**
     * Calculate overdue fine recursively
     */
    private double calculateOverdueFine(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate.isBefore(dueDate) || returnDate.isEqual(dueDate)) {
            return 0.0;
        }
        
        // Recursive calculation
        return calculateFineRecursive(dueDate, returnDate, 0.0);
    }
    
    /**
     * Recursive helper for fine calculation
     */
    private double calculateFineRecursive(LocalDate currentDate, LocalDate endDate, double accumulatedFine) {
        if (currentDate.isAfter(endDate)) {
            return accumulatedFine;
        }
        
        if (currentDate.isBefore(endDate)) {
            return calculateFineRecursive(
                currentDate.plusDays(1), 
                endDate, 
                accumulatedFine + OVERDUE_FINE_PER_DAY
            );
        }
        
        return accumulatedFine;
    }
    
    /**
     * Get count of active borrowings for a user
     */
    private int getActiveBorrowCount(String userId) {
        return (int) activeBorrowings.values().stream()
            .filter(t -> t.getUserId().equals(userId))
            .count();
    }
    
    /**
     * Undo last borrow/return operation
     */
    public BorrowResult undoLastOperation() {
        if (undoStack.isEmpty()) {
            return new BorrowResult(false, "No operations to undo", null);
        }
        
        try {
            BorrowOperation operation = undoStack.pop();
            
            if (operation.getType() == BorrowOperation.Type.BORROW) {
                // Undo borrow: return the item
                return returnItem(operation.getTransaction().getTransactionId());
            } else {
                // Undo return: re-borrow the item
                BorrowTransaction trans = operation.getTransaction();
                return borrowItem(trans.getUserId(), trans.getItemId());
            }
            
        } catch (Exception e) {
            return new BorrowResult(false, "Error during undo: " + e.getMessage(), null);
        }
    }
    
    /**
     * Get all active borrowings
     */
    public List<BorrowTransaction> getActiveBorrowings() {
        return new ArrayList<>(activeBorrowings.values());
    }
    
    /**
     * Get borrowings for a specific user
     */
    public List<BorrowTransaction> getUserBorrowings(String userId) {
        List<BorrowTransaction> userBorrowings = new ArrayList<>();
        
        for (BorrowTransaction trans : activeBorrowings.values()) {
            if (trans.getUserId().equals(userId)) {
                userBorrowings.add(trans);
            }
        }
        
        return userBorrowings;
    }
    
    /**
     * Get reservation queue for an item
     */
    public Queue<Reservation> getReservationQueue(String itemId) {
        return reservationQueues.getOrDefault(itemId, new LinkedList<>());
    }
    
    /**
     * Check for overdue items and generate notifications
     */
    public List<OverdueNotification> checkOverdueItems() {
        List<OverdueNotification> overdueItems = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (BorrowTransaction trans : activeBorrowings.values()) {
            if (trans.getDueDate().isBefore(today)) {
                long daysOverdue = ChronoUnit.DAYS.between(trans.getDueDate(), today);
                double fine = daysOverdue * OVERDUE_FINE_PER_DAY;
                
                OverdueNotification notification = new OverdueNotification(
                    trans.getTransactionId(),
                    trans.getUserId(),
                    trans.getItemId(),
                    trans.getDueDate(),
                    daysOverdue,
                    fine
                );
                
                overdueItems.add(notification);
            }
        }
        
        return overdueItems;
    }
    
    /**
     * Generate report of most borrowed items
     */
    public Map<String, Integer> getMostBorrowedItemsReport(int limit) {
        Map<String, Integer> borrowCount = new HashMap<>();
        
        // Count borrowings from history
        for (BorrowTransaction trans : borrowingHistory) {
            borrowCount.merge(trans.getItemId(), 1, Integer::sum);
        }
        
        // Sort by count
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(borrowCount.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // Return top 'limit' items
        Map<String, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            result.put(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
    /**
     * Get users with overdue items
     */
    public Set<String> getUsersWithOverdueItems() {
        Set<String> overdueUsers = new HashSet<>();
        LocalDate today = LocalDate.now();
        
        for (BorrowTransaction trans : activeBorrowings.values()) {
            if (trans.getDueDate().isBefore(today)) {
                overdueUsers.add(trans.getUserId());
            }
        }
        
        return overdueUsers;
    }
    
    /**
     * Helper method to find user by ID
     */
    private UserAccount findUserById(String userId) {
        // This would typically come from list of users that are saved in the librarydatabase
        // For now, return null
        return null;
    }
    
    /**
     * Helper method to find item by ID
     */
    private LibraryItem findItemById(String itemId) {
        // This would typically come from list of items that are saved in the librarydatabase
        // For now, return null
        return null;
    }
    
    /**
     * Save borrowing data to file
     */
    private void saveBorrowingData() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("activeBorrowings", new ArrayList<>(activeBorrowings.values()));
            data.put("borrowingHistory", borrowingHistory);
            FileHandler.saveData("borrowing_data.json", data);
        } catch (Exception e) {
            System.err.println("Error saving borrowing data: " + e.getMessage());
        }
    }
    
    /**
     * Save reservation data to file
     */
    private void saveReservationData() {
        try {
            FileHandler.saveData("reservation_data.json", reservationQueues);
        } catch (Exception e) {
            System.err.println("Error saving reservation data: " + e.getMessage());
        }
    }
    
    /**
     * Load borrowing data from file
     */
    private void loadBorrowingData() {
        try {
            Map<String, Object> data = FileHandler.loadData("borrowing_data.json");
            if (data != null) {
                // Parse and load data
                // Implementation depends on your serialization method
            }
        } catch (Exception e) {
            System.err.println("No existing borrowing data found");
        }
    }
}

/**
 * Supporting Classes (can be in separate files)
 */

class BorrowTransaction {
    private String transactionId;
    private String userId;
    private String itemId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    
    // Constructor, getters, setters
    public BorrowTransaction(String transactionId, String userId, String itemId, 
                            LocalDate borrowDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.itemId = itemId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.fine = 0.0;
    }
    
    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
}

class BorrowResult {
    private boolean success;
    private String message;
    private BorrowTransaction transaction;
    
    public BorrowResult(boolean success, String message, BorrowTransaction transaction) {
        this.success = success;
        this.message = message;
        this.transaction = transaction;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public BorrowTransaction getTransaction() { return transaction; }
}

class Reservation {
    private String reservationId;
    private String userId;
    private String itemId;
    private LocalDate reservationDate;
    
    public Reservation(String reservationId, String userId, String itemId, LocalDate reservationDate) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.itemId = itemId;
        this.reservationDate = reservationDate;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
}

class BorrowOperation {
    public enum Type { BORROW, RETURN }
    
    private Type type;
    private BorrowTransaction transaction;
    
    public BorrowOperation(Type type, BorrowTransaction transaction) {
        this.type = type;
        this.transaction = transaction;
    }
    
    public Type getType() { return type; }
    public BorrowTransaction getTransaction() { return transaction; }
}

class OverdueNotification {
    private String transactionId;
    private String userId;
    private String itemId;
    private LocalDate dueDate;
    private long daysOverdue;
    private double fine;
    
    public OverdueNotification(String transactionId, String userId, String itemId, 
                              LocalDate dueDate, long daysOverdue, double fine) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.itemId = itemId;
        this.dueDate = dueDate;
        this.daysOverdue = daysOverdue;
        this.fine = fine;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public long getDaysOverdue() { return daysOverdue; }
    public double getFine() { return fine; }
}
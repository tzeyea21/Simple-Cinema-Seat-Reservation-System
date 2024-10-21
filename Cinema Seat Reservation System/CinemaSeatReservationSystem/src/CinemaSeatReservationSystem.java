import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CinemaSeatReservationSystem {

    // Class representing a Theatre with seat reservation functionality
    static class Theatre {
        private final int seats = 20; // Total number of seats in the theatre
        private final boolean[] seatAvailability = new boolean[seats]; // Array to track seat availability
        private final Lock lock = new ReentrantLock(); // Lock to ensure thread safety

        // Method to select seats for a customer
        public boolean selectSeats(int customerId, int[] seatsToReserve, int theatreNumber) {
            lock.lock(); // Acquire the lock
            try {
                // Check if the seats are available
                for (int seat : seatsToReserve) {
                    if (seat <= 0 || seat > this.seats || seatAvailability[seat - 1]) {
                        return false; // Seat already taken or out of bounds
                    }
                }

                // Select the seats that are to be reserved
                for (int seat : seatsToReserve) {
                    seatAvailability[seat - 1] = true;
                }
                System.out.println("Customer " + customerId + " selected seat(s) " + toString(seatsToReserve) + " in Theatre " + theatreNumber);
                return true; // Seats selected successfully
            } finally {
                lock.unlock(); // Release the lock
            }
        }

        // Method to display seat availability in the theatre
        public void showSeatAvailability(int theatreNumber) {
            lock.lock(); // Acquire the lock
            try {
                System.out.println(); // Print an empty line
                System.out.print("Theatre " + theatreNumber + " Seat Availability:\nSeats: ");
                // Print seat availability status
                for (boolean seat : seatAvailability) {
                    System.out.print(seat ? "[X]" : "[ ]"); // [X] for reserved, [ ] for available
                }
                System.out.println();
            } finally {
                lock.unlock(); // Release the lock
            }
        }

        // Helper method to convert an array of seat numbers to a string
        private String toString(int[] seats) {
            StringBuilder sb = new StringBuilder();
            for (int seat : seats) {
                sb.append(seat).append(" ");
            }
            return sb.toString().trim(); // Return the string representation of seat numbers
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Create an array of theatres
        Theatre[] theatres = {new Theatre(), new Theatre(), new Theatre()};
        List<Thread> customerThreads = new ArrayList<>(); // List to hold customer threads
    
        Random random = new Random(); // Random number generator
    
        // Create and start 100 customer threads
        for (int i = 0; i < 100; i++) {
            final int customerId = i; // Unique ID for each customer
            Thread customerThread = new Thread(() -> {
                int theatreIndex = random.nextInt(3); // Select a random theatre
                int seatsToReserve = random.nextInt(3) + 1; // Select a random number of seats to reserve
                Set<Integer> uniqueSeats = new HashSet<>();
                // Generate unique seat numbers to reserve
                while (uniqueSeats.size() < seatsToReserve) {
                    uniqueSeats.add(random.nextInt(20) + 1);
                }
                int[] seats = uniqueSeats.stream().mapToInt(Integer::intValue).toArray();
                try {
                    Thread.sleep(random.nextInt(501) + 500); // Simulate delay
                    // Attempt to reserve seats and print result
                    boolean success = theatres[theatreIndex].selectSeats(customerId, seats, theatreIndex + 1);
                    if (success) {
                        System.out.println("Customer " + customerId + " successfully reserved seat(s) " + toString(seats) + " in Theatre " + (theatreIndex + 1));
                    } else {
                        System.out.println("Customer " + customerId + " failed to reserve seat(s) " + toString(seats) + " in Theatre " + (theatreIndex + 1));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                }
            });
            customerThreads.add(customerThread); // Add thread to the list
            customerThread.start(); // Start the thread
        }
    
        // Wait for all customer threads to finish
        for (Thread thread : customerThreads) {
            thread.join();
        }
    
        // Display seat availability for each theatre
        for (int i = 0; i < theatres.length; i++) {
            theatres[i].showSeatAvailability(i + 1);
        }
    }
    
    // Helper method to convert an array of seat numbers to a string
    private static String toString(int[] seats) {
        StringBuilder sb = new StringBuilder();
        for (int seat : seats) {
            sb.append(seat).append(" ");
        }
        return sb.toString().trim(); // Return the string representation of seat numbers
    }
}

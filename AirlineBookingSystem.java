
import java.util.*;

public class AirlineBookingSystem {

    // ===============================
    // FLIGHT CLASS
    // ===============================
    static class Flight {
        String flightNumber;
        String source;
        String destination;

        String[][] seats = {
            {"1A", "1B"},
            {"2A", "2B"}
        };

        boolean[][] booked = new boolean[2][2];

        PriorityQueue<Passenger> waitingList =
            new PriorityQueue<>(
                Comparator.comparingInt(
                    (Passenger p) -> p.type.equalsIgnoreCase("Gold") ? 0 : 1
                ).thenComparingLong(p -> p.sequence)
            );

        Flight(String flightNumber, String source, String destination) {
            this.flightNumber = flightNumber;
            this.source = source;
            this.destination = destination;
        }

        String getAvailableSeat() {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    if (!booked[i][j]) {
                        booked[i][j] = true;
                        return seats[i][j];
                    }
                }
            }
            return null;
        }

        void releaseSeat(String seat) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    if (seats[i][j].equals(seat)) {
                        booked[i][j] = false;
                        return;
                    }
                }
            }
        }

        int seatsLeft() {
            int count = 0;

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    if (!booked[i][j]) {
                        count++;
                    }
                }
            }
            return count;
        }

        void displaySeats() {
            System.out.println("\n--- SEAT MAP ---");

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    System.out.print(
                        seats[i][j] + " : " +
                        (booked[i][j] ? "Booked" : "Available") + "   "
                    );
                }
                System.out.println();
            }

            System.out.println("Seats left: " + seatsLeft());
        }
    }

    // ===============================
    // PASSENGER CLASS
    // ===============================
    static class Passenger {
        String name;
        String type;
        long sequence;

        Passenger(String name, String type, long sequence) {
            this.name = name;
            this.type = type;
            this.sequence = sequence;
        }
    }

    // ===============================
    // BOOKING CLASS
    // ===============================
    static class Booking {
        String pnr;
        String passengerName;
        String flightNumber;
        String seat;
        String passengerType;
        boolean active;

        Booking(String pnr, String passengerName,
                String flightNumber, String seat,
                String passengerType) {

            this.pnr = pnr;
            this.passengerName = passengerName;
            this.flightNumber = flightNumber;
            this.seat = seat;
            this.passengerType = passengerType;
            this.active = true;
        }
    }

    // ===============================
    // DIJKSTRA NODE
    // ===============================
    static class Node {
        String city;
        int distance;

        Node(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }
    }

    // ===============================
    // GLOBAL DATA STRUCTURES
    // ===============================
    static Scanner sc = new Scanner(System.in);

    static HashMap<String, Flight> flights = new HashMap<>();

    static HashMap<String, Booking> bookings = new HashMap<>();

    static HashMap<String, HashMap<String, Integer>> graph =
        new HashMap<>();

    static int pnrCounter = 101;
    static long passengerSequence = 0;

    // ===============================
    // INITIALIZE FLIGHTS
    // ===============================
    static void initializeFlights() {

        flights.put(
            "AI101",
            new Flight("AI101", "Raipur", "Delhi")
        );

        System.out.println("Flights initialized successfully.");
    }

    // ===============================
    // GENERATE PNR
    // ===============================
    static String generatePNR() {
        return "P" + pnrCounter++;
    }

    // ===============================
    // BOOK TICKET
    // ===============================
    static void bookTicket() {

        System.out.print("Enter Flight Number: ");
        String flightNo = sc.nextLine().trim().toUpperCase();

        Flight flight = flights.get(flightNo);

        if (flight == null) {
            System.out.println("Flight " + flightNo + " not found.");
            return;
        }

        System.out.print("Enter Passenger Name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Passenger name cannot be empty.");
            return;
        }

        System.out.print("Passenger Type (Gold/Regular): ");
        String type = sc.nextLine().trim();

        if (!type.equalsIgnoreCase("Gold") &&
            !type.equalsIgnoreCase("Regular")) {

            System.out.println("Invalid passenger type.");
            return;
        }

        type = type.equalsIgnoreCase("Gold") ? "Gold" : "Regular";

        String seat = flight.getAvailableSeat();

        if (seat == null) {

            Passenger passenger =
                new Passenger(name, type, passengerSequence++);

            flight.waitingList.add(passenger);

            System.out.println("\nFlight is full!");

            System.out.println(
                name + " added to waiting list."
            );

            System.out.println(
                "Position: " + getWaitingPosition(flight, passenger)
            );

            return;
        }

        confirmBooking(flight, name, type, seat);
    }

    // ===============================
    // CONFIRM BOOKING
    // ===============================
    static void confirmBooking(
        Flight flight,
        String name,
        String type,
        String seat
    ) {

        String pnr = generatePNR();

        Booking booking = new Booking(
            pnr,
            name,
            flight.flightNumber,
            seat,
            type
        );

        bookings.put(pnr, booking);

        System.out.println("\n==============================");
        System.out.println("BOOKING CONFIRMED!");
        System.out.println("==============================");

        System.out.println("PNR: " + pnr);
        System.out.println("Passenger: " + name);
        System.out.println("Flight: " + flight.flightNumber);
        System.out.println("Seat: " + seat);
        System.out.println("Category: " + type);

        System.out.println("Seats left: " + flight.seatsLeft());
    }

    // ===============================
    // WAITING LIST POSITION
    // ===============================
    static int getWaitingPosition(
        Flight flight, Passenger passenger
    ) {

        ArrayList<Passenger> list =
            new ArrayList<>(flight.waitingList);

        list.sort(
            Comparator.comparingInt(
                (Passenger p) ->
                    p.type.equalsIgnoreCase("Gold") ? 0 : 1
            ).thenComparingLong(p -> p.sequence)
        );

        return list.indexOf(passenger) + 1;
    }

    // ===============================
    // CANCEL TICKET
    // ===============================
    static void cancelTicket() {

        System.out.print("Enter PNR Number: ");
        String pnr = sc.nextLine().trim().toUpperCase();

        Booking booking = bookings.get(pnr);

        if (booking == null || !booking.active) {
            System.out.println("Invalid or already cancelled PNR.");
            return;
        }

        booking.active = false;

        Flight flight = flights.get(booking.flightNumber);

        flight.releaseSeat(booking.seat);

        System.out.println("\nBooking " + pnr + " cancelled successfully.");

        System.out.println("Released Seat: " + booking.seat);

        // Give released seat to highest-priority passenger
        if (!flight.waitingList.isEmpty()) {

            Passenger nextPassenger = flight.waitingList.poll();

            String seat = flight.getAvailableSeat();

            System.out.println(
                "\nWaiting list passenger confirmed!"
            );

            confirmBooking(
                flight,
                nextPassenger.name,
                nextPassenger.type,
                seat
            );
        }
    }

    // ===============================
    // VIEW FLIGHT DETAILS
    // ===============================
    static void viewFlights() {

        System.out.println("\n--- AVAILABLE FLIGHTS ---");

        for (Flight flight : flights.values()) {

            System.out.println(
                "Flight: " + flight.flightNumber
            );

            System.out.println(
                "Source: " + flight.source
            );

            System.out.println(
                "Destination: " + flight.destination
            );

            flight.displaySeats();

            System.out.println(
                "Waiting list size: " + flight.waitingList.size()
            );

            System.out.println("--------------------------");
        }
    }

    // ===============================
    // VIEW BOOKINGS
    // ===============================
    static void viewBookings() {

        System.out.println("\n--- BOOKING DETAILS ---");

        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking b : bookings.values()) {

            System.out.println("PNR: " + b.pnr);
            System.out.println("Passenger: " + b.passengerName);
            System.out.println("Flight: " + b.flightNumber);
            System.out.println("Seat: " + b.seat);
            System.out.println(
                "Status: " + (b.active ? "Confirmed" : "Cancelled")
            );

            System.out.println("-----------------------");
        }
    }

    // ===============================
    // CREATE ROUTE GRAPH
    // ===============================
    static void addRoute(String from, String to, int fare) {

        graph.putIfAbsent(from, new HashMap<>());

        graph.get(from).put(to, fare);
    }

    static void initializeRoutes() {

        graph.clear();

        addRoute("Raipur", "Nagpur", 2000);
        addRoute("Nagpur", "Mumbai", 3000);
        addRoute("Raipur", "Mumbai", 6000);
        addRoute("Raipur", "Delhi", 5000);
        addRoute("Delhi", "Mumbai", 4500);
    }

    // ===============================
    // DIJKSTRA ALGORITHM
    // ===============================
    static void findCheapestRoute() {

        System.out.print("Enter Source City: ");
        String source = sc.nextLine().trim();

        System.out.print("Enter Destination City: ");
        String destination = sc.nextLine().trim();

        HashMap<String, Integer> distance = new HashMap<>();

        HashMap<String, String> parent = new HashMap<>();

        PriorityQueue<Node> pq =
            new PriorityQueue<>(
                Comparator.comparingInt(n -> n.distance)
            );

        distance.put(source, 0);

        pq.add(new Node(source, 0));

        while (!pq.isEmpty()) {

            Node current = pq.poll();

            String city = current.city;

            int dist = current.distance;

            if (dist > distance.getOrDefault(city, Integer.MAX_VALUE)) {
                continue;
            }

            if (city.equalsIgnoreCase(destination)) {
                break;
            }

            HashMap<String, Integer> neighbours = graph.get(city);

            if (neighbours == null) {
                continue;
            }

            for (String nextCity : neighbours.keySet()) {

                int fare = neighbours.get(nextCity);

                int newDistance = dist + fare;

                if (newDistance <
                    distance.getOrDefault(nextCity, Integer.MAX_VALUE)) {

                    distance.put(nextCity, newDistance);

                    parent.put(nextCity, city);

                    pq.add(new Node(nextCity, newDistance));
                }
            }
        }

        if (!distance.containsKey(destination)) {

            System.out.println("No route found between these cities.");
            return;
        }

        ArrayList<String> path = new ArrayList<>();

        String current = destination;

        while (current != null) {

            path.add(current);

            current = parent.get(current);
        }

        Collections.reverse(path);

        System.out.println("\n--- CHEAPEST ROUTE ---");

        System.out.println(
            "Route: " + String.join(" -> ", path)
        );

        System.out.println(
            "Total Fare: Rs. " + distance.get(destination)
        );
    }

    // ===============================
    // LOAD TEST CASE 4
    // ===============================
    static void loadTestCase4() {

        bookings.clear();
        flights.clear();

        pnrCounter = 101;
        passengerSequence = 0;

        initializeFlights();

        Flight flight = flights.get("AI101");

        String[] names = {"Riya", "Aman", "Karan", "Neha"};
        String[] seats = {"1A", "1B", "2A", "2B"};

        for (int i = 0; i < 4; i++) {

            String pnr = generatePNR();

            Booking b = new Booking(
                pnr,
                names[i],
                "AI101",
                seats[i],
                "Regular"
            );

            bookings.put(pnr, b);

            flight.booked[i / 2][i % 2] = true;
        }

        System.out.println("\nTest Case 4 data loaded.");

        System.out.println("PNR P101 - Riya - Seat 1A");
        System.out.println("PNR P102 - Aman - Seat 1B");
        System.out.println("PNR P103 - Karan - Seat 2A");
        System.out.println("PNR P104 - Neha - Seat 2B");

        System.out.println("Flight is now full.");
    }

    // ===============================
    // RESET DATA
    // ===============================
    static void resetSystem() {

        bookings.clear();
        flights.clear();

        pnrCounter = 101;
        passengerSequence = 0;

        initializeFlights();

        System.out.println("System reset successfully.");
    }

    // ===============================
    // MAIN METHOD
    // ===============================
    public static void main(String[] args) {

        initializeFlights();
        initializeRoutes();

        int choice =-1;

        do {

            System.out.println("\n================================");
            System.out.println("      AIRLINE BOOKING SYSTEM");
            System.out.println("================================");

            System.out.println("1. Book Flight");
            System.out.println("2. Cancel Booking");
            System.out.println("3. Find Cheapest Route");
            System.out.println("4. View Flight Details");
            System.out.println("5. View Booking Details");
            System.out.println("6. Load Test Case 4 Data");
            System.out.println("7. Reset System");
            System.out.println("8. Exit");

            System.out.print("\nEnter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {

                case 1:
                    bookTicket();
                    break;

                case 2:
                    cancelTicket();
                    break;

                case 3:
                    findCheapestRoute();
                    break;

                case 4:
                    viewFlights();
                    break;

                case 5:
                    viewBookings();
                    break;

                case 6:
                    loadTestCase4();
                    break;

                case 7:
                    resetSystem();
                    initializeRoutes();
                    break;

                case 8:
                    System.out.println("Thank you for using Airline Booking System!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 8);

        sc.close();
    }
}

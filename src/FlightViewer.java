
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Displays flight arrivals for a selected airport terminal.
 * Retrieves flight data securely using JDBC and PreparedStatement.
 */
public class FlightViewer {

    /**
     * Connects to FlightDB and displays arrivals for the selected terminal.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/FlightDB";
        String user = "root";

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            System.out.print("Enter terminal number (2, 3, 4, or 5): ");
            String terminal = scanner.nextLine().trim();

            String sql = """
                    SELECT a.id, a.flight_number, f.from_city,
                           a.scheduled, a.delay_status, a.terminal
                    FROM Arrivals AS a
                    INNER JOIN FlightRoutes AS f
                        ON a.flight_number = f.flight_number
                    WHERE a.terminal = ?
                    ORDER BY a.scheduled ASC
                    """;

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // Bind the terminal number as a String
                pstmt.setString(1, terminal);

                try (ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("\n--- Flight Arrivals - Terminal " + terminal + " ---");

                    System.out.printf("%-5s %-12s %-18s %-12s %-22s %-10s%n",
                            "ID", "Flight", "From", "Scheduled", "Status", "Terminal");

                    System.out.println("-".repeat(85));

                    int count = 0;

                    while (rs.next()) {

                        System.out.printf("%-5d %-12s %-18s %-12s %-22s %-10s%n",
                                rs.getInt("id"),
                                rs.getString("flight_number"),
                                rs.getString("from_city"),
                                rs.getString("scheduled"),
                                rs.getString("delay_status"),
                                rs.getString("terminal"));

                        count++;
                    }

                    if (count == 0) {
                        System.out.println("No flights found for terminal " + terminal);
                    } else {
                        System.out.println("-".repeat(85));
                        System.out.println("Total flights found: " + count);
                    }
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
}

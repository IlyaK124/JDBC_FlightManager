
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Updates the delay status of flight LX354 securely.
 *
 * The previous implementation used a regular Statement
 * and concatenated user input directly into the SQL query.
 * This allowed SQL Injection attacks, where malicious input
 * could change the structure of the SQL command.
 *
 * This implementation uses PreparedStatement with
 * parameterized queries. User input is treated as data
 * rather than executable SQL, preventing SQL Injection.
 */
public class SimpleFlightUpdater {

    /**
     * Connects to FlightDB and updates the flight status
     * using a secure PreparedStatement.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
        String user = "root";

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            System.out.print("Enter new delay status for flight LX354: ");
            String userInput = scanner.nextLine();

            String sql = "UPDATE Arrivals SET delay_status = ? WHERE flight_number = ?";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, userInput);
                pstmt.setString(2, "LX354");

                int affectedRows = pstmt.executeUpdate();

                System.out.println("Rows updated: " + affectedRows);

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
}

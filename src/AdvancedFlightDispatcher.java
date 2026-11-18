
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdvancedFlightDispatcher {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
        String user = "root";

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            try (Connection conn = DriverManager.getConnection(url, user, password)) {

                List<Integer> arrivalIds = Arrays.asList(1, 3, 5);
                String newStatus = "Landed - Final";

                updateMultipleDelays(conn, arrivalIds, newStatus);

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    public static void updateMultipleDelays(Connection conn,
                                            List<Integer> arrivalIds,
                                            String newStatus) throws SQLException {

        String sql = "UPDATE Arrivals SET delay_status = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the new status once before the loop
            pstmt.setString(1, newStatus);

            // Add each arrival ID to the batch
            for (Integer id : arrivalIds) {
                pstmt.setInt(2, id);
                pstmt.addBatch();
            }

            // Execute all updates together
            int[] results = pstmt.executeBatch();

            int totalAffectedRows = 0;

            for (int result : results) {
                if (result >= 0) {
                    totalAffectedRows += result;
                }
            }

            System.out.println("Total rows updated: " + totalAffectedRows);
        }
    }
}

import java.sql.*;

public class TicketDAO {

    public boolean bookTicket(int flightId, String passengerName, String passengerEmail) {
        FlightDAO flightDAO = new FlightDAO();
        if (!flightDAO.updateAvailableSeats(flightId)) {
            System.out.println("Извините, на этот рейс нет свободных мест.");
            return false;
        }

        String sql = "INSERT INTO tickets (flight_id, passenger_name, passenger_email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            pstmt.setString(2, passengerName);
            pstmt.setString(3, passengerEmail);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Ошибка при бронировании: " + e.getMessage());
            return false;
        }
    }
}
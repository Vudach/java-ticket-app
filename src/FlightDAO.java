import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights WHERE available_seats > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Flight flight = new Flight();
                flight.setId(rs.getInt("id"));
                flight.setFlightNumber(rs.getString("flight_number"));
                flight.setDepartureCity(rs.getString("departure_city"));
                flight.setDestinationCity(rs.getString("destination_city"));
                flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                flight.setPrice(rs.getDouble("price"));
                flight.setAvailableSeats(rs.getInt("available_seats"));
                flights.add(flight);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении рейсов: " + e.getMessage());
        }
        return flights;
    }

    public boolean updateAvailableSeats(int flightId) {
        String sql = "UPDATE flights SET available_seats = available_seats - 1 WHERE id = ? AND available_seats > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении мест: " + e.getMessage());
            return false;
        }
    }
}
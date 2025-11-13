import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class TicketBookingApp extends JFrame {
    private JComboBox<String> flightsComboBox;
    private JTextField nameField;
    private JTextField emailField;
    private JTextArea resultArea;
    private List<Flight> flightsList;
    
    public TicketBookingApp() {
        // Настройка главного окна
        setTitle("🎫 Система бронирования авиабилетов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null); // Центрируем окно
        
        // Создаем компоненты
        createComponents();
        
        // Загружаем рейсы при запуске
        loadFlights();
    }
    
    private void createComponents() {
        // Главная панель
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Заголовок
        JLabel titleLabel = new JLabel("Бронирование авиабилетов", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 200));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Центральная панель с формой
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));
        
        // Поля формы
        formPanel.add(new JLabel("Выберите рейс:"));
        flightsComboBox = new JComboBox<>();
        formPanel.add(flightsComboBox);
        
        formPanel.add(new JLabel("Ваше ФИО:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
        
        // Кнопки
        JButton bookButton = new JButton("🛒 Забронировать билет");
        bookButton.setBackground(new Color(50, 150, 50));
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton refreshButton = new JButton("🔄 Обновить список рейсов");
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        
        formPanel.add(bookButton);
        formPanel.add(refreshButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Область для результатов
        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(240, 240, 240));
        resultArea.setBorder(BorderFactory.createTitledBorder("Результат"));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        // Обработчики событий для кнопок
        bookButton.addActionListener(e -> bookTicket());
        refreshButton.addActionListener(e -> loadFlights());
        
        // Красивое оформление при наведении на кнопки
        bookButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bookButton.setBackground(new Color(40, 130, 40));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bookButton.setBackground(new Color(50, 150, 50));
            }
        });
        
        add(mainPanel);
    }
    
    private void loadFlights() {
        try {
            FlightDAO flightDAO = new FlightDAO();
            flightsList = flightDAO.getAllFlights();
            
            flightsComboBox.removeAllItems();
            
            if (flightsList.isEmpty()) {
                flightsComboBox.addItem("Нет доступных рейсов");
                resultArea.setText("Нет доступных рейсов для бронирования.");
            } else {
                for (Flight flight : flightsList) {
                    String flightInfo = String.format("Рейс %s: %s → %s (%.0f руб., мест: %d)", 
                        flight.getFlightNumber(),
                        flight.getDepartureCity(), 
                        flight.getDestinationCity(),
                        flight.getPrice(),
                        flight.getAvailableSeats());
                    flightsComboBox.addItem(flightInfo);
                }
                resultArea.setText("Загружено " + flightsList.size() + " рейсов. Выберите рейс для бронирования.");
            }
        } catch (Exception e) {
            resultArea.setText("Ошибка загрузки рейсов: " + e.getMessage());
        }
    }
    
   private void bookTicket() {
    if (flightsList == null || flightsList.isEmpty()) {
        resultArea.setText("Сначала загрузите список рейсов!");
        return;
    }

    int selectedIndex = flightsComboBox.getSelectedIndex();
    if (selectedIndex < 0 || selectedIndex >= flightsList.size()) {
        resultArea.setText("Пожалуйста, выберите рейс!");
        return;
    }

    String name = nameField.getText().trim();
    String email = emailField.getText().trim();

    if (name.isEmpty() || email.isEmpty()) {
        resultArea.setText("Пожалуйста, заполните все поля!");
        return;
    }

    try {
        Flight selectedFlight = flightsList.get(selectedIndex);
        TicketDAO ticketDAO = new TicketDAO();
        
        boolean success = ticketDAO.bookTicket(selectedFlight.getId(), name, email);
        
        if (success) {
            String message = "✅ БРОНИРОВАНИЕ УСПЕШНО!\n\n" +
                "Детали бронирования:\n" +
                "• Пассажир: " + name + "\n" +
                "• Email: " + email + "\n" +
                "• Рейс: " + selectedFlight.getFlightNumber() + "\n" +
                "• Маршрут: " + selectedFlight.getDepartureCity() + " → " + selectedFlight.getDestinationCity() + "\n" +
                "• Время вылета: " + selectedFlight.getDepartureTime() + "\n" +
                "• Стоимость: " + selectedFlight.getPrice() + " руб.\n\n" +
                "Билет отправлен на вашу электронную почту!";
            
            resultArea.setText(message);
            
            // Очищаем поля после успешного бронирования
            nameField.setText("");
            emailField.setText("");
            
            // Обновляем список рейсов
            loadFlights();
        } else {
            resultArea.setText("❌ Ошибка бронирования. Возможно, закончились места.");
        }
    } catch (Exception e) {
        resultArea.setText("Ошибка при бронировании: " + e.getMessage());
    }
}
 public static void main(String[] args) {
    // Запускаем приложение
    SwingUtilities.invokeLater(() -> {
        new TicketBookingApp().setVisible(true);
    });
 }}
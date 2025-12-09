package com.example.todo.repository;

import com.example.todo.model.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagesRepository {
    
    private static MessagesRepository instance;
    private List<Message> messages;
    
    public static MessagesRepository getInstance() {
        if (instance == null) {
            instance = new MessagesRepository();
        }
        return instance;
    }
    
    private MessagesRepository() {
        messages = new ArrayList<>();
        initializeMockData();
    }
    
    private void initializeMockData() {
        Calendar cal = Calendar.getInstance();
        
        // Komunikat 1 - Szczepienie
        cal.set(2024, Calendar.NOVEMBER, 5);
        messages.add(new Message(
            "msg1",
            "Przypomnienie o szczepieniu przeciw grypie",
            "Zbliża się sezon grypowy. Zalecane szczepienie dla pacjentów przed transplantacją.",
            "vaccinations",
            cal.getTime(),
            false
        ));
        
        // Komunikat 2 - Termin
        cal.set(2024, Calendar.NOVEMBER, 4);
        messages.add(new Message(
            "msg2",
            "Termin pobierania surowicy – badania immunologiczne",
            "Zapraszamy 12.11.2024 o godz. 8:00 do pobrania próbki surowicy na badania immunologiczne.",
            "appointments",
            cal.getTime(),
            false
        ));
        
        // Komunikat 3 - Badanie
        cal.set(2024, Calendar.NOVEMBER, 3);
        messages.add(new Message(
            "msg3",
            "ECHO serca wymaga aktualizacji",
            "Twoje badanie ECHO serca jest po terminie o 201 dni. Umów wizytę kardiologiczną.",
            "examinations",
            cal.getTime(),
            false
        ));
    }
    
    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }
    
    public List<Message> getMessagesByCategory(String category) {
        if (category == null || category.equals("all")) {
            return getAllMessages();
        }
        
        List<Message> filtered = new ArrayList<>();
        for (Message message : messages) {
            if (message.getCategory().equals(category)) {
                filtered.add(message);
            }
        }
        return filtered;
    }
    
    public int getUnreadCount() {
        int count = 0;
        for (Message message : messages) {
            if (!message.isRead()) {
                count++;
            }
        }
        return count;
    }
    
    public void markAsRead(String messageId) {
        for (Message message : messages) {
            if (message.getId().equals(messageId)) {
                message.setRead(true);
                break;
            }
        }
    }
}


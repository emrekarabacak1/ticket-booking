package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (eventRepository.count() == 0) {

            Category category = categoryRepository.findByName("Müzik").orElseGet(() -> {
                Category newCategory = new Category();
                newCategory.setName("Müzik");
                return categoryRepository.save(newCategory);
            });

            if (userRepository.findByEmail("test@test.com").isEmpty()) {
                User user = new User();
                user.setEmail("test@test.com");
                user.setPassword(passwordEncoder.encode("password"));
                user.setFirstName("Test");
                user.setLastName("User");
                user.setRole(Role.USER);
                userRepository.save(user);
                System.out.println("✅ Test Kullanıcısı Oluşturuldu: test@test.com / password");
            }

            Event event = new Event();
            event.setName("Tarkan Konseri");
            event.setDescription("Harbiye Açıkhava Konseri");
            event.setDate(LocalDateTime.now().plusDays(10));
            event.setLocation("İstanbul");
            event.setPrice(BigDecimal.valueOf(500));
            event.setCategory(category);

            event = eventRepository.save(event);
            System.out.println("✅ Örnek Etkinlik Oluşturuldu: " + event.getName());

            System.out.println("--------------------------------------------------");
            System.out.println("🎟️ KOLTUKLAR VERİTABANINA YAZILIYOR...");

            for (long i = 1; i <= 10; i++) {
                Seat seat = new Seat();
                seat.setEvent(event);
                seat.setRow("A");
                seat.setNumber(i);
                seat.setPrice(BigDecimal.valueOf(500));
                seat.setStatus(SeatStatus.AVAILABLE);

                Seat savedSeat = seatRepository.save(seat);

                System.out.println("✅ Koltuk Eklendi -> ID: " + savedSeat.getId() + " | Sıra: A-" + savedSeat.getNumber());
            }
            System.out.println("--------------------------------------------------");
        }
    }
}
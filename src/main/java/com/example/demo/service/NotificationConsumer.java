package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeMessage(NotificationMessage message) {
        log.info("--------------------------------------------------");
        log.info("📨 RabbitMQ'dan Yeni Mesaj Yakalandı!");
        log.info("👤 Kime: {}", message.getEmail());
        log.info("📃 Konu: {}", message.getSubject());
        log.info("📝 İçerik: {}", message.getContent());

        try {
            log.info("⏳ Mail sunucusuna bağlanılıyor... (Simülasyon)");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("✅ Mail başarıyla gönderildi!");
        log.info("--------------------------------------------------");
    }
}
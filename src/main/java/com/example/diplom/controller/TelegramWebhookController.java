package com.example.diplom.controller;

import com.example.diplom.models.Order;
import com.example.diplom.repository.OrderRepository;
import com.example.diplom.service.OrderCheckService;
import com.example.diplom.service.TelegramNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Tag(
        name = "Telegram Webhook",
        description = "Обработка входящих обновлений от Telegram-бота"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/telegram")
public class TelegramWebhookController {
    private final TelegramNotificationService telegramNotificationService;
    private final OrderCheckService orderCheckService;
    private final OrderRepository orderRepository;

    @Operation(
            summary     = "Получение обновления от Telegram",
            description = "Принимает Webhook от Telegram, обрабатывает команды по заказам (accept, reject, shipped, delivered, complete)"
    )
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveUpdate(@RequestBody Map<String, Object> update) {
        try {
            // Достаем текст сообщения из JSON-ответа Telegram
            Map<String, Object> message = (Map<String, Object>) update.get("message");
            if (message == null) {
                return ResponseEntity.ok("No message field.");
            }

            String text = (String) message.get("text");
            if (text != null && (text.startsWith("/accept_")
                    || text.startsWith("/reject_")
                    || text.startsWith("/shipped_")
                    || text.startsWith("/delivered_")
                    || text.startsWith("/complete_"))) {
                telegramNotificationService.handleOrderResponse(text);
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing Telegram update");
        }
    }
}

package com.example.diplom.controller;

import com.example.diplom.models.Order;
import com.example.diplom.repository.OrderRepository;
import com.example.diplom.service.OrderCheckService;
import com.example.diplom.service.TelegramNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Optional;

@Tag(
        name = "QR Confirmation",
        description = "Подтверждение заказа через QR-код и отправка чека в Telegram"
)
@RestController
@RequiredArgsConstructor
public class QrConfirmController {
    private final OrderRepository orderRepository;
    private final OrderCheckService orderCheckService;
    private final TelegramNotificationService telegramNotificationService;

    @Operation(
            summary     = "Подтвердить заказ по QR",
            description = "Генерирует PDF-чек по заказу и отправляет его клиенту в Telegram"
    )
    @GetMapping("/api/v1/qr-confirm")
    public ResponseEntity<String> confirmQr(@RequestParam Long orderId) throws Exception {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Order not found");
        }

        Order order = orderOpt.get();
        String chatId = order.getClient().getChatId();

        File pdf = orderCheckService.generateOrderReceiptPdf(order);

        telegramNotificationService.sendDocument(
                chatId,
                pdf,
                "🧾 Спасибо за заказ №" + order.getId() + "!\nВаш чек прикреплён ниже 💙"
        );
        pdf.delete();

        return ResponseEntity.ok("Чек отправлен в Telegram");
    }
}

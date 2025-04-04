package com.example.diplom.controller;

import com.example.diplom.models.Order;
import com.example.diplom.repository.OrderRepository;
import com.example.diplom.service.OrderCheckService;
import com.example.diplom.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class QrConfirmController {

    private final OrderRepository orderRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final OrderCheckService orderCheckService;

    @GetMapping("/qr-confirm")
    public ResponseEntity<String> confirmQr(
            @RequestParam Long orderId
    ) throws Exception {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) return ResponseEntity.badRequest().body("Order not found");

        Order order = orderOpt.get();
        String chatId = order.getClient().getChatId(); // Предполагается, что есть chatId у клиента

        File pdf = orderCheckService.generateOrderReceiptPdf(order);

        telegramNotificationService.sendDocument(
                chatId,
                pdf,
                "🧾 Спасибо за заказ №" + order.getId() + "!\nВаш чек прикреплён ниже 💙"
        );
        pdf.delete(); // удалить временный файл

        return ResponseEntity.ok("Чек отправлен в Telegram");
    }
}


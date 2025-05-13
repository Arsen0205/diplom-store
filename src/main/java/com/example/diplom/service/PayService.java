package com.example.diplom.service;


import com.example.diplom.dto.response.OrderItemDtoResponse;
import com.example.diplom.dto.response.ReceiptDtoResponse;
import com.example.diplom.models.Client;
import com.example.diplom.models.Order;
import com.example.diplom.models.enums.OrderStatus;
import com.example.diplom.repository.ClientRepository;
import com.example.diplom.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PayService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderCheckService orderCheckService;
    private final TelegramNotificationService telegramNotificationService;

    @Transactional
    public ResponseEntity<byte[]> payForOrder(Long orderId, Principal principal) {
        Client client = clientRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));

        if (!order.getClient().getId().equals(client.getId())) {
            throw new IllegalArgumentException("Вы не можете оплатить чужой заказ");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Этот заказ уже оплачен");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        List<OrderItemDtoResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemDtoResponse(
                        item.getProduct().getTitle(),
                        item.getQuantity(),
                        item.getSellingPrice(),
                        item.getTotalPrice()
                )).collect(Collectors.toList());

        ReceiptDtoResponse receipt = new ReceiptDtoResponse(
                order.getId(),
                client.getLogin(),
                client.getLoginTelegram(),
                order.getAddress(),
                order.getCity(),
                LocalDateTime.now(),
                order.getTotalCost(),
                order.getTotalPrice(),
                order.getProfit(),
                items
        );

        try {
            byte[] pdfBytes = ReceiptPdfGenerator.generateInvoice(receipt);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=receipt_" + order.getId() + ".pdf");
            headers.add("Content-Type", "application/pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

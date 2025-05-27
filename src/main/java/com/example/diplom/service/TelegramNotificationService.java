package com.example.diplom.service;


import com.example.diplom.models.Order;
import com.example.diplom.models.OrderItem;
import com.example.diplom.models.Product;
import com.example.diplom.models.enums.OrderStatus;
import com.example.diplom.repository.OrderRepository;
import com.example.diplom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderCheckService orderCheckService;

    private final String TELEGRAM_BOT_TOKEN = "7637842579:AAEXPLFZM9p-VliUkSsFzRfmE3qmq5jadrw";
    private final String API_URL = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN;


    //Отправка оповещения в телеграмме при создании заказа поставщику
    public void sendOrderNotification(String chatIdSupplier, String chatIdClient,Order order) {
        String message = "📦 Новый заказ! \n" +
                "ID заказа: " + order.getId() + "\n" +
                "Общая стоимость: " + order.getTotalPrice() + " ₽\n" +
                "Прибыль: " + order.getProfit() + " ₽\n" +
                "Статус: " + order.getStatus() + "\n" +
                "Адрес: г." + order.getCity() + ", " + order.getAddress() + "\n\n" +
                "Товары в заказе:\n";

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem item : order.getOrderItems()) {
                message += item.getProduct().getTitle() + " (Кол-во: " + item.getQuantity() + ", Цена: " + item.getProduct().getSellingPrice() + " ₽)\n";
            }
        } else {
            message += "Товары в заказе отсутствуют.\n";
        }
        message += "Принять: /accept_" + order.getId() + "\n" +
                "Отклонить: /reject_" + order.getId();

        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + chatIdSupplier + "&text=" + message;
        restTemplate.getForObject(url, String.class);

        String messageClient = "Заказ отправлен поставщику на подтверждение";
        String url1 = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + chatIdClient + "&text=" + messageClient;
        restTemplate.getForObject(url1, String.class);

    }

    //Проверяет на какую кнопку нажал поставщик в телеграмме
    public void handleOrderResponse(String command) {
        if (command.startsWith("/accept_")) {
            Long orderId = Long.parseLong(command.replace("/accept_", ""));
            acceptOrder(orderId);
        } else if (command.startsWith("/reject_")) {
            Long orderId = Long.parseLong(command.replace("/reject_", ""));
            rejectOrder(orderId);
        } else if (command.startsWith("/shipped_")) {
            Long orderId = Long.parseLong(command.replace("/shipped_", ""));
            shippedOrder(orderId);
        } else if (command.startsWith("/complete_")) {
            Long orderId = Long.parseLong(command.replace("/complete_", ""));
            completeOrder(orderId);
        } else if (command.startsWith("/delivered_")) {
            Long orderId = Long.parseLong(command.replace("/delivered_", ""));
            deliveredOrder(orderId);
        }
    }

    //Принятие заказа в телеграмм
    public void acceptOrder(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);

        Order order = orderOptional.get();

        //Уменьшение количества товара на складе
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }


        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        String message = "Вы подтвердили заказ! \n\n" +
                "Изменить статус заказа: \n" +
                "В пути: /shipped_" + id + "\n" +
                "Отменить заказ: /reject_" + id;

        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + order.getSupplier().getChatId() + "&text=" + message;
        restTemplate.getForObject(url, String.class);
        String text = "✅ Ваш заказ №" + id + " подтвержден поставщиком!";
        String url1 = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + order.getClient().getChatId() + "&text=" + text;
        restTemplate.getForObject(url1, String.class);
    }

    //Отклонение заказа со стороны клиента
    public void rejectClient(Long id){
        Optional<Order> orderOptional = orderRepository.findById(id);
        Order order = orderOptional.get();

        //Проверка на статус заказа, если заказ еще не подтвердился, то возвращать товары обратно нет необходимости
        if (order.getStatus() != OrderStatus.PENDING) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        String text1 = "❌ Ваш заказ №" + id + " Отменен клиентом!";
        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + order.getSupplier().getChatId() + "&text=" + text1;
        restTemplate.getForObject(url, String.class);
        String text2 = "Вы отменили заказ";
        String url1 = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + order.getClient().getChatId() + "&text=" + text2;
        restTemplate.getForObject(url1, String.class);
    }

    //Отклонение заказа со стороны поставщика
    public void rejectOrder(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        Order order = orderOptional.get();

        //Проверка на статус заказа, если заказ еще не подтвердился, то возвращать товары обратно нет необходимости
        if (order.getStatus() != OrderStatus.PENDING) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + order.getSupplier().getChatId() + "&text=" + "Вы отменили заказ!";
        restTemplate.getForObject(url, String.class);
        String text = "❌ Ваш заказ №" + id + " Отменен поставщиком!";
        String url1 = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + order.getClient().getChatId() + "&text=" + text;
        restTemplate.getForObject(url1, String.class);

    }

    //Изменение статуса на - в пути
    public void shippedOrder(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        Order order = orderOptional.get();

        if (order.getStatus() != OrderStatus.SHIPPED) {
            order.setStatus(OrderStatus.SHIPPED);
            orderRepository.save(order);
        }

        String message = "Статус заказа изменен \n\n" +
                "Изменить статус заказа: \n" +
                "Доставлен: /delivered_" + id + "\n" +
                "Отменить заказ: /reject_" + id;

        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + order.getSupplier().getChatId() + "&text=" + message;
        restTemplate.getForObject(url, String.class);
        String text = "\uD83D\uDE9A Ваш заказ №" + id + " В пути";
        String url1 = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + order.getClient().getChatId() + "&text=" + text;
        restTemplate.getForObject(url1, String.class);
    }


    public void sendPhoto(String chatId, File photo) {
        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendPhoto";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("chat_id", chatId);
        body.add("photo", new FileSystemResource(photo));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        System.out.println("Telegram response: " + response.getBody());
    }

    public void deliveredOrder(Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) return;

        Order order = orderOptional.get();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        // Генерируем PDF чек
        try {
            File pdfFile = orderCheckService.generateOrderReceiptPdf(order);
            sendDocument(order.getSupplier().getChatId(), pdfFile, "✅ Заказ доставлен. Чек для клиента с QR-кодом для оплаты.");
            pdfFile.delete(); // удаляем временный файл
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void completeOrder(Long id) {

    }

    public void paidOrder(){

    }


    private void sendMessage(String chatId, String text) {
        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + chatId + "&text=" + text;
        restTemplate.getForObject(url, String.class);
    }


    public void sendDocument(String chatId, File document, String caption) {
        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendDocument";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("chat_id", chatId);
        body.add("caption", caption);
        body.add("document", new FileSystemResource(document));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
        System.out.println("Ответ от Telegram: " + request.getBody());
    }
}

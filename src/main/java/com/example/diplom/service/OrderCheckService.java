package com.example.diplom.service;


import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.example.diplom.models.Order;
import com.example.diplom.models.OrderItem;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class OrderCheckService {

    private final StripeService stripeService;

    public File generateOrderReceiptPdf(Order order) throws Exception {
        String paymentLink = "https://fruity-bags-punch.loca.lt/gr-confirm?orderId=" + order.getId(); // можно заменить на свой
//        String paymentLink = stripeService.createCheckoutSession(
//                order.getId(),        order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue()
//        );

        File file = File.createTempFile("receipt_order_" + order.getId(), ".pdf");
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        PdfFont font = PdfFontFactory.createFont("C:\\Windows\\Fonts\\arial.ttf");

        doc.add(new Paragraph("Чек на оплату").setBold().setFontSize(14).setFont(font));
        doc.add(new Paragraph("Заказ №: " + order.getId()).setFont(font));
        doc.add(new Paragraph("Клиент: " + order.getClient().getLogin()).setFont(font));
        doc.add(new Paragraph("Сумма: " + order.getTotalPrice() + "₽").setFont(font));
        doc.add(new Paragraph(" "));

        doc.add(new Paragraph("Состав заказа:").setFont(font));
        for (OrderItem item : order.getOrderItems()) {
            doc.add(new Paragraph("- " + item.getProduct().getTitle() + " x" + item.getQuantity()).setFont(font));
        }

        // QR-код
        byte[] qrBytes = generateQrCode(paymentLink);
        Image qrImage = new Image(ImageDataFactory.create(qrBytes));
        doc.add(new Paragraph("Оплатите по QR-коду:").setFont(font));
        doc.add(qrImage);

        doc.close();
        return file;
    }

    public File generateOrderReceiptPdfForClient(Order order) throws Exception{
        File file = File.createTempFile("receipt_order_" + order.getId(), ".pdf");
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        PdfFont font = PdfFontFactory.createFont("C:\\Windows\\Fonts\\arial.ttf");

        doc.add(new Paragraph("Чек на оплату").setBold().setFontSize(14).setFont(font));
        doc.add(new Paragraph("Заказ №: " + order.getId()).setFont(font));
        doc.add(new Paragraph("Клиент: " + order.getClient().getLogin()).setFont(font));
        doc.add(new Paragraph("Сумма: " + order.getTotalPrice() + "₽").setFont(font));
        doc.add(new Paragraph(" "));

        doc.add(new Paragraph("Состав заказа:").setFont(font));
        for (OrderItem item : order.getOrderItems()) {
            doc.add(new Paragraph("- " + item.getProduct().getTitle() + " x" + item.getQuantity()).setFont(font));
        }

        doc.close();
        return file;
    }

    public byte[] generateQrCode(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix matrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

}

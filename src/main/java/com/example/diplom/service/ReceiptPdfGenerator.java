package com.example.diplom.service;


import com.example.diplom.dto.response.ReceiptDtoResponse;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ReceiptPdfGenerator {

    public static byte[] generateInvoice(ReceiptDtoResponse response) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = PdfFontFactory.createFont("C:\\Windows\\Fonts\\arial.ttf");

            document.add(new Paragraph("Чек оплаты").setFont(font));
            document.add(new Paragraph("Номер заказа: " + response.getOrderId()).setFont(font));
            document.add(new Paragraph("Сумма: " + response.getTotalCost() + " руб.").setFont(font));
            document.add(new Paragraph("Дата: " + response.getPaymentDate()).setFont(font));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации PDF", e);
        }
    }
}

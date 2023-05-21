package view;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import view.component.CustomTable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class SaveAsPdf {

    public SaveAsPdf(CustomTable[] tables, String[] tableTitles, File file) {
        try (PDDocument document = new PDDocument()) {
            for (int i = 0; i < tables.length; i++) {
                CustomTable table = tables[i];
                String tableTitle = tableTitles[i];
                PDType0Font font = PDType0Font.load(document, getClass().getResourceAsStream("/font/Montserrat-Regular.ttf"));

                // Create a new page for each table
                PDPage page = new PDPage();
                document.addPage(page);

                // Start writing the content to the page
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Set the position to start writing
                float startX = 50;
                float startY = page.getMediaBox().getHeight() - 50;

                // Set font properties
                contentStream.setFont(font, 12);

                // Write the table title
                contentStream.beginText();
                contentStream.newLineAtOffset(startX, startY);
                contentStream.showText(tableTitle);
                contentStream.endText();

                // Set the position to start drawing the table
                startY -= 20;

                // Set the row height and column width
                float rowHeight = 20;
                float tableWidth = page.getMediaBox().getWidth() - 2 * startX;
                float tableHeight = rowHeight * table.getRowCount();

                // Set the intercell spacing
                float cellSpacing = 2;

                // Draw the table headers
                contentStream.setLineWidth(1f);
                contentStream.setFont(font, 10);
                float nextY = startY;
                for (int col = 0; col < table.getColumnCount(); col++) {
                    float nextX = startX + col * tableWidth / table.getColumnCount();
                    String headerValue = table.getValueAt(0, col).toString();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(nextX, nextY);
                    contentStream.showText(headerValue);
                    contentStream.endText();
                }

                // Draw the table rows
                contentStream.setFont(font, 10);
                for (int row = 1; row < table.getRowCount(); row++) {
                    nextY -= rowHeight;
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        float nextX = startX + col * tableWidth / table.getColumnCount();
                        Object cellValue = table.getValueAt(row, col);
                        if (cellValue != null) {
                            String cellText = String.valueOf(cellValue);

                            // Draw border for each cell
                            contentStream.setLineWidth(1f);
                            float boxX = nextX + cellSpacing / 2;
                            float boxY = nextY + cellSpacing / 2;
                            float boxWidth = tableWidth / table.getColumnCount() - cellSpacing;
                            float boxHeight = rowHeight - cellSpacing;

                            // Draw left border
                            contentStream.moveTo(boxX, boxY);
                            contentStream.lineTo(boxX, boxY + boxHeight);
                            contentStream.stroke();

                            // Draw right border
                            contentStream.moveTo(boxX + boxWidth, boxY);
                            contentStream.lineTo(boxX + boxWidth, boxY + boxHeight);
                            contentStream.stroke();

                            // Draw top border
                            contentStream.moveTo(boxX, boxY + boxHeight);
                            contentStream.lineTo(boxX + boxWidth, boxY + boxHeight);
                            contentStream.stroke();

                            // Draw bottom border
                            contentStream.moveTo(boxX, boxY);
                            contentStream.lineTo(boxX + boxWidth, boxY);
                            contentStream.stroke();

                            // Write cell text
                            contentStream.beginText();
                            contentStream.newLineAtOffset(boxX, boxY + boxHeight - 2);  // Adjust text position
                            contentStream.showText(cellText);
                            contentStream.endText();
                        }
                    }
                }

                // Close the content stream
                contentStream.close();
            }

            // Save the PDF file
            document.save(file);
            JOptionPane.showMessageDialog(null, "Results saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error occurred while saving the results.", "Error", JOptionPane.ERROR_MESSAGE);
        }



    }
}

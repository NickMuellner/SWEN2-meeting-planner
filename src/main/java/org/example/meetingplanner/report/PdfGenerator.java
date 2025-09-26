package org.example.meetingplanner.report;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("hh:mm dd.MM.yyyy");

    public static void generateMeetingReport(File file, List<Meeting> meetings) throws IOException {
        PdfWriter writer = new PdfWriter(file.getAbsolutePath());
        PdfDocument pdf = new PdfDocument(writer);

        try (Document document = new Document(pdf)) {
            boolean first = true;
            for (Meeting meeting : meetings) {
                if (!first) {
                    document.add(new AreaBreak());
                }
                first = false;

                Paragraph title = new Paragraph(meeting.getTitle())
                        .setFontSize(18)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10);
                document.add(title);

                Paragraph sep = new Paragraph()
                        .setBorder(new SolidBorder(1))
                        .setMarginBottom(8)
                        .setMarginTop(0);
                document.add(sep);

                String from = meeting.getFrom() != null ? DATE_FORMAT.format(meeting.getFrom()) : "-";
                String to = meeting.getTo() != null ? DATE_FORMAT.format(meeting.getTo()) : "-";
                Paragraph dates = new Paragraph("Datum: " + from + " — " + to)
                        .setFontSize(11)
                        .setMarginBottom(8);
                document.add(dates);

                Paragraph agendaHeader = new Paragraph("Agenda")
                        .setFontSize(13)
                        .setMarginTop(6)
                        .setMarginBottom(4);
                document.add(agendaHeader);

                Paragraph agenda = new Paragraph(meeting.getAgenda() == null ? "" : meeting.getAgenda())
                        .setFontSize(11)
                        .setMarginBottom(10);
                document.add(agenda);

                Paragraph notesHeader = new Paragraph("Notizen")
                        .setFontSize(13)
                        .setMarginTop(8)
                        .setMarginBottom(4);
                document.add(notesHeader);

                List<MeetingNote> notes = meeting.getNotes();
                if (notes == null || notes.isEmpty()) {
                    document.add(new Paragraph("Keine Notizen vorhanden.").setFontSize(11));
                } else {
                    com.itextpdf.layout.element.List itextList = new com.itextpdf.layout.element.List()
                            .setSymbolIndent(12)
                            .setListSymbol("")
                            .setFontSize(11);
                    int i = 1;
                    for (MeetingNote note : notes) {
                        String text = note == null ? "" : note.getNote();
                        itextList.add(new ListItem(i + ". " + text));
                        i++;
                    }
                    document.add(itextList);
                }
            }
        }
    }
}

package control;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class StampaFatturaBenchmark {

    @Param({"1", "5", "10", "20"})
    public int items; // numero righe "acquisti"

    private String numCarta;
    private String nome;
    private String cognome;
    private String viaCap;
    private String citta;

    private float prezzoTotale;
    private int[] idMaglietta;
    private int[] quantita;
    private float[] prezzoAq;

    private DateTimeFormatter dateFmt;
    private LocalDate ordineDate;

    @Setup(Level.Trial)
    public void setup() {
        numCarta = "1234-5678-9012-3456";
        nome = "Mario";
        cognome = "Rossi";
        viaCap = "Via Roma 1 00100";
        citta = "Napoli";

        prezzoTotale = 123.45f;

        idMaglietta = new int[items];
        quantita = new int[items];
        prezzoAq = new float[items];

        for (int i = 0; i < items; i++) {
            idMaglietta[i] = 1000 + i;
            quantita[i] = (i % 3) + 1;
            prezzoAq[i] = 9.99f + i;
        }

        dateFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        ordineDate = LocalDate.of(2025, 1, 15);
    }

    /**
     * Simula la parte "core" di StampaFattura:
     * - scritture di testo a coordinate fisse
     * - loop sugli acquisti
     * - save del PDF (qui su memoria, non su file)
     */
    @Benchmark
    public void pdfbox_generate_invoice_in_memory(final Blackhole bh) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                contentStream.setFont(PDType1Font.HELVETICA, 8);

                // come in StampaFattura: numCarta
                contentStream.beginText();
                contentStream.newLineAtOffset(446.609f, 767.3385f);
                contentStream.showText(numCarta);
                contentStream.endText();

                // data corrente
                contentStream.beginText();
                contentStream.newLineAtOffset(430.864f, 635.7037f);
                contentStream.showText(LocalDate.now().format(dateFmt));
                contentStream.endText();

                // data ordine
                contentStream.beginText();
                contentStream.newLineAtOffset(430.864f, 618.963f);
                contentStream.showText(ordineDate.format(dateFmt));
                contentStream.endText();

                // nome, cognome, indirizzo, città
                contentStream.beginText();
                contentStream.newLineAtOffset(46.6203f, 642.2537f);
                contentStream.showText(nome);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(46.6203f, 631.2537f);
                contentStream.showText(cognome);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(46.6203f, 620.2537f);
                contentStream.showText(viaCap);
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(46.6203f, 609.2537f);
                contentStream.showText(citta);
                contentStream.endText();

                // prezzo totale
                contentStream.beginText();
                contentStream.newLineAtOffset(405.1517f, 591.2341f);
                contentStream.showText(prezzoTotale + " euro");
                contentStream.endText();

                // loop acquisti (replica della tua logica coord -= 15)
                float coord = 448.0316f;
                for (int i = 0; i < items; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(92.7409f, coord);
                    contentStream.showText(String.valueOf(idMaglietta[i]));
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(288.2813f, coord);
                    contentStream.showText(String.valueOf(quantita[i]));
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(344.4793f, coord);
                    contentStream.showText(prezzoAq[i] + " euro");
                    contentStream.endText();

                    contentStream.beginText();
                    contentStream.newLineAtOffset(515.6068f, coord);
                    contentStream.showText((prezzoAq[i] * quantita[i]) + " euro");
                    contentStream.endText();

                    coord -= 15f;
                }
            }

            // come document.save(...), ma in RAM
            ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024);
            document.save(baos);

            bh.consume(baos.size());
        }
    }
}

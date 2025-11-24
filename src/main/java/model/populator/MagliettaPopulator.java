package model.populator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MagliettaPopulator implements TablePopulator {

    private final DataSource ds;

    public MagliettaPopulator(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void populate() throws Exception {
        if (!isEmpty()) return;

        String q = "INSERT INTO Maglietta(ID, nome, prezzo, IVA, colore, tipo, grafica, descrizione) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            for (Object[] row : DATA) {
                ps.setInt(1, (int) row[0]);
                ps.setString(2, (String) row[1]);
                ps.setFloat(3, (float) row[2]);
                ps.setInt(4, (int) row[3]);
                ps.setString(5, (String) row[4]);
                ps.setString(6, (String) row[5]);
                ps.setString(7, (String) row[6]);
                ps.setString(8, (String) row[7]);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private boolean isEmpty() throws Exception {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM Maglietta");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static final Object[][] DATA = {
            {1, "Mercoledi", 15f, 3, "Bianco", "Film e Serie TV", "images/grafiche/1Film e Serie TV.jpg", "Maglietta di Mercoledì"},
            {2, "Game of Thrones", 15f, 3, "Nero", "Film e Serie TV", "images/grafiche/2Film e Serie TV.jpg", "Maglietta di Game of Thrones"},
            {3, "Stranger Things", 15f, 3, "Bianco", "Film e Serie TV", "images/grafiche/3Film e Serie TV.jpg", "Maglietta di Stranger Things"},
            {4, "Bud Spencer", 15f, 3, "Nero", "Film e Serie TV", "images/grafiche/4Film e Serie TV.png", "Maglietta del GRANDE Bud Spencer (il sommo, il mio preferito)"},
            {5, "Naruto", 16f, 3, "Nero", "Anime e Manga", "images/grafiche/5Anime e Manga.jpg", "Maglietta di Naruto"},
            {6, "Anime", 12f, 3, "Nero", "Anime e Manga", "images/grafiche/6Anime e Manga.jpg", "Generica maglietta sugli anime"},
            {7, "JoJo Reference", 18f, 3, "Blu", "Anime e Manga", "images/grafiche/7Anime e Manga.png", "Maglietta di JoJo"},
            {8, "Demon Slayer", 23f, 3, "Nero", "Anime e Manga", "images/grafiche/8Anime e Manga.png", "Maglietta di Demon Slayer"},
            {9, "Kuromi", 25f, 3, "Nero", "Girl Power", "images/grafiche/9Girl Power.png", "Maglietta di Kuromi"},
            {10, "Sailor Moon", 17f, 3, "Bianco", "Girl Power", "images/grafiche/10Girl Power.png", "Maglietta di Sailor Moon"},
            {11, "Pulp Fiction", 15f, 3, "Nero", "Film e Serie TV", "images/grafiche/11Film e Serie TV.jpg", "Maglietta Pulp Fiction"},
            {12, "Interstellar", 17f, 3, "Nero", "Film e Serie TV", "images/grafiche/12Film e Serie TV.jpg", "Maglietta Interstellar"},
            {13, "Aristogatti", 15f, 3, "Rosa", "Film e Serie TV", "images/grafiche/13Film e Serie TV.jpg", "Maglietta degli aristogatti"},
            {14, "Garfield", 16f, 3, "Arancione", "Film e Serie TV", "images/grafiche/14Film e Serie TV.jpg", "Maglietta Garfield"},
            {15, "Sailor Moon", 14f, 3, "Nero", "Girl Power", "images/grafiche/15Girl Power.png", "Maglietta Sailor Moon gattino mao"},
            {16, "Paperino", 15f, 3, "Azzurro", "Film e Serie TV", "images/grafiche/16Film e Serie TV.jpg", "Maglietta Paperino"},
            {17, "Criminal Minds", 15f, 3, "Nero", "Film e Serie TV", "images/grafiche/17Film e Serie TV.jpg", "Maglietta Criminal Minds"},
            {18, "The Resident", 15f, 2, "Bianco", "Film e Serie TV", "images/grafiche/18Film e Serie TV.jpg", "Maglietta The Resident"},
            {19, "I maghi di Waverly", 17f, 2, "Bianco", "Film e Serie TV", "images/grafiche/19Film e Serie TV.jpg", "Maglietta I maghi di Waverly"},
            {20, "Hannah Montana", 15f, 3, "Rosa", "Girl Power", "images/grafiche/20Girl Power.jpg", "Maglietta Hannah Montana"},
            {21, "Steins;Gate", 17f, 3, "Nero", "Anime e Manga", "images/grafiche/21Anime e Manga.jpeg", "Maglietta Steins;Gate"},
            {22, "Holly e Benji", 15f, 3, "Azzurro", "Anime e Manga", "images/grafiche/22Anime e Manga.jpg", "Maglietta Holly e Benji"},
            {23, "Violet Evergarden", 17f, 2, "Bianco", "Girl Power", "images/grafiche/23Girl Power.jpg", "Maglietta Violet Evergarden"},
            {24, "Your Name", 15f, 3, "Bianco", "Anime e Manga", "images/grafiche/24Anime e Manga.jpg", "Maglietta Your Name."},
            {25, "Peanuts", 16f, 2, "Nero", "Fumetti", "images/grafiche/25Fumetti.jpg", "Maglietta Peanuts"},
            {26, "Topolino", 18f, 3, "Bianco", "Fumetti", "images/grafiche/26Fumetti.jpg", "Maglietta Topolino"},
            {27, "Diabolik", 15f, 2, "Nero", "Fumetti", "images/grafiche/27Fumetti.jpg", "Maglietta Diabolik"},
            {28, "Alice in Wonderland", 17f, 3, "Bianco", "Girl Power", "images/grafiche/28Girl Power.jpg", "Maglietta Alice in Wonderland"},
            {29, "Il castello errante di Howl", 18f, 2, "Azzurro", "Anime e Manga", "images/grafiche/29Anime e Manga.webp", "Maglietta Il castello errante di Howl"},
            {30, "La città incantata", 17f, 3, "Viola", "Anime e Manga", "images/grafiche/30Anime e Manga.jpg", "Maglietta La città incantata"},
            {31, "La Sirenetta", 15f, 2, "Giallo", "Film e Serie TV", "images/grafiche/31Film e Serie TV.jpg", "Maglietta La Sirenetta"},
            {32, "Blu", 20f, 3, "Blu", "Personalizzata", "images/grafiche/32Personalizzata Blu.png", "Maglietta blu per la personalizzazione"},
            {33, "Bianca", 20f, 3, "Bianca", "Personalizzata", "images/grafiche/33Personalizzata Bianca.png", "Maglietta bianca per la personalizzazione"},
            {34, "Nera", 20f, 3, "Nera", "Personalizzata", "images/grafiche/34Personalizzata Nera.png", "Maglietta nera per la personalizzazione"},
            {35, "Rossa", 20f, 3, "Rossa", "Personalizzata", "images/grafiche/35Personalizzata Rossa.png", "Maglietta rossa per la personalizzazione"},
            {36, "Verde", 20f, 3, "Verde", "Personalizzata", "images/grafiche/36Personalizzata Verde.png", "Maglietta verde per la personalizzazione"},
            {37, "Viola", 20f, 3, "Viola", "Personalizzata", "images/grafiche/37Personalizzata Viola.png", "Maglietta viola per la personalizzazione"}
    };
}


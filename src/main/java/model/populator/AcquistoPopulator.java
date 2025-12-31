package model.populator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AcquistoPopulator {

    private final DataSource ds;

    public AcquistoPopulator(DataSource ds) {
        this.ds = ds;
    }

    public void populate() throws SQLException {
        if (!isEmpty()) return;

        String q = "INSERT INTO Acquisto (IDOrdine, IDMaglietta, quantita, immagine, prezzoAq, ivaAq, taglia) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {

            Object[][] data = DATA;

            for (Object[] row : data) {
                ps.setInt(1, (int) row[0]);
                ps.setInt(2, (int) row[1]);
                ps.setInt(3, (int) row[2]);
                ps.setString(4, null);
                ps.setFloat(5, (float) row[4]);
                ps.setInt(6, (int) row[5]);
                ps.setString(7, (String) row[6]);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private boolean isEmpty() throws SQLException {
        String q = "SELECT COUNT(*) FROM Acquisto";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static final Object[][] DATA = {
            {1,4,1,null,15f,22,"XS"},
            {2,7,3,null,15f,22,"S"},
            {3,10,1,null,15f,22,"M"},
            {4,6,2,null,15f,22,"L"},
            {5,7,3,null,15f,22,"XL"},
            {6,23,2,null,15f,22,"XXL"},
            {7,10,5,null,15f,22,"S"},
            {8,1,5,null,15f,22,"M"},
            {9,24,4,null,15f,22,"XS"},
            {10,18,3,null,15f,22,"M"},
            {11,12,4,null,15f,22,"XL"},
            {12,17,3,null,15f,22,"L"},
            {13,16,5,null,15f,22,"XS"},
            {14,5,1,null,15f,22,"M"},
            {15,7,3,null,15f,22,"XS"},
            {16,11,5,null,15f,22,"L"},
            {17,14,3,null,15f,22,"M"},
            {18,2,5,null,15f,22,"XXL"},
            {19,2,1,null,15f,22,"S"},
            {20,8,1,null,15f,22,"XL"},
            {21,28,4,null,15f,22,"M"},
            {22,14,3,null,15f,22,"L"},
            {23,8,4,null,15f,22,"M"},
            {24,5,2,null,15f,22,"XXL"},
            {25,9,1,null,15f,22,"S"},
            {26,11,3,null,15f,22,"L"},
            {27,2,3,null,15f,22,"M"},
            {28,22,2,null,15f,22,"XL"},
            {29,16,4,null,15f,22,"XL"},
            {30,23,4,null,15f,22,"M"},
            {1,20,2,null,15f,22,"L"},
            {2,10,4,null,15f,22,"S"},
            {3,23,3,null,15f,22,"S"},
            {4,15,3,null,15f,22,"L"},
            {5,23,5,null,15f,22,"XXL"},
            {6,19,4,null,15f,22,"L"},
            {7,10,2,null,15f,22,"S"},
            {8,5,5,null,15f,22,"L"},
            {9,27,2,null,15f,22,"XL"},
            {10,25,4,null,15f,22,"XXL"}
    };
}


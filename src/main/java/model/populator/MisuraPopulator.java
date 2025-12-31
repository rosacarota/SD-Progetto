package model.populator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MisuraPopulator implements TablePopulator {

    private final DataSource ds;

    public MisuraPopulator(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void populate() throws SQLException {
        if (!isEmpty()) return;

        String q = "INSERT INTO Misura(IDMaglietta, taglia, quantita) VALUES (?, ?, ?)";

        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            for (Object[] row : DATA) {
                ps.setInt(1, (int) row[0]);
                ps.setString(2, (String) row[1]);
                ps.setInt(3, (int) row[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private boolean isEmpty() throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM Misura");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static final Object[][] DATA = {
        {1,"XS",50},{1,"S",50},{1,"M",50},{1,"L",50},{1,"XL",50},{1,"XXL",50},
        {2,"XS",50},{2,"S",50},{2,"M",50},{2,"L",50},{2,"XL",50},{2,"XXL",50},
        {3,"XS",50},{3,"S",50},{3,"M",50},{3,"L",50},{3,"XL",50},{3,"XXL",50},
        {4,"XS",50},{4,"S",50},{4,"M",50},{4,"L",50},{4,"XL",50},{4,"XXL",50},
        {5,"XS",50},{5,"S",50},{5,"M",50},{5,"L",50},{5,"XL",50},{5,"XXL",50},
        {6,"XS",50},{6,"S",50},{6,"M",50},{6,"L",50},{6,"XL",50},{6,"XXL",50},
        {7,"XS",50},{7,"S",50},{7,"M",50},{7,"L",50},{7,"XL",50},{7,"XXL",50},
        {8,"XS",50},{8,"S",50},{8,"M",50},{8,"L",50},{8,"XL",50},{8,"XXL",50},
        {9,"XS",50},{9,"S",50},{9,"M",50},{9,"L",50},{9,"XL",50},{9,"XXL",50},
        {10,"XS",50},{10,"S",50},{10,"M",50},{10,"L",50},{10,"XL",50},{10,"XXL",50},
        {11,"XS",50},{11,"S",50},{11,"M",50},{11,"L",50},{11,"XL",50},{11,"XXL",50},
        {12,"XS",50},{12,"S",50},{12,"M",50},{12,"L",50},{12,"XL",50},{12,"XXL",50},
        {13,"XS",50},{13,"S",50},{13,"M",50},{13,"L",50},{13,"XL",50},{13,"XXL",50},
        {14,"XS",50},{14,"S",50},{14,"M",50},{14,"L",50},{14,"XL",50},{14,"XXL",50},
        {15,"XS",50},{15,"S",50},{15,"M",50},{15,"L",50},{15,"XL",50},{15,"XXL",50},
        {16,"XS",50},{16,"S",50},{16,"M",50},{16,"L",50},{16,"XL",50},{16,"XXL",50},
        {17,"XS",50},{17,"S",50},{17,"M",50},{17,"L",50},{17,"XL",50},{17,"XXL",50},
        {18,"XS",50},{18,"S",50},{18,"M",50},{18,"L",50},{18,"XL",50},{18,"XXL",50},
        {19,"XS",50},{19,"S",50},{19,"M",50},{19,"L",50},{19,"XL",50},{19,"XXL",50},
        {20,"XS",50},{20,"S",50},{20,"M",50},{20,"L",50},{20,"XL",50},{20,"XXL",50},
        {21,"XS",50},{21,"S",50},{21,"M",50},{21,"L",50},{21,"XL",50},{21,"XXL",50},
        {22,"XS",50},{22,"S",50},{22,"M",50},{22,"L",50},{22,"XL",50},{22,"XXL",50},
        {23,"XS",50},{23,"S",50},{23,"M",50},{23,"L",50},{23,"XL",50},{23,"XXL",50},
        {24,"XS",50},{24,"S",50},{24,"M",50},{24,"L",50},{24,"XL",50},{24,"XXL",50},
        {25,"XS",50},{25,"S",50},{25,"M",50},{25,"L",50},{25,"XL",50},{25,"XXL",50},
        {26,"XS",50},{26,"S",50},{26,"M",50},{26,"L",50},{26,"XL",50},{26,"XXL",50},
        {27,"XS",50},{27,"S",50},{27,"M",50},{27,"L",50},{27,"XL",50},{27,"XXL",50},
        {28,"XS",50},{28,"S",50},{28,"M",50},{28,"L",50},{28,"XL",50},{28,"XXL",50},
        {29,"XS",50},{29,"S",50},{29,"M",50},{29,"L",50},{29,"XL",50},{29,"XXL",50},
        {30,"XS",50},{30,"S",50},{30,"M",50},{30,"L",50},{30,"XL",50},{30,"XXL",50},
        {33,"XS",50},{33,"S",50},{33,"M",50},{33,"L",50},{33,"XL",50},{33,"XXL",50},
        {34,"XS",50},{34,"S",50},{34,"M",50},{34,"L",50},{34,"XL",50},{34,"XXL",50},
        {35,"XS",50},{35,"S",50},{35,"M",50},{35,"L",50},{35,"XL",50},{35,"XXL",50},
        {36,"XS",50},{36,"S",50},{36,"M",50},{36,"L",50},{36,"XL",50},{36,"XXL",50},
        {37,"XS",50},{37,"S",50},{37,"M",50},{37,"L",50},{37,"XL",50},{37,"XXL",50}
    };
}


package model.populator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TagliaPopulator implements TablePopulator {

    private final DataSource ds;

    public TagliaPopulator(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void populate() throws Exception {
        if (!isEmpty()) return;

        String q = "INSERT INTO Taglia(taglia) VALUES (?)";

        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            for (String t : DATA) {
                ps.setString(1, t);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private boolean isEmpty() throws Exception {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM Taglia");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static final String[] DATA = {"XS","S","M","L","XL","XXL"};
}


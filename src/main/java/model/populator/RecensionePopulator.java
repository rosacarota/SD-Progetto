package model.populator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RecensionePopulator {

    private final DataSource ds;

    public RecensionePopulator(DataSource ds) {
        this.ds = ds;
    }

    public void populate() throws Exception {
        if (!isEmpty()) return;

        String q = "INSERT INTO Recensione (ID, IDMaglietta, username, contenuto) VALUES (?, ?, ?, ?)";

        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {

            Object[][] data = DATA;

            for (Object[] row : data) {
                ps.setInt(1, (int) row[0]);
                ps.setInt(2, (int) row[1]);
                ps.setString(3, (String) row[2]);
                ps.setString(4, (String) row[3]);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private boolean isEmpty() throws Exception {
        String q = "SELECT COUNT(*) FROM Recensione";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static final Object[][] DATA = {
            {1,21,"ebomfieldr","Aliquam erat volutpat. In congue. Etiam justo."},
            {2,26,"econradiei","Duis bibendum, felis sed..."},
            {3,8,"cphipp0","Integer ac neque. Duis bibendum..."},
            {4,23,"jmaddicksb","Etiam faucibus cursus urna..."},
            {5,15,"dodoughertyh","Nunc nisl."},
            {6,10,"arenault5","Maecenas ut massa quis augue..."},
            {7,25,"klelievre3","Suspendisse potenti."},
            {8,14,"fbarnbrook9","Etiam faucibus cursus urna."},
            {9,22,"jtironeg","Cras mi pede..."},
            {10,16,"klelievre3","Nulla neque libero..."},
            {11,25,"nlockner4","Aliquam sit amet diam..."},
            {12,15,"dodoughertyh","Aliquam erat volutpat."},
            {13,28,"rgethinsa","Suspendisse potenti..."},
            {14,18,"ltayloure","Donec odio justo..."},
            {15,23,"sbrouwerj","Praesent id massa..."},
            {16,24,"klelievre3","Suspendisse potenti..."},
            {17,11,"gflucks1","Maecenas tristique..."},
            {18,6,"cbalbeckk","Suspendisse ornare..."},
            {19,18,"nlockner4","Integer non velit..."},
            {20,4,"cmcgucken2","Duis mattis egestas..."},
            {21,20,"slarmouthc","Morbi vestibulum..."},
            {22,4,"sbittlestone6","Fusce consequat..."},
            {23,4,"agovenlockf","Nam dui..."},
            {24,8,"cmoulstert","Etiam vel augue..."},
            {25,10,"cmcgucken2","Morbi ut odio."},
            {26,28,"klelievre3","Proin risus."},
            {27,23,"rgethinsa","Nulla ac enim..."},
            {28,24,"sbrouwerj","Mauris enim leo..."},
            {29,22,"aologhlen7","Vestibulum ante ipsum..."},
            {30,23,"econradiei","In congue..."},
            {31,2,"slarmouthc","Duis at velit..."},
            {32,24,"sbrouwerj","Suspendisse potenti..."},
            {33,3,"nlockner4","Sed vel enim..."},
            {34,17,"sbrouwerj","Integer aliquet..."},
            {35,23,"arenault5","Duis bibendum..."},
            {36,18,"aologhlen7","Integer a nibh."},
            {37,20,"msolland8","Nulla tempus..."},
            {38,11,"klelievre3","Mauris enim leo..."},
            {39,18,"rgethinsa","Curabitur in libero..."},
            {40,18,"clonergano","Sed accumsan felis."},
            {41,4,"sbrouwerj","Cum sociis natoque..."},
            {42,17,"arenault5","Cum sociis..."},
            {43,22,"sbittlestone6","Nunc purus."},
            {44,4,"jarmfieldp","Morbi non quam..."},
            {45,27,"fbarnbrook9","Vestibulum ante ipsum..."},
            {46,3,"agovenlockf","Praesent blandit..."},
            {47,26,"cphipp0","Nunc purus."},
            {48,28,"nskeneq","In tempor..."},
            {49,19,"econradiei","Aliquam erat volutpat."},
            {50,10,"nskeneq","Proin interdum..."},
            {51,14,"aologhlen7","Morbi ut odio."},
            {52,26,"msolland8","Aenean fermentum..."},
            {53,23,"econradiei","In hac habitasse..."},
            {54,2,"jarmfieldp","Donec ut mauris..."},
            {55,10,"jtironeg","Etiam faucibus cursus..."},
            {56,16,"gidwalevansd","Donec ut dolor..."},
            {57,16,"sferons","Mauris ullamcorper..."},
            {58,9,"gfernelym","Nulla tellus..."},
            {59,28,"tchastonl","Vivamus vestibulum..."},
            {60,24,"clonergano","Aliquam augue quam..."}
    };
}


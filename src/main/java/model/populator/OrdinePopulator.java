package model.populator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class OrdinePopulator {

    private final DataSource ds;

    public OrdinePopulator(DataSource ds) {
        this.ds = ds;
    }

    public void populate() throws SQLException {
        if (!isEmpty()) return;

        String q = "INSERT INTO Ordine (ID, username, prezzoTotale, dataConsegna, dataOrdine, nomeConsegna, cognomeConsegna, cap, via, citta) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            Object[][] data = DATA;

            for (Object[] row : data) {
                ps.setInt(1, (int) row[0]);
                ps.setString(2, (String) row[1]);
                ps.setFloat(3, (float) row[2]);
                ps.setDate(4, java.sql.Date.valueOf((LocalDate) row[3]));
                ps.setDate(5, java.sql.Date.valueOf((LocalDate) row[4]));
                ps.setString(6, (String) row[5]);
                ps.setString(7, (String) row[6]);
                ps.setString(8, (String) row[7]);
                ps.setString(9, (String) row[8]);
                ps.setString(10, (String) row[9]);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private boolean isEmpty() throws SQLException {
        String q = "SELECT COUNT(*) FROM Ordine";
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static final Object[][] DATA = {
            {1,"dodoughertyh",30f,local("2023-05-30"),local("2023-05-23"),"Darren","O'Dougherty","40745","02042 Independence Point","Dongfanghong"},
            {2,"econradiei",30f,local("2023-05-08"),local("2023-05-01"),"Erna","Conradie","51484","086 Lunder Terrace","Babakan Baru"},
            {3,"sbittlestone6",30f,local("2023-05-14"),local("2023-05-07"),"Shandeigh","Bittlestone","39531","38117 Maple Park","Castro"},
            {4,"sbrouwerj",30f,local("2023-06-09"),local("2023-06-02"),"Sallee","Brouwer","31671","37 Cherokee Street","Oranmore"},
            {5,"slarmouthc",30f,local("2023-05-10"),local("2023-05-03"),"Susie","Larmouth","09396","9077 1st Point","Dalu"},
            {6,"fbarnbrook9",30f,local("2023-05-18"),local("2023-05-11"),"Flo","Barnbrook","40275","92566 Loftsgordon Road","Kasui"},
            {7,"tchastonl",30f,local("2023-06-11"),local("2023-06-04"),"Ted","Chaston","30103","76 Lindbergh Terrace","Bejsce"},
            {8,"clonergano",30f,local("2023-05-21"),local("2023-05-14"),"Carlota","Lonergan","07604","335 Parkside Terrace","Shahrak-e Bükharz"},
            {9,"jmaddicksb",30f,local("2023-05-20"),local("2023-05-13"),"Jocelyne","Maddicks","65172","4 Dryden Junction","Yushu"},
            {10,"clonergano",30f,local("2023-05-30"),local("2023-05-23"),"Carlota","Lonergan","86357","4494 Grim Avenue","Anaheim"},
            {11,"econradiei",15f,local("2023-05-18"),local("2023-05-11"),"Erna","Conradie","24285","03 Morningstar Street","Rancapare"},
            {12,"msolland8",15f,local("2023-05-22"),local("2023-05-15"),"Major","Solland","55403","88701 Elgar Avenue","Ban½ Suwayf"},
            {13,"rgethinsa",15f,local("2023-06-07"),local("2023-05-31"),"Roana","Gethins","89027","75 Barnett Parkway","Akhaldaba"},
            {14,"sbittlestone6",15f,local("2023-05-20"),local("2023-05-13"),"Shandeigh","Bittlestone","64612","7115 Eastlawn Court","Fengshan"},
            {15,"agovenlockf",15f,local("2023-05-09"),local("2023-05-02"),"Alanna","Govenlock","02467","777 Bellgrove Trail","Mosfilotí"},
            {16,"sferons",15f,local("2023-05-23"),local("2023-05-16"),"Sandro","Feron","05012","388 Helena Terrace","Tygda"},
            {17,"nlockner4",15f,local("2023-06-10"),local("2023-06-03"),"Nixie","Lockner","59161","78 Crest Line Lane","Bakalang"},
            {18,"gfernelym",15f,local("2023-06-03"),local("2023-05-27"),"Gunner","Fernely","91462","7400 Fallview Road","Duanshen"},
            {19,"ltayloure",15f,local("2023-05-23"),local("2023-05-16"),"Lanni","Taylour","03513","8176 Roth Way","Hashtgerd"},
            {20,"cmcgucken2",15f,local("2023-06-10"),local("2023-06-03"),"Culley","McGucken","98805","98864 Charing Cross Park","Bira"},
            {21,"jmaddicksb",15f,local("2023-06-11"),local("2023-06-04"),"Jocelyne","Maddicks","27066","34 Logan Junction","Klimontów"},
            {22,"econradiei",15f,local("2023-05-22"),local("2023-05-15"),"Erna","Conradie","94924","059 Old Gate Court","Houmen"},
            {23,"cmoulstert",15f,local("2023-05-21"),local("2023-05-14"),"Craggy","Moulster","13806","1977 5th Plaza","Jindřichov"},
            {24,"gfernelym",15f,local("2023-05-19"),local("2023-05-12"),"Gunner","Fernely","03782","54017 Roth Street","Bautista"},
            {25,"cmoulstert",15f,local("2023-06-08"),local("2023-06-01"),"Craggy","Moulster","21803","6 Thierer Trail","Sandefjord"},
            {26,"fbarnbrook9",15f,local("2023-05-13"),local("2023-05-06"),"Flo","Barnbrook","84334","9811 Emmet Court","Kuala Lumpur"},
            {27,"gfernelym",15f,local("2023-05-23"),local("2023-05-16"),"Gunner","Fernely","54575","76173 Harper Court","Milton"},
            {28,"sbittlestone6",15f,local("2023-06-11"),local("2023-06-04"),"Shandeigh","Bittlestone","71487","7280 Bayside Crossing","Lapid"},
            {29,"clonergano",15f,local("2023-06-10"),local("2023-06-03"),"Carlota","Lonergan","74085","84 West Center","Geputan"},
            {30,"sferons",15f,local("2023-06-08"),local("2023-06-01"),"Sandro","Feron","52026","50 Forster Alley","MacArthur"}
    };

    private static LocalDate local(String d) { return LocalDate.parse(d); }
}


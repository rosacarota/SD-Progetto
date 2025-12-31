package model.populator;

import model.utente.UtenteBean;
import model.utente.UtenteDAO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtentePopulator implements TablePopulator {

    private final DataSource dataSource;

    public UtentePopulator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void populate() throws SQLException {
        if (!isEmpty()) {
            return;
        }
        List<UtenteBean> users = loadUsers();
        UtenteDAO dao = new UtenteDAO();
        for (UtenteBean u : users) {
            dao.doSave(u);
        }
    }

    private boolean isEmpty() throws SQLException {
        String query = "SELECT COUNT(*) FROM Utente";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1) == 0;
        }
    }

    private List<UtenteBean> loadUsers() {
        List<UtenteBean> list = new ArrayList<>();

        list.add(make("admin","admin","admin","admin","admin",
                LocalDate.parse("2023-05-12"), null,null,null,null,null,
                null,null,null,"admin"));

        list.add(make("agovenlockf","ZJNtfPtjYa2","Alanna","Govenlock","agovenlockf@oracle.com",
                LocalDate.parse("1970-10-24"),"Alanna","Govenlock","374288773429328",
                LocalDate.parse("2025-08-30"),"12345","70119","374 Meadow Ridge Parkway",
                "Trzemeszno","user"));

        list.add(make("aologhlen7","md1uFwQzgK91","Ambrosio","O'Loghlen","aologhlen7@aboutads.info",
                LocalDate.parse("2002-01-07"),"Ambrosio","O'Loghlen","3536362078660470",
                LocalDate.parse("2028-03-15"),"12345","59977","8 Forster Place",
                "Zhaitou","user"));

        list.add(make("arenault5","Clvfwt2Gu7Lc","Annabella","Renault","arenault5@house.gov",
                LocalDate.parse("1963-11-15"),"Annabella","Renault","5610529600346731078",
                LocalDate.parse("2026-09-04"),"12345","87199","31970 Jana Lane",
                "Oborniki ┼Ül─àskie","user"));

        list.add(make("aseggen","9Qq6pHUBARAE","Alyce","Segge","aseggen@statcounter.com",
                LocalDate.parse("1990-12-18"),"Alyce","Segge","3550656637414357",
                LocalDate.parse("2027-02-17"),"12345","01157","17193 Jenna Pass",
                "Thß╗ï Trß║Ñn Thß╗ì Xu├ón","user"));

        list.add(make("cbalbeckk","9t5C4f","Cornell","Balbeck","cbalbeckk@sogou.com",
                LocalDate.parse("1986-08-05"),"Cornell","Balbeck","201996451814145",
                LocalDate.parse("2029-01-21"),"12345","61062","4186 Texas Hill",
                "Corticeiro de Baixo","user"));

        list.add(make("clonergano","cAZPf3vAU","Carlota","Lonergan","clonergano@scribd.com",
                LocalDate.parse("1989-03-28"),"Carlota","Lonergan","3574939790589906",
                LocalDate.parse("2028-04-30"),"12345","95104","00 Cottonwood Junction",
                "Mahates","user"));

        list.add(make("cmcgucken2","6S1rng5kX","Culley","McGucken","cmcgucken2@sbwire.com",
                LocalDate.parse("1991-08-16"),"Culley","McGucken","36442967633902",
                LocalDate.parse("2026-11-17"),"12345","59897","0 Grover Pass",
                "Turkestan","user"));

        list.add(make("cmoulstert","kMyLKqL3h","Craggy","Moulster","cmoulstert@amazon.com",
                LocalDate.parse("2002-05-12"),"Craggy","Moulster","3540465736761111",
                LocalDate.parse("2028-09-01"),"12345","01912","0051 Buell Place",
                "Hengdian","user"));

        list.add(make("cphipp0","LFZUUXjnS9","Calypso","Phipp","cphipp0@posterous.com",
                LocalDate.parse("1975-12-15"),"Calypso","Phipp","5602259305531520",
                LocalDate.parse("2028-07-01"),"12345","78751","0924 Jana Trail",
                "Nereta","user"));

        list.add(make("dodoughertyh","gmTuZB","Darren","O'Dougherty","dodoughertyh@mit.edu",
                LocalDate.parse("1984-03-10"),"Darren","O'Dougherty","3572791630934293",
                LocalDate.parse("2029-03-10"),"12345","78402","4983 Green Ridge Junction",
                "Kirovgrad","user"));

        list.add(make("ebomfieldr","HyGC7mDTT","Elvira","Bomfield","ebomfieldr@nationalgeographic.com",
                LocalDate.parse("1986-06-24"),"Elvira","Bomfield","3531967784979967",
                LocalDate.parse("2028-09-06"),"12345","14169","4708 Ohio Point",
                "Roma","user"));

        list.add(make("econradiei","Oyqdojzx","Erna","Conradie","econradiei@privacy.gov.au",
                LocalDate.parse("1969-11-11"),"Erna","Conradie","670600954715283646",
                LocalDate.parse("2026-02-15"),"12345","80080","446 Kinsman Trail",
                "Padhahegha","user"));

        list.add(make("fbarnbrook9","35bV42eoIt","Flo","Barnbrook","fbarnbrook9@dmoz.org",
                LocalDate.parse("1994-07-13"),"Flo","Barnbrook","36888646157053",
                LocalDate.parse("2025-10-06"),"12345","00126","7701 Blackbird Pass",
                "Jinping","user"));

        list.add(make("gfernelym","MbZa4rcKcR","Gunner","Fernely","gfernelym@google.com.br",
                LocalDate.parse("1990-01-17"),"Gunner","Fernely","201490374406585",
                LocalDate.parse("2027-05-26"),"12345","82259","9981 Forest Dale Crossing",
                "Bluri","user"));

        list.add(make("gflucks1","0KNTOZqVDF","Gianina","Flucks","gflucks1@soundcloud.com",
                LocalDate.parse("2003-12-04"),"Gianina","Flucks","3549105101434644",
                LocalDate.parse("2027-06-28"),"12345","61244","8 Esch Park",
                "Koundara","user"));

        list.add(make("gidwalevansd","awkUzNGmcOzF","Gert","Idwal Evans","gidwalevansd@amazonaws.com",
                LocalDate.parse("1961-08-30"),"Gert","Idwal Evans","3533409596742760",
                LocalDate.parse("2026-07-04"),"12345","16810","1 Lyons Lane",
                "Topolovgrad","user"));

        list.add(make("jarmfieldp","vPHONxyJxER","Jennifer","Armfield","jarmfieldp@vimeo.com",
                LocalDate.parse("1986-04-27"),"Jennifer","Armfield","67615079647924265",
                LocalDate.parse("2025-07-19"),"12345","33268","422 Toban Place",
                "Bangkle","user"));

        list.add(make("jmaddicksb","ktDH8DKi","Jocelyne","Maddicks","jmaddicksb@usda.gov",
                LocalDate.parse("1988-08-02"),"Jocelyne","Maddicks","4459672859008074",
                LocalDate.parse("2028-09-29"),"12345","97429","72625 Del Mar Alley",
                "Huaicheng","user"));

        list.add(make("jtironeg","x8rWaf","Jule","Tirone","jtironeg@about.me",
                LocalDate.parse("1998-08-23"),"Jule","Tirone","5100144892952163",
                LocalDate.parse("2025-10-22"),"12345","61516","161 Golf Course Hill",
                "Pokrovka","user"));

        list.add(make("klelievre3","kTXQyQfWMj","Kahaleel","Lelievre","klelievre3@thetimes.co.uk",
                LocalDate.parse("1969-12-06"),"Kahaleel","Lelievre","5108497127350302",
                LocalDate.parse("2025-08-01"),"12345","45172","87375 Michigan Center",
                "Bakungan","user"));

        list.add(make("ltayloure","98pNQnX","Lanni","Taylour","ltayloure@japanpost.jp",
                LocalDate.parse("1982-11-13"),"Lanni","Taylour","67619566239208061",
                LocalDate.parse("2025-07-08"),"12345","70889","33 Delladonna Place",
                "Tindog","user"));

        list.add(make("msolland8","1LS9mRVW2Wx3","Major","Solland","msolland8@jalbum.net",
                LocalDate.parse("1989-10-31"),"Major","Solland","63047057357415531",
                LocalDate.parse("2027-02-21"),"12345","27493","51981 Nova Court",
                "Rat├¡┼íkovice","user"));

        list.add(make("nlockner4","830L29YLWVB","Nixie","Lockner","nlockner4@furl.net",
                LocalDate.parse("1979-08-04"),"Nixie","Lockner","201419602648320",
                LocalDate.parse("2025-08-23"),"12345","39695","1 Farwell Place",
                "Nyk├Âping","user"));

        list.add(make("nskeneq","tlqPz8PF96","Neda","Skene","nskeneq@comcast.net",
                LocalDate.parse("2000-04-28"),"Neda","Skene","30260857131472",
                LocalDate.parse("2026-11-07"),"28387","12345","919 Melrose Court",
                "Kolobolon","user"));

        list.add(make("rgethinsa","5D5ajEWbz","Roana","Gethins","rgethinsa@blog.com",
                LocalDate.parse("1989-04-21"),"Roana","Gethins","201410134416500",
                LocalDate.parse("2027-03-28"),"12345","62657","861 Surrey Drive",
                "Amqui","user"));

        list.add(make("sbittlestone6","8YpzCljci6o","Shandeigh","Bittlestone","sbittlestone6@chronoengine.com",
                LocalDate.parse("1991-08-12"),"Shandeigh","Bittlestone","560224701266557244",
                LocalDate.parse("2025-12-06"),"12345","38609","584 Golf Course Avenue",
                "Vilarinho da Castanheira","user"));

        list.add(make("sbrouwerj","egM3oyF3X","Sallee","Brouwer","sbrouwerj@yelp.com",
                LocalDate.parse("1969-12-06"),"Sallee","Brouwer","3544121540810570",
                LocalDate.parse("2025-12-16"),"12345","36371","8 Meadow Valley Drive",
                "Barayong","user"));

        list.add(make("sferons","dzhPifMJJUNR","Sandro","Feron","sferons@live.com",
                LocalDate.parse("1974-04-18"),"Sandro","Feron","6331105789968794094",
                LocalDate.parse("2026-11-27"),"12345","44682","05325 Bashford Junction",
                "Tagum","user"));

        list.add(make("slarmouthc","UslwRz666l","Susie","Larmouth","slarmouthc@nationalgeographic.com",
                LocalDate.parse("1987-10-26"),"Susie","Larmouth","5610112778422666542",
                LocalDate.parse("2028-01-24"),"12345","78269","276 Hoard Alley",
                "Kanbe","user"));

        list.add(make("tchastonl","yjmvNIBMEQ","Ted","Chaston","tchastonl@stanford.edu",
                LocalDate.parse("1999-07-29"),"Ted","Chaston","3571788550196508",
                LocalDate.parse("2028-06-02"),"12345","36615","0 Maple Wood Terrace",
                "Ampele├¡es","user"));

        list.add(make("username","username","Casotto","Mango Loco","username@example.com",
                LocalDate.parse("2002-04-05"),"Casotto","Mango Loco","4111111111111111",
                LocalDate.parse("2027-12-31"),"123","00100","Via Roma 1","Roma","user"));

        return list;
    }

    private UtenteBean make(
            String username,
            String password,
            String name,
            String surname,
            String email,
            LocalDate birth,
            String cardName,
            String cardSurname,
            String cardNumber,
            LocalDate exp,
            String cvv,
            String cap,
            String street,
            String city,
            String type
    ) {

        UtenteBean bean = new UtenteBean();
        bean.setUsername(username);
        bean.setPwd(password);
        bean.setNome(name);
        bean.setCognome(surname);
        bean.setEmail(email);
        bean.setDataNascita(birth);
        bean.setTipo(type);
        bean.setCap(cap);
        bean.setVia(street);
        bean.setCitta(city);
        bean.setNomeCarta(cardName);
        bean.setCognomeCarta(cardSurname);
        bean.setNumCarta(cardNumber);
        bean.setCVV(cvv);
        bean.setDataScadenza(exp);

        return bean;
    }
}
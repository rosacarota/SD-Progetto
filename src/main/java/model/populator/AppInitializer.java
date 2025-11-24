package model.populator;

import model.DBConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DataSource ds = DBConnection.getDataSource();

            new MagliettaPopulator(ds).populate();
            new TagliaPopulator(ds).populate();
            new MisuraPopulator(ds).populate();
            new UtentePopulator(ds).populate();
            new RecensionePopulator(ds).populate();
            new OrdinePopulator(ds).populate();
            new AcquistoPopulator(ds).populate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
    }
}


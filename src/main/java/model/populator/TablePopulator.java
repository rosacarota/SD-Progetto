package model.populator;

import java.sql.SQLException;

public interface TablePopulator {
    void populate() throws SQLException;
}

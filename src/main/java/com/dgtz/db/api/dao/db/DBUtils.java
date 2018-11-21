package com.dgtz.db.api.dao.db;

import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sardor on 12/1/15.
 */
public final class DBUtils {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DBUtils.class);

    public DBUtils() {
    }

    public synchronized static void closeConnections(PreparedStatement preparedStatement,
                                                     Connection dbConnection, ResultSet rs){
        try {
            if (rs != null) {
                rs.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (dbConnection != null) {
                dbConnection.commit();
                dbConnection.close();
            }
        }catch (SQLException e){
            log.error("ERROR IN DB API ", e);
        }

    }

    public synchronized static void dbRollback(Connection dbConnection){
        try {
            if (dbConnection != null) {
                dbConnection.rollback();
            }
        } catch (Exception e1) {
            log.error("ERROR IN DB API ", e1);
        }
    }
}

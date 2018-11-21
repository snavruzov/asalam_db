package com.dgtz.db.api.dao.db;

import com.dgtz.mcache.api.factory.Constants;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Sardor Navruzov CEO, DGTZ.
 */
public class JDBCUtil {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(JDBCUtil.class);

    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_CONNECTION = Constants.DB_CONNECTION;//"jdbc:postgresql://db.digitizen.com:6543/dgtz";
    private static final String DB_USER = Constants.DB_USER;
    private static final String DB_PASSWORD = Constants.DB_PASSWORD;


    public static Connection getDBConnection() {

        Connection dbConnection = null;

        try {

            Class.forName(DB_DRIVER);
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            dbConnection.setAutoCommit(false);

        } catch (SQLException | ClassNotFoundException e) {

            log.error("ERROR IN DB API ", e);
        }

        return dbConnection;

    }

    private static java.sql.Timestamp getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }
}

package me.soapiee.merger.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.Getter;
import me.soapiee.merger.utils.ConfigPath;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class HikariCPConnection {

    private final String HOST, DATABASE, USERNAME, PASSWORD;
    private final int PORT;

    @Getter private HikariDataSource database;

    public HikariCPConnection(FileConfiguration config) {
        HOST = config.getString(ConfigPath.DATABASE_HOST.getPath());
        PORT = config.getInt(ConfigPath.DATABASE_PORT.getPath());
        DATABASE = config.getString(ConfigPath.DATABASE_NAME.getPath());
        USERNAME = config.getString(ConfigPath.DATABASE_USERNAME.getPath());
        PASSWORD = config.getString(ConfigPath.DATABASE_PASSWORD.getPath());
    }

    private HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("sp-hikari");
        config.addDataSourceProperty("serverName", HOST);
        config.addDataSourceProperty("port", PORT);
        config.addDataSourceProperty("databaseName", DATABASE);
        config.setJdbcUrl("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=true");
        config.addDataSourceProperty("user", USERNAME);
        config.addDataSourceProperty("password", PASSWORD);
        config.setMaximumPoolSize(10);
        return config;
    }

    public void connect() throws IOException, SQLException, HikariPool.PoolInitializationException {
        HikariConfig config = getHikariConfig();
        database = new HikariDataSource(config);

        String setup;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("dbsetup.sql")) {
            setup = new BufferedReader(new InputStreamReader(in))
                    .lines()
                    .collect(Collectors.joining("\n"));

            try (Connection connection = this.database.getConnection();
                     PreparedStatement statement = connection.prepareStatement(setup)) {
                    statement.execute();
                }
        }
    }

    public boolean isConnected() {
        return database != null;
    }

    public void disconnect() {
        if (isConnected()) {
            database.close();
        }
    }

}

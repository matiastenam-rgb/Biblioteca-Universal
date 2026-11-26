package data;

import org.apache.commons.dbcp2.BasicDataSource;

public class Connect {

    private static BasicDataSource pool = null;

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Lectura de variables de entorno sin valores predeterminados hardcodeados
    private static String getRequiredEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Variable de entorno obligatoria no configurada: " + key);
        }
        return value.trim();
    }

    public static String getUrl() {
        String host = getRequiredEnv("DB_HOST");
        String port = getRequiredEnv("DB_PORT");
        String dbName = getRequiredEnv("DB_NAME");
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName
               + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
               + "&useUnicode=true&characterEncoding=UTF-8";
    }

    public static String getUser() {
        return getRequiredEnv("DB_USER");
    }

    public static String getPass() {
        return getRequiredEnv("DB_PASS");
    }

    public Connect() {}

    public static void create() {
        if (pool == null) {
            pool = new BasicDataSource();
            pool.setDriverClassName(DRIVER);
            pool.setUrl(getUrl());
            pool.setUsername(getUser());
            pool.setPassword(getPass());
            pool.setInitialSize(5);
            pool.setMaxTotal(10);
        }
    }

    public static java.sql.Connection getConnection() {
        try {
            return pool.getConnection();
        } catch (Exception e) {
            return null;
        }
    }

    public static void close() {
        if (pool != null) {
            try {
                pool.close();
            } catch (Exception e) {
                // ignorar
            }
        }
    }
}

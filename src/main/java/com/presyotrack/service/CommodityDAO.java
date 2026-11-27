package com.presyotrack.service;
import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public final class CommodityDAO {
    public record Quote(String name, String category, String unit, String market,
                        String date, double price, double benchmark) {
        public double difference() { return price - benchmark; }
    }
    public List<Quote> load() throws IOException, SQLException {
        Path db = Files.createTempFile("presyotrack-albay-", ".db");
        try {
            try (InputStream in = CommodityDAO.class.getResourceAsStream("/database/albay.db")) {
                if (in == null) throw new IOException("Bundled market database missing");
                Files.copy(in, db, StandardCopyOption.REPLACE_EXISTING);
            }
            List<Quote> result = new ArrayList<>();
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + db.toAbsolutePath());
                 Statement s = c.createStatement();
                 ResultSet r = s.executeQuery("SELECT name,category,unit,market,observed_on,price,benchmark_price FROM regional_comparison WHERE benchmark_price IS NOT NULL ORDER BY name,market,observed_on DESC")) {
                while (r.next()) result.add(new Quote(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getDouble(6),r.getDouble(7)));
            }
            return result;
        } finally { Files.deleteIfExists(db); }
    }
}

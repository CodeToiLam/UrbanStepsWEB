package vn.urbansteps.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseSchemaUpdater implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaUpdater.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Checking database schema and adding missing columns if needed");
        
        try {
            // Check if gia_cu column exists in SanPhamChiTiet table
            boolean giaCuExists = columnExists("SanPhamChiTiet", "gia_cu");
            boolean ngayThayDoiGiaExists = columnExists("SanPhamChiTiet", "ngay_thay_doi_gia");
            
            if (!giaCuExists) {
                logger.info("Adding gia_cu column to SanPhamChiTiet table");
                jdbcTemplate.execute("ALTER TABLE SanPhamChiTiet ADD gia_cu DECIMAL(19,2) NULL");
            }
            
            if (!ngayThayDoiGiaExists) {
                logger.info("Adding ngay_thay_doi_gia column to SanPhamChiTiet table");
                jdbcTemplate.execute("ALTER TABLE SanPhamChiTiet ADD ngay_thay_doi_gia DATETIME NULL");
            }
            
            logger.info("Database schema check completed successfully");
        } catch (Exception e) {
            logger.error("Error updating database schema: " + e.getMessage(), e);
        }
    }
    
    private boolean columnExists(String table, String column) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class, table, column);
            
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Error checking if column exists: " + e.getMessage(), e);
            return false;
        }
    }
}

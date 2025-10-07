# Database Migration Guide: H2 to SQLite

## Overview
This project has been successfully migrated from H2 in-memory database to SQLite file-based database for better portability and data persistence.

## Changes Made

### 1. Dependencies Updated (pom.xml)
**Removed:**
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Added:**
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.43.0.0</version>
</dependency>

<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
</dependency>
```

### 2. Application Configuration (application.properties)
**Before (H2):**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**After (SQLite):**
```properties
spring.datasource.url=jdbc:sqlite:mymovie.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.datasource.username=
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

### 3. .gitignore Updated
Added SQLite database files to .gitignore:
```
### SQLite Database ###
*.db
*.db-shm
*.db-wal
mymovie.db
```

## Benefits of SQLite

### ✅ Portability
- Single file database (`mymovie.db`)
- Easy to backup, copy, or share
- No separate database server required

### ✅ Data Persistence
- Data survives application restarts
- Unlike H2 in-memory mode where data is lost on shutdown

### ✅ Zero Configuration
- No installation required
- No server to start or manage
- Perfect for development and demos

### ✅ Easy Backup & Restore
```powershell
# Backup
copy mymovie.db mymovie_backup_2025-10-07.db

# Restore
copy mymovie_backup_2025-10-07.db mymovie.db
```

## Important Notes

### Database Location
The SQLite database file `mymovie.db` is created in the project root directory when you first run the application.

### DDL Mode Changed
- **H2 Mode**: `create-drop` (recreates database on each startup)
- **SQLite Mode**: `update` (preserves data, updates schema only)

### First Run
On first run, the `DataInitializer` will seed the database with:
- Demo users (admin and customers)
- 24 movies with poster image URLs
- 2 theatres
- 3 screens
- Sample shows
- Sample seats

**Note:** The seeding only happens if tables are empty, so data persists across restarts.

### Resetting Database
If you want to reset to initial state:
1. Stop the application
2. Delete `mymovie.db` file
3. Restart the application

## Viewing the Database

### Recommended Tools

#### 1. DB Browser for SQLite (Free)
- Download: https://sqlitebrowser.org/
- Features: Visual table editor, SQL query tool, CSV export
- **How to use:**
  1. Open DB Browser
  2. File → Open Database
  3. Select `mymovie.db`

#### 2. DBeaver (Free, Universal)
- Download: https://dbeaver.io/
- Supports SQLite and many other databases
- **How to use:**
  1. Create New Connection → SQLite
  2. Browse to `mymovie.db`

#### 3. IntelliJ IDEA Database Tool
- Already available in IntelliJ Ultimate
- **How to use:**
  1. Database panel → + → Data Source → SQLite
  2. Point to `mymovie.db`

#### 4. VS Code Extension
- Install "SQLite" extension by alexcvzz
- Right-click `mymovie.db` → Open Database

### Command Line
```bash
# Open SQLite shell
sqlite3 mymovie.db

# Inside SQLite shell
.tables                 # List all tables
.schema movies          # Show table structure
SELECT * FROM movies;   # Query data
.exit                   # Exit
```

## Troubleshooting

### Issue: "Database is locked"
**Cause:** Another process has the database open
**Solution:** Close all database viewers/tools and try again

### Issue: IDE shows errors in application.properties
**Cause:** IntelliJ hasn't indexed new dependencies yet
**Solution:** 
1. File → Invalidate Caches
2. Or restart IntelliJ
3. Or run `mvn clean install` to ensure dependencies are downloaded

### Issue: Data not persisting
**Check:**
1. Verify `spring.jpa.hibernate.ddl-auto=update` (not `create-drop`)
2. Check if `mymovie.db` file exists in project root
3. Ensure application has write permissions to project directory

## Migration Checklist

- [x] Updated pom.xml with SQLite dependencies
- [x] Updated application.properties configuration
- [x] Added database files to .gitignore
- [x] Updated README.md with SQLite information
- [x] Changed DDL mode from create-drop to update
- [x] Tested build with `mvn clean install`
- [ ] Test application startup
- [ ] Verify data persistence after restart
- [ ] Test all CRUD operations

## Next Steps

1. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Verify database creation:**
   - Check that `mymovie.db` file appears in project root

3. **Test the application:**
   - Login as admin: admin@mymovie.com / admin123
   - Add/edit movies with image URLs
   - Create bookings

4. **Restart and verify:**
   - Stop the application
   - Start again
   - Verify all data is still present

## Support

If you encounter any issues:
1. Check this guide's troubleshooting section
2. Verify all configuration files match the examples above
3. Ensure dependencies are downloaded: `mvn clean install`
4. Check application logs for specific error messages

---

**Migration completed on:** October 7, 2025
**Database:** SQLite 3.43.0.0
**Hibernate Dialect:** SQLiteDialect (Community Edition)


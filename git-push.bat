@echo off
echo ======================================
echo Git Push Script - MyMovie Project
echo ======================================
echo.

cd /d "%~dp0"

echo Checking Git status...
git status
echo.

echo Adding all changes to staging...
git add -A
echo.

echo Committing changes...
git commit -m "Migrate from H2 to SQLite database and add movie image URL support" -m "Changes:" -m "- Replaced H2 in-memory database with SQLite file-based database" -m "- Added imageUrl field to Movie entity for easy poster management" -m "- Updated pom.xml with SQLite dependencies" -m "- Updated application.properties for SQLite configuration" -m "- Fixed View Shows button in Theatres page to use React Router navigation" -m "- Updated README.md with SQLite information and benefits" -m "- Added DATABASE_MIGRATION_GUIDE.md with detailed migration documentation" -m "- Updated .gitignore to exclude SQLite database files"
echo.

echo Checking remote repository...
git remote -v
echo.

echo Pushing to remote repository...
git push
echo.

echo ======================================
echo Git push completed!
echo ======================================
pause


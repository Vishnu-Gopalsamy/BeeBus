@echo off
echo ====================================
echo   Bus Booking API Server
echo ====================================
echo.
echo Server will start on: http://localhost:3000
echo.
echo Make sure MongoDB Atlas connection is configured in backend\.env
echo.
echo IMPORTANT: After server starts, run setup_adb_reverse.bat
echo to connect your Android device!
echo.
cd /d "%~dp0backend"
npm start
pause


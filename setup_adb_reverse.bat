@echo off
echo ====================================
echo   Setting up ADB Port Forwarding
echo ====================================
echo.

set ADB_PATH=C:\Users\vishn\AppData\Local\Android\Sdk\platform-tools\adb.exe

echo Checking connected devices...
"%ADB_PATH%" devices

echo.
echo Setting up port forwarding (tcp:3000)...
"%ADB_PATH%" reverse tcp:3000 tcp:3000

echo.
echo Port forwarding set up successfully!
echo Your phone's localhost:3000 now connects to your computer's localhost:3000
echo.
pause


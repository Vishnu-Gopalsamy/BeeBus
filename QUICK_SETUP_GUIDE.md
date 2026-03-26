# Bus Booking Application - Complete Setup Guide

## 🚀 Quick Start

### Step 1: Start Backend Server
```bash
cd "D:\college\sem 6\flyer bus\Beebus\backend"
node server.js
```
You should see: `🚀 Bus Booking API Server Started`

### Step 2: Setup ADB Port Forwarding
Double-click: `setup_adb_reverse.bat`

Or run manually:
```bash
C:\Users\vishn\AppData\Local\Android\Sdk\platform-tools\adb.exe reverse tcp:3000 tcp:3000
```

### Step 3: Run the App
Open Android Studio → Click Run (or Shift+F10)

---

## 📱 Test Credentials

| Email | Password |
|-------|----------|
| admin@busbooking.com | admin123 |
| john@example.com | password123 |

---

## 🗺️ Sample Routes (for testing search)

| From | To |
|------|-----|
| Chennai | Coimbatore |
| Chennai | Bangalore |
| Chennai | Madurai |
| Bangalore | Coimbatore |
| Coimbatore | Kochi |

---

## 📋 App Features

1. **Splash Screen** - App launch with logo
2. **Login/Register** - User authentication
3. **Home Screen** - Popular routes, search card
4. **Search** - Find buses by route and date
5. **Search Results** - List of available buses
6. **Bus Details** - Bus info, amenities
7. **Seat Selection** - Interactive seat map
8. **Passenger Details** - Enter traveler info
9. **Payment** - Mock payment processing
10. **Booking Confirmation** - Success screen
11. **Profile** - User info, booking history

---

## 🔧 Troubleshooting

### App shows "null" or no data
1. Check backend is running: http://localhost:3000
2. Re-run ADB reverse: `adb reverse tcp:3000 tcp:3000`
3. Check Logcat for errors: filter by `BusViewModel`

### Connection Timeout
1. Make sure backend server is running
2. Run `setup_adb_reverse.bat` after connecting phone
3. USB debugging must be enabled on phone

### Build Errors
```bash
cd "D:\college\sem 6\flyer bus\Beebus"
.\gradlew clean
.\gradlew assembleDebug
```

---

## 📁 Project Structure

```
Beebus/
├── app/                          # Android App
│   └── src/main/
│       ├── java/.../activities/  # All Activity classes
│       ├── java/.../adapters/    # RecyclerView adapters
│       ├── java/.../models/      # Data models
│       ├── java/.../api/         # Retrofit API client
│       ├── java/.../viewmodel/   # ViewModels
│       └── res/layout/           # XML layouts
├── backend/                      # Node.js Backend
│   ├── controllers/              # API logic
│   ├── models/                   # MongoDB models
│   ├── routes/                   # Express routes
│   └── server.js                 # Entry point
├── setup_adb_reverse.bat         # ADB helper script
├── start_backend.bat             # Backend starter
└── QUICK_SETUP_GUIDE.md          # This file
```

---

## 🌐 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/login | User login |
| POST | /api/auth/register | User registration |
| GET | /api/buses/popular-routes | Get popular routes |
| GET | /api/buses/search | Search buses |
| POST | /api/bookings | Create booking |
| POST | /api/payments | Process payment |

---

## ✅ Current Configuration

- **Backend Port**: 3000
- **API Base URL**: http://127.0.0.1:3000/api/
- **Database**: MongoDB Atlas (connected)
- **Connection Method**: ADB Reverse Port Forwarding

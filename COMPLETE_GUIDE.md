# Bus Booking Application - Complete Guide

## ✅ BUILD SUCCESSFUL

The application is complete with both Admin and User functionality.

---

## 🚀 How to Run

### Step 1: Start Backend Server
```powershell
cd "D:\college\sem 6\flyer bus\Beebus\backend"
npm start
```
Server runs on: http://localhost:3000

### Step 2: Setup ADB Reverse (for physical device)
```powershell
adb reverse tcp:3000 tcp:3000
```

### Step 3: Run App
Open Android Studio → Press Shift+F10 (Run)

---

## 👤 User Flow

### Login
- **Email:** admin@busbooking.com
- **Password:** admin123

### Booking Flow
1. **Home Screen** → Tap search card or popular route
2. **Search Screen** → Enter From, To, Date → Search
3. **Results Screen** → Select a bus
4. **Bus Details** → View amenities → Select Seats
5. **Seat Selection** → Choose seats → Proceed
6. **Passenger Details** → Enter passenger info → Proceed to Payment
7. **Payment Screen** → Select payment method → Pay Now
8. **Confirmation** → View Ticket or Go Home
9. **Ticket Screen** → View digital ticket

---

## 🔧 Admin Flow

### Access Admin Dashboard
- From Home Screen → Tap **gear icon** (top-left)

### Admin Features

#### 1. Add New Bus
- Bus Name, Number, Operator
- Bus Type (AC Seater, Sleeper, etc.)
- Total Seats
- Amenities (WiFi, AC, Charging, etc.)

#### 2. Add New Route
- Source City
- Destination City  
- Distance (km)
- Duration (hours)

#### 3. Add Schedule
- Select Bus
- Select Route
- Travel Date
- Departure Time
- Arrival Time
- Ticket Price
- Boarding Points
- Dropping Points

#### 4. View All Bookings
- See all customer bookings
- Booking ID, User, Route, Date, Amount, Status

---

## 📁 Project Structure

```
Beebus/
├── app/src/main/java/com/busbooking/app/
│   ├── activities/           # 18 Activities
│   │   ├── SplashActivity.java
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   ├── HomeActivity.java
│   │   ├── SearchActivity.java
│   │   ├── SearchResultsActivity.java
│   │   ├── BusDetailsActivity.java
│   │   ├── SeatSelectionActivity.java
│   │   ├── PassengerDetailsActivity.java
│   │   ├── PaymentActivity.java
│   │   ├── BookingConfirmationActivity.java
│   │   ├── TicketActivity.java
│   │   ├── ProfileActivity.java
│   │   ├── BookingHistoryActivity.java
│   │   ├── AdminDashboardActivity.java    # NEW
│   │   ├── AddBusActivity.java            # NEW
│   │   ├── AddRouteActivity.java          # NEW
│   │   ├── AddScheduleActivity.java       # NEW
│   │   └── AdminBookingsActivity.java     # NEW
│   │
│   ├── adapters/             # 6 Adapters
│   │   ├── BusAdapter.java
│   │   ├── SeatAdapter.java
│   │   ├── PassengerAdapter.java
│   │   ├── PopularRoutesAdapter.java
│   │   ├── BookingHistoryAdapter.java
│   │   └── AdminBookingAdapter.java       # NEW
│   │
│   ├── viewmodel/            # 4 ViewModels
│   │   ├── AuthViewModel.java
│   │   ├── BusViewModel.java
│   │   ├── BookingViewModel.java
│   │   └── AdminViewModel.java            # NEW
│   │
│   ├── api/                  # API Layer
│   │   ├── ApiClient.java
│   │   └── ApiService.java
│   │
│   └── models/               # Data Models
│       ├── Bus.java
│       ├── Seat.java
│       ├── Passenger.java
│       ├── Booking.java
│       └── api/              # API Response Models
│
├── app/src/main/res/layout/  # 25 XML Layouts
│
└── backend/                  # Node.js Backend
    ├── server.js
    ├── config/db.js
    ├── controllers/
    │   ├── authController.js
    │   ├── busController.js
    │   ├── bookingController.js
    │   ├── paymentController.js
    │   └── routeController.js     # NEW
    ├── routes/
    │   ├── authRoutes.js
    │   ├── busRoutes.js
    │   ├── bookingRoutes.js
    │   ├── paymentRoutes.js
    │   └── routeRoutes.js         # NEW
    └── models/
```

---

## 🔌 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | User login |
| GET | /api/auth/profile | Get profile |

### Buses
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/buses | Get all buses |
| GET | /api/buses/search | Search buses |
| GET | /api/buses/popular-routes | Popular routes |
| POST | /api/buses | Add bus (Admin) |
| POST | /api/buses/schedules | Add schedule (Admin) |

### Routes
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/routes | Get all routes |
| POST | /api/routes | Add route (Admin) |

### Bookings
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/bookings | Create booking |
| GET | /api/bookings | Get all bookings (Admin) |
| GET | /api/bookings/user | Get user bookings |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/admin/dashboard | Get dashboard stats |

---

## 🎨 UI Theme

- **Primary Color:** #D32F2F (Red)
- **Accent Color:** #FF5722 (Orange)
- **Background:** White/Light Gray
- **Success:** #4CAF50 (Green)
- **Warning:** #FF9800 (Orange)

---

## ✅ Features Complete

### User Features
- [x] Splash Screen
- [x] Login / Register
- [x] Home with Popular Routes
- [x] Bus Search
- [x] Search Results
- [x] Bus Details
- [x] Seat Selection
- [x] Passenger Details
- [x] Payment Screen
- [x] Booking Confirmation
- [x] E-Ticket
- [x] Profile
- [x] Booking History
- [x] Logout

### Admin Features
- [x] Admin Dashboard
- [x] Add New Bus
- [x] Add New Route
- [x] Add Schedule
- [x] View All Bookings
- [x] Dashboard Stats

---

## 🧪 Test Data

### Login Credentials
```
Email: admin@busbooking.com
Password: admin123
```

### Sample Routes
- Chennai → Coimbatore
- Bangalore → Chennai
- Madurai → Trichy
- Mumbai → Pune

---

## 📱 APK Location
```
D:\college\sem 6\flyer bus\Beebus\app\build\outputs\apk\debug\app-debug.apk
```

---

## 🎉 Application Complete!

Both Admin and User flows are fully functional.


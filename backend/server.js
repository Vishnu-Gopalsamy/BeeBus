const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
require('dotenv').config();

// Import database connection
const dbConnection = require('./config/db');

// Import routes
const authRoutes = require('./routes/authRoutes');
const busRoutes = require('./routes/busRoutes');
const bookingRoutes = require('./routes/bookingRoutes');
const paymentRoutes = require('./routes/paymentRoutes');
const routeRoutes = require('./routes/routeRoutes');

// Initialize Express app
const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors({
    origin: process.env.CORS_ORIGIN || '*',
    credentials: true
}));

app.use(bodyParser.json({ limit: '10mb' }));
app.use(bodyParser.urlencoded({ extended: true, limit: '10mb' }));

// Request logging middleware
app.use((req, res, next) => {
    console.log(`${new Date().toISOString()} - ${req.method} ${req.path}`);
    next();
});

// Health check endpoint
app.get('/health', async (req, res) => {
    try {
        const dbHealth = await dbConnection.ping();
        res.status(200).json({
            status: 'healthy',
            timestamp: new Date().toISOString(),
            version: '1.0.0',
            database: dbHealth,
            uptime: process.uptime()
        });
    } catch (error) {
        res.status(500).json({
            status: 'unhealthy',
            timestamp: new Date().toISOString(),
            error: error.message
        });
    }
});

// API Routes
app.use('/api/auth', authRoutes);
app.use('/api/buses', busRoutes);
app.use('/api/bookings', bookingRoutes);
app.use('/api/payments', paymentRoutes);
app.use('/api/routes', routeRoutes);

// Admin Dashboard Stats
const { authenticateAdmin } = require('./middleware/authMiddleware');
app.get('/api/admin/dashboard', authenticateAdmin, async (req, res) => {
    try {
        const db = dbConnection.getDb();
        const totalBuses = await db.collection('buses').countDocuments({ isActive: true });
        const totalRoutes = await db.collection('routes').countDocuments({ isActive: true });
        const totalBookings = await db.collection('bookings').countDocuments();
        const totalUsers = await db.collection('users').countDocuments({ isActive: true });

        res.status(200).json({
            success: true,
            message: 'Dashboard stats retrieved',
            data: {
                totalBuses,
                totalRoutes,
                totalBookings,
                totalUsers
            }
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to get dashboard stats',
            error: error.message
        });
    }
});

// Root endpoint
app.get('/', (req, res) => {
    res.json({
        message: 'BeeBus API Server',
        version: '1.0.0',
        status: 'running',
        documentation: '/api/docs',
        endpoints: {
            auth: '/api/auth',
            buses: '/api/buses',
            bookings: '/api/bookings',
            payments: '/api/payments',
            health: '/health'
        }
    });
});

// API documentation endpoint (simple)
app.get('/api/docs', (req, res) => {
    res.json({
        title: 'BeeBus API Documentation',
        version: '1.0.0',
        baseUrl: `http://localhost:${PORT}/api`,
        endpoints: {
            authentication: {
                'POST /auth/register': 'Register new user',
                'POST /auth/login': 'Login user',
                'GET /auth/profile': 'Get user profile (requires auth)',
                'PUT /auth/profile': 'Update user profile (requires auth)',
                'POST /auth/change-password': 'Change password (requires auth)',
                'POST /auth/logout': 'Logout user (requires auth)'
            },
            buses: {
                'GET /buses': 'Get all buses',
                'GET /buses/search?source=&destination=&date=': 'Search buses by route and date',
                'GET /buses/popular-routes': 'Get popular routes',
                'GET /buses/:id': 'Get bus by ID',
                'GET /buses/:scheduleId/seats': 'Get seats for schedule (requires auth)',
                'POST /buses/:scheduleId/block-seats': 'Block seats temporarily (requires auth)',
                'POST /buses': 'Create bus (admin only)',
                'POST /buses/schedules': 'Create schedule (admin only)'
            },
            bookings: {
                'POST /bookings': 'Create new booking (requires auth)',
                'GET /bookings/user/:userId': 'Get user bookings (requires auth)',
                'GET /bookings/:bookingId': 'Get booking by ID (requires auth)',
                'GET /bookings/:bookingId/details': 'Get detailed booking info (requires auth)',
                'GET /bookings/pnr/:pnr': 'Get booking by PNR (requires auth)',
                'POST /bookings/:bookingId/cancel': 'Cancel booking (requires auth)',
                'PUT /bookings/:bookingId/status': 'Update booking status (admin only)',
                'GET /bookings/admin/stats': 'Get booking statistics (admin only)'
            },
            payments: {
                'POST /payments': 'Process payment (requires auth)',
                'GET /payments/:transactionId': 'Get payment status (requires auth)',
                'GET /payments/user/history': 'Get user payment history (requires auth)',
                'POST /payments/:transactionId/refund': 'Process refund (requires auth)'
            }
        },
        authentication: {
            type: 'Bearer Token',
            header: 'Authorization: Bearer <token>',
            note: 'Include JWT token in Authorization header for protected routes'
        }
    });
});

// 404 handler for undefined routes
app.use('*', (req, res) => {
    res.status(404).json({
        success: false,
        message: 'Route not found',
        path: req.originalUrl,
        method: req.method,
        timestamp: new Date().toISOString()
    });
});

// Global error handler
app.use((error, req, res, next) => {
    console.error('Global error handler:', error);

    res.status(error.status || 500).json({
        success: false,
        message: error.message || 'Internal server error',
        ...(process.env.NODE_ENV === 'development' && { stack: error.stack })
    });
});

// Graceful shutdown handler
process.on('SIGINT', async () => {
    console.log('\n🛑 Received SIGINT. Graceful shutdown...');

    try {
        await dbConnection.disconnect();
        console.log('✅ Database connection closed.');
        process.exit(0);
    } catch (error) {
        console.error('❌ Error during shutdown:', error);
        process.exit(1);
    }
});

// Start server
const startServer = async () => {
    try {
        // Connect to database first
        await dbConnection.connect();

        // Start HTTP server - bind to 0.0.0.0 to accept connections from any IP
        const HOST = '0.0.0.0';
        app.listen(PORT, HOST, () => {
            console.log('🚀 BeeBus API Server Started');
            console.log(`📡 Server running on http://${HOST}:${PORT}`);
            console.log(`📱 For mobile devices, use your computer's WiFi IP: http://10.85.201.158:${PORT}`);
            console.log(`🏥 Health check: http://localhost:${PORT}/health`);
            console.log(`📚 API docs: http://localhost:${PORT}/api/docs`);
            console.log(`🌍 Environment: ${process.env.NODE_ENV || 'development'}`);
            console.log('🔗 Ready to accept connections from any device on the network...\n');
        });

    } catch (error) {
        console.error('❌ Failed to start server:', error);
        process.exit(1);
    }
};

// Start the server
startServer();

module.exports = app;

const { MongoClient } = require('mongodb');
require('dotenv').config();

class DatabaseConnection {
    constructor() {
        this.client = null;
        this.db = null;
        this.isConnected = false;
        this.maxRetries = 3;
        this.retryDelayMs = 2000; // start with 2s, doubles each retry
    }

    async connect() {
        if (this.isConnected && this.db) {
            return this.db;
        }

        let lastError;
        for (let attempt = 1; attempt <= this.maxRetries; attempt++) {
            try {
                console.log(`🔄 Connecting to MongoDB Atlas (attempt ${attempt}/${this.maxRetries})...`);

                this.client = new MongoClient(process.env.MONGODB_URI, {
                    serverSelectionTimeoutMS: 10000,
                    heartbeatFrequencyMS: 15000,
                });

                await this.client.connect();

                // Use 'beebus' to match the database name in the Atlas URI
                this.db = this.client.db('beebus');
                this.isConnected = true;

                console.log('✅ Successfully connected to MongoDB Atlas');

                // Monitor connection health
                this._setupMonitoring();

                // Create indexes for better performance
                await this.createIndexes();

                return this.db;
            } catch (error) {
                lastError = error;
                console.error(`❌ MongoDB connection attempt ${attempt} failed:`, error.message);

                // Clean up failed client
                if (this.client) {
                    try { await this.client.close(); } catch (_) {}
                    this.client = null;
                }

                if (attempt < this.maxRetries) {
                    const delay = this.retryDelayMs * Math.pow(2, attempt - 1);
                    console.log(`⏳ Retrying in ${delay / 1000}s...`);
                    await new Promise(resolve => setTimeout(resolve, delay));
                }
            }
        }

        // All retries exhausted — log but do NOT process.exit so the server
        // can still serve cached/static responses or accept a later reconnect.
        console.error('❌ All MongoDB connection attempts failed:', lastError.message);
        throw lastError;
    }

    _setupMonitoring() {
        if (!this.client) return;

        this.client.on('serverDescriptionChanged', (event) => {
            const newDesc = event.newDescription;
            if (newDesc.type === 'Unknown') {
                console.warn('⚠️  MongoDB connection lost. Will auto-reconnect on next request.');
                this.isConnected = false;
            }
        });

        this.client.on('close', () => {
            console.warn('⚠️  MongoDB client closed.');
            this.isConnected = false;
        });
    }

    async createIndexes() {
        try {
            // Index for users collection
            await this.db.collection('users').createIndex({ email: 1 }, { unique: true });

            // Index for schedules collection
            await this.db.collection('schedules').createIndex({
                'route.source': 1,
                'route.destination': 1,
                travelDate: 1
            });

            // Index for bookings collection
            await this.db.collection('bookings').createIndex({ userId: 1 });
            await this.db.collection('bookings').createIndex({ bookingId: 1 }, { unique: true });

            // Index for seats collection
            await this.db.collection('seats').createIndex({ scheduleId: 1, seatNumber: 1 });

            console.log('📄 Database indexes created successfully');
        } catch (error) {
            console.log('⚠️  Index creation warning:', error.message);
        }
    }

    getDb() {
        if (!this.isConnected || !this.db) {
            throw new Error('Database not connected. Call connect() first.');
        }
        return this.db;
    }

    /**
     * Safe getter that attempts reconnect if the connection was dropped.
     * Use this in request handlers for resilient DB access.
     */
    async getDbSafe() {
        if (!this.isConnected || !this.db) {
            console.log('🔄 DB not connected, attempting reconnect...');
            await this.connect();
        }
        return this.db;
    }

    async disconnect() {
        try {
            if (this.client) {
                await this.client.close();
                this.isConnected = false;
                console.log('🔌 Disconnected from MongoDB');
            }
        } catch (error) {
            console.error('❌ Error disconnecting from MongoDB:', error);
        }
    }

    // Health check method
    async ping() {
        try {
            if (!this.client || !this.isConnected) {
                return { status: 'unhealthy', message: 'Not connected to database' };
            }
            await this.client.db().admin().ping();
            return { status: 'healthy', message: 'Database connection is active' };
        } catch (error) {
            this.isConnected = false;
            return { status: 'unhealthy', message: error.message };
        }
    }
}

// Create a singleton instance
const dbConnection = new DatabaseConnection();

module.exports = dbConnection;

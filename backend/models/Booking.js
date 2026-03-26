const { ObjectId } = require('mongodb');
const { v4: uuidv4 } = require('uuid');
const dbConnection = require('../config/db');

class Booking {
    constructor(bookingData) {
        this.bookingId = uuidv4().toUpperCase().substring(0, 8); // Generate unique booking ID
        this.userId = new ObjectId(bookingData.userId);
        this.scheduleId = new ObjectId(bookingData.scheduleId);
        this.seats = bookingData.seats; // Array of seat numbers
        this.passengers = bookingData.passengers; // Array of passenger details
        this.totalAmount = bookingData.totalAmount;
        this.bookingStatus = 'confirmed'; // confirmed, cancelled, completed
        this.paymentStatus = 'pending'; // pending, completed, failed, refunded
        this.pnr = this.generatePNR();
        this.createdAt = new Date();
        this.updatedAt = new Date();

        // Additional booking details
        this.contactDetails = {
            email: bookingData.contactEmail,
            phone: bookingData.contactPhone
        };
        this.ticketDetails = {
            source: bookingData.source,
            destination: bookingData.destination,
            travelDate: bookingData.travelDate,
            departureTime: bookingData.departureTime,
            arrivalTime: bookingData.arrivalTime,
            busName: bookingData.busName,
            busType: bookingData.busType
        };
    }

    // Generate PNR number
    generatePNR() {
        return 'PNR' + Date.now().toString().slice(-6) + Math.random().toString(36).substring(2, 5).toUpperCase();
    }

    // Create new booking
    static async create(bookingData) {
        try {
            const db = dbConnection.getDb();
            const booking = new Booking(bookingData);
            const result = await db.collection('bookings').insertOne(booking);
            return { ...booking, _id: result.insertedId };
        } catch (error) {
            throw new Error(`Error creating booking: ${error.message}`);
        }
    }

    // Get booking by ID
    static async findById(bookingId) {
        try {
            const db = dbConnection.getDb();
            const booking = await db.collection('bookings').findOne({
                bookingId: bookingId
            });
            return booking;
        } catch (error) {
            throw new Error(`Error finding booking: ${error.message}`);
        }
    }

    // Get all bookings (Admin only)
    static async findAll() {
        try {
            const db = dbConnection.getDb();
            const pipeline = [
                {
                    $lookup: {
                        from: 'users',
                        localField: 'userId',
                        foreignField: '_id',
                        as: 'user'
                    }
                },
                { $unwind: '$user' },
                {
                    $lookup: {
                        from: 'schedules',
                        localField: 'scheduleId',
                        foreignField: '_id',
                        as: 'schedule'
                    }
                },
                { $unwind: '$schedule' },
                {
                    $project: {
                        'user.password': 0,
                        'user.createdAt': 0,
                        'user.updatedAt': 0
                    }
                },
                { $sort: { createdAt: -1 } }
            ];
            return await db.collection('bookings').aggregate(pipeline).toArray();
        } catch (error) {
            throw new Error(`Error fetching all bookings: ${error.message}`);
        }
    }

    // Get all bookings for buses owned by a specific owner
    static async findByOwnerId(ownerId) {
        try {
            const db = dbConnection.getDb();
            const pipeline = [
                {
                    $lookup: {
                        from: 'schedules',
                        localField: 'scheduleId',
                        foreignField: '_id',
                        as: 'schedule'
                    }
                },
                { $unwind: '$schedule' },
                {
                    $lookup: {
                        from: 'buses',
                        localField: 'schedule.busId',
                        foreignField: '_id',
                        as: 'bus'
                    }
                },
                { $unwind: '$bus' },
                // Filter where the bus ownerId matches the requesting owner
                { $match: { 'bus.ownerId': new ObjectId(ownerId) } },
                {
                    $lookup: {
                        from: 'users',
                        localField: 'userId',
                        foreignField: '_id',
                        as: 'user'
                    }
                },
                { $unwind: '$user' },
                {
                    $project: {
                        'user.password': 0,
                        'user.createdAt': 0,
                        'user.updatedAt': 0
                    }
                },
                { $sort: { createdAt: -1 } }
            ];
            return await db.collection('bookings').aggregate(pipeline).toArray();
        } catch (error) {
            throw new Error(`Error fetching owner bookings: ${error.message}`);
        }
    }

    // Get bookings by user ID
    static async findByUserId(userId) {
        try {
            const db = dbConnection.getDb();
            const bookings = await db.collection('bookings')
                .find({ userId: new ObjectId(userId) })
                .sort({ createdAt: -1 })
                .toArray();
            return bookings;
        } catch (error) {
            throw new Error(`Error fetching user bookings: ${error.message}`);
        }
    }

    // Get booking by PNR
    static async findByPNR(pnr) {
        try {
            const db = dbConnection.getDb();
            const booking = await db.collection('bookings').findOne({
                pnr: pnr
            });
            return booking;
        } catch (error) {
            throw new Error(`Error finding booking by PNR: ${error.message}`);
        }
    }

    // Update booking status
    static async updateStatus(bookingId, status) {
        try {
            const db = dbConnection.getDb();
            const result = await db.collection('bookings').updateOne(
                { bookingId: bookingId },
                {
                    $set: {
                        bookingStatus: status,
                        updatedAt: new Date()
                    }
                }
            );
            return result;
        } catch (error) {
            throw new Error(`Error updating booking status: ${error.message}`);
        }
    }

    // Update payment status
    static async updatePaymentStatus(bookingId, paymentStatus, transactionId = null) {
        try {
            const db = dbConnection.getDb();
            const updateData = {
                paymentStatus: paymentStatus,
                updatedAt: new Date()
            };

            if (transactionId) {
                updateData.transactionId = transactionId;
            }

            const result = await db.collection('bookings').updateOne(
                { bookingId: bookingId },
                { $set: updateData }
            );
            return result;
        } catch (error) {
            throw new Error(`Error updating payment status: ${error.message}`);
        }
    }

    // Get booking with schedule and bus details
    static async getBookingDetails(bookingId) {
        try {
            const db = dbConnection.getDb();
            const pipeline = [
                {
                    $match: { bookingId: bookingId }
                },
                {
                    $lookup: {
                        from: 'schedules',
                        localField: 'scheduleId',
                        foreignField: '_id',
                        as: 'schedule'
                    }
                },
                {
                    $unwind: '$schedule'
                },
                {
                    $lookup: {
                        from: 'buses',
                        localField: 'schedule.busId',
                        foreignField: '_id',
                        as: 'bus'
                    }
                },
                {
                    $unwind: '$bus'
                },
                {
                    $lookup: {
                        from: 'users',
                        localField: 'userId',
                        foreignField: '_id',
                        as: 'user'
                    }
                },
                {
                    $unwind: '$user'
                },
                {
                    $project: {
                        'user.password': 0 // Exclude password from user details
                    }
                }
            ];

            const bookings = await db.collection('bookings').aggregate(pipeline).toArray();
            return bookings.length > 0 ? bookings[0] : null;
        } catch (error) {
            throw new Error(`Error getting booking details: ${error.message}`);
        }
    }

    // Cancel booking
    static async cancel(bookingId, reason = '') {
        try {
            const db = dbConnection.getDb();

            // Get booking details first
            const booking = await this.findById(bookingId);
            if (!booking) {
                throw new Error('Booking not found');
            }

            // Update booking status
            const result = await db.collection('bookings').updateOne(
                { bookingId: bookingId },
                {
                    $set: {
                        bookingStatus: 'cancelled',
                        cancellationReason: reason,
                        cancelledAt: new Date(),
                        updatedAt: new Date()
                    }
                }
            );

            return result;
        } catch (error) {
            throw new Error(`Error cancelling booking: ${error.message}`);
        }
    }

    // Get booking statistics
    static async getBookingStats(userId = null) {
        try {
            const db = dbConnection.getDb();
            const matchQuery = userId ? { userId: new ObjectId(userId) } : {};

            const pipeline = [
                { $match: matchQuery },
                {
                    $group: {
                        _id: '$bookingStatus',
                        count: { $sum: 1 },
                        totalAmount: { $sum: '$totalAmount' }
                    }
                }
            ];

            const stats = await db.collection('bookings').aggregate(pipeline).toArray();
            return stats;
        } catch (error) {
            throw new Error(`Error getting booking stats: ${error.message}`);
        }
    }

    // Validate booking data
    static validate(bookingData) {
        const errors = [];

        if (!bookingData.userId) {
            errors.push('User ID is required');
        }

        if (!bookingData.scheduleId) {
            errors.push('Schedule ID is required');
        }

        if (!bookingData.seats || !Array.isArray(bookingData.seats) || bookingData.seats.length === 0) {
            errors.push('At least one seat must be selected');
        }

        if (!bookingData.passengers || !Array.isArray(bookingData.passengers) || bookingData.passengers.length === 0) {
            errors.push('At least one passenger detail is required');
        }

        if (bookingData.seats && bookingData.passengers && bookingData.seats.length !== bookingData.passengers.length) {
            errors.push('Number of seats and passengers must match');
        }

        if (!bookingData.totalAmount || bookingData.totalAmount <= 0) {
            errors.push('Valid total amount is required');
        }

        // Validate passenger details
        if (bookingData.passengers && Array.isArray(bookingData.passengers)) {
            bookingData.passengers.forEach((passenger, index) => {
                if (!passenger.name || passenger.name.trim().length < 2) {
                    errors.push(`Passenger ${index + 1}: Name is required`);
                }
                if (!passenger.age || passenger.age < 1 || passenger.age > 100) {
                    errors.push(`Passenger ${index + 1}: Valid age is required`);
                }
                if (!passenger.gender || !['male', 'female', 'other'].includes(passenger.gender.toLowerCase())) {
                    errors.push(`Passenger ${index + 1}: Valid gender is required`);
                }
            });
        }

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    }
}

module.exports = Booking;

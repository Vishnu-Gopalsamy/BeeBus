const { ObjectId } = require('mongodb');
const dbConnection = require('../config/db');

class Seat {
    constructor(seatData) {
        this.scheduleId = new ObjectId(seatData.scheduleId);
        this.seatNumber = seatData.seatNumber;
        this.seatType = seatData.seatType; // 'regular', 'ladies', 'premium'
        this.status = seatData.status || 'available'; // 'available', 'booked', 'blocked'
        this.bookedBy = seatData.bookedBy || null; // userId when booked
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Initialize seats for a schedule
    static async initializeSeats(scheduleId, totalSeats) {
        try {
            const db = dbConnection.getDb();
            const seats = [];

            for (let i = 1; i <= totalSeats; i++) {
                // Determine seat type (simplified logic)
                let seatType = 'regular';
                if (i <= 4) {
                    seatType = 'ladies'; // First 4 seats reserved for ladies
                } else if (i > totalSeats - 4) {
                    seatType = 'premium'; // Last 4 seats as premium
                }

                const seat = new Seat({
                    scheduleId: scheduleId,
                    seatNumber: i,
                    seatType: seatType
                });
                seats.push(seat);
            }

            const result = await db.collection('seats').insertMany(seats);
            return result;
        } catch (error) {
            throw new Error(`Error initializing seats: ${error.message}`);
        }
    }

    // Get seats for a schedule
    static async getByScheduleId(scheduleId) {
        try {
            const db = dbConnection.getDb();
            const seats = await db.collection('seats')
                .find({ scheduleId: new ObjectId(scheduleId) })
                .sort({ seatNumber: 1 })
                .toArray();
            return seats;
        } catch (error) {
            throw new Error(`Error fetching seats: ${error.message}`);
        }
    }

    // Book seats
    static async bookSeats(scheduleId, seatNumbers, userId) {
        try {
            const db = dbConnection.getDb();

            // Check if seats are available
            const existingSeats = await db.collection('seats').find({
                scheduleId: new ObjectId(scheduleId),
                seatNumber: { $in: seatNumbers },
                status: { $ne: 'available' }
            }).toArray();

            if (existingSeats.length > 0) {
                throw new Error('Some seats are already booked or blocked');
            }

            // Book the seats
            const result = await db.collection('seats').updateMany(
                {
                    scheduleId: new ObjectId(scheduleId),
                    seatNumber: { $in: seatNumbers }
                },
                {
                    $set: {
                        status: 'booked',
                        bookedBy: new ObjectId(userId),
                        updatedAt: new Date()
                    }
                }
            );

            return result;
        } catch (error) {
            throw new Error(`Error booking seats: ${error.message}`);
        }
    }

    // Block seats temporarily (during booking process)
    static async blockSeats(scheduleId, seatNumbers, userId, duration = 300000) { // 5 minutes
        try {
            const db = dbConnection.getDb();

            // Check if seats are available
            const existingSeats = await db.collection('seats').find({
                scheduleId: new ObjectId(scheduleId),
                seatNumber: { $in: seatNumbers },
                status: { $ne: 'available' }
            }).toArray();

            if (existingSeats.length > 0) {
                throw new Error('Some seats are not available for blocking');
            }

            const blockUntil = new Date(Date.now() + duration);

            // Block the seats
            const result = await db.collection('seats').updateMany(
                {
                    scheduleId: new ObjectId(scheduleId),
                    seatNumber: { $in: seatNumbers }
                },
                {
                    $set: {
                        status: 'blocked',
                        bookedBy: new ObjectId(userId),
                        blockUntil: blockUntil,
                        updatedAt: new Date()
                    }
                }
            );

            return result;
        } catch (error) {
            throw new Error(`Error blocking seats: ${error.message}`);
        }
    }

    // Release blocked seats
    static async releaseBlockedSeats(scheduleId, seatNumbers) {
        try {
            const db = dbConnection.getDb();

            const result = await db.collection('seats').updateMany(
                {
                    scheduleId: new ObjectId(scheduleId),
                    seatNumber: { $in: seatNumbers },
                    status: 'blocked'
                },
                {
                    $set: {
                        status: 'available',
                        bookedBy: null,
                        blockUntil: null,
                        updatedAt: new Date()
                    }
                }
            );

            return result;
        } catch (error) {
            throw new Error(`Error releasing seats: ${error.message}`);
        }
    }

    // Clean up expired blocked seats (should be run periodically)
    static async cleanupExpiredBlocks() {
        try {
            const db = dbConnection.getDb();

            const result = await db.collection('seats').updateMany(
                {
                    status: 'blocked',
                    blockUntil: { $lt: new Date() }
                },
                {
                    $set: {
                        status: 'available',
                        bookedBy: null,
                        blockUntil: null,
                        updatedAt: new Date()
                    }
                }
            );

            return result;
        } catch (error) {
            throw new Error(`Error cleaning up blocked seats: ${error.message}`);
        }
    }

    // Get available seat count for a schedule
    static async getAvailableCount(scheduleId) {
        try {
            const db = dbConnection.getDb();
            const count = await db.collection('seats').countDocuments({
                scheduleId: new ObjectId(scheduleId),
                status: 'available'
            });
            return count;
        } catch (error) {
            throw new Error(`Error counting available seats: ${error.message}`);
        }
    }
}

module.exports = Seat;

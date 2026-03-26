const { ObjectId } = require('mongodb');
const dbConnection = require('../config/db');

class Schedule {
    constructor(scheduleData) {
        this.busId = new ObjectId(scheduleData.busId);
        this.route = {
            source: scheduleData.source,
            destination: scheduleData.destination,
            distance: scheduleData.distance, // in km
            duration: scheduleData.duration // in hours
        };
        this.travelDate = new Date(scheduleData.travelDate);
        this.departureTime = scheduleData.departureTime; // "06:00 AM"
        this.arrivalTime = scheduleData.arrivalTime; // "12:30 PM"
        this.price = scheduleData.price;
        this.availableSeats = scheduleData.availableSeats;
        this.boardingPoints = scheduleData.boardingPoints || [];
        this.droppingPoints = scheduleData.droppingPoints || [];
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Create new schedule
    static async create(scheduleData) {
        try {
            const db = dbConnection.getDb();
            const schedule = new Schedule(scheduleData);
            const result = await db.collection('schedules').insertOne(schedule);
            return { ...schedule, _id: result.insertedId };
        } catch (error) {
            throw new Error(`Error creating schedule: ${error.message}`);
        }
    }

    // Search schedules
    static async search(source, destination, date) {
        try {
            const db = dbConnection.getDb();

            // Parse the date to search for the entire day
            const searchDate = new Date(date);
            const nextDay = new Date(searchDate);
            nextDay.setDate(nextDay.getDate() + 1);

            const pipeline = [
                {
                    $match: {
                        'route.source': { $regex: source, $options: 'i' },
                        'route.destination': { $regex: destination, $options: 'i' },
                        travelDate: {
                            $gte: searchDate,
                            $lt: nextDay
                        },
                        isActive: true,
                        availableSeats: { $gt: 0 }
                    }
                },
                {
                    $lookup: {
                        from: 'buses',
                        localField: 'busId',
                        foreignField: '_id',
                        as: 'bus'
                    }
                },
                {
                    $unwind: '$bus'
                },
                {
                    $match: {
                        'bus.isActive': true
                    }
                },
                {
                    $project: {
                        _id: 1,
                        route: 1,
                        travelDate: 1,
                        departureTime: 1,
                        arrivalTime: 1,
                        price: 1,
                        availableSeats: 1,
                        boardingPoints: 1,
                        droppingPoints: 1,
                        'bus.busName': 1,
                        'bus.busType': 1,
                        'bus.operatorName': 1,
                        'bus.amenities': 1,
                        'bus.rating': 1,
                        'bus.totalSeats': 1
                    }
                },
                {
                    $sort: { departureTime: 1 }
                }
            ];

            const schedules = await db.collection('schedules').aggregate(pipeline).toArray();
            return schedules;
        } catch (error) {
            throw new Error(`Error searching schedules: ${error.message}`);
        }
    }

    // Get schedule by ID with bus details
    static async findById(id) {
        try {
            const db = dbConnection.getDb();

            // Check if id is a valid ObjectId
            if (!ObjectId.isValid(id)) {
                return null;
            }

            const pipeline = [
                {
                    $match: {
                        _id: new ObjectId(id),
                        isActive: true
                    }
                },
                {
                    $lookup: {
                        from: 'buses',
                        localField: 'busId',
                        foreignField: '_id',
                        as: 'bus'
                    }
                },
                {
                    $unwind: '$bus'
                }
            ];

            const schedules = await db.collection('schedules').aggregate(pipeline).toArray();
            return schedules.length > 0 ? schedules[0] : null;
        } catch (error) {
            throw new Error(`Error finding schedule: ${error.message}`);
        }
    }

    // Update available seats
    static async updateAvailableSeats(scheduleId, seatsToDeduct) {
        try {
            const db = dbConnection.getDb();
            const result = await db.collection('schedules').updateOne(
                { _id: new ObjectId(scheduleId) },
                {
                    $inc: { availableSeats: -seatsToDeduct },
                    $set: { updatedAt: new Date() }
                }
            );
            return result;
        } catch (error) {
            throw new Error(`Error updating seats: ${error.message}`);
        }
    }

    // Get popular routes
    static async getPopularRoutes(limit = 5) {
        try {
            const db = dbConnection.getDb();
            const pipeline = [
                {
                    $match: {
                        isActive: true,
                        travelDate: { $gte: new Date() }
                    }
                },
                {
                    $group: {
                        _id: {
                            source: '$route.source',
                            destination: '$route.destination'
                        },
                        minPrice: { $min: '$price' },
                        count: { $sum: 1 }
                    }
                },
                {
                    $sort: { count: -1 }
                },
                {
                    $limit: limit
                },
                {
                    $project: {
                        route: '$_id',
                        minPrice: 1,
                        count: 1
                    }
                }
            ];

            const routes = await db.collection('schedules').aggregate(pipeline).toArray();
            return routes;
        } catch (error) {
            throw new Error(`Error getting popular routes: ${error.message}`);
        }
    }

    // Find all schedules (Admin)
    static async findAll() {
        try {
            const db = dbConnection.getDb();
            const pipeline = [
                { $match: { isActive: true } },
                {
                    $lookup: {
                        from: 'buses',
                        localField: 'busId',
                        foreignField: '_id',
                        as: 'bus'
                    }
                },
                { $unwind: { path: '$bus', preserveNullAndEmptyArrays: true } },
                { $sort: { travelDate: -1, createdAt: -1 } }
            ];

            const schedules = await db.collection('schedules').aggregate(pipeline).toArray();
            return schedules.map(schedule => ({
                ...schedule,
                scheduleId: schedule._id.toString(),
                source: schedule.route?.source,
                destination: schedule.route?.destination,
                travelDate: schedule.travelDate ? schedule.travelDate.toISOString().split('T')[0] : ''
            }));
        } catch (error) {
            throw new Error(`Error finding all schedules: ${error.message}`);
        }
    }

    // Delete schedule by ID (soft delete)
    static async deleteById(scheduleId) {
        try {
            const db = dbConnection.getDb();
            const result = await db.collection('schedules').updateOne(
                { _id: new ObjectId(scheduleId) },
                { $set: { isActive: false, updatedAt: new Date() } }
            );
            return result.modifiedCount > 0;
        } catch (error) {
            throw new Error(`Error deleting schedule: ${error.message}`);
        }
    }

    // Validate schedule data
    static validate(scheduleData) {
        const errors = [];

        if (!scheduleData.busId) {
            errors.push('Bus ID is required');
        }

        if (!scheduleData.source || scheduleData.source.trim().length < 2) {
            errors.push('Source city is required');
        }

        if (!scheduleData.destination || scheduleData.destination.trim().length < 2) {
            errors.push('Destination city is required');
        }

        if (!scheduleData.travelDate) {
            errors.push('Travel date is required');
        }

        if (!scheduleData.departureTime) {
            errors.push('Departure time is required');
        }

        if (!scheduleData.arrivalTime) {
            errors.push('Arrival time is required');
        }

        if (!scheduleData.price || scheduleData.price <= 0) {
            errors.push('Valid price is required');
        }

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    }
}

module.exports = Schedule;

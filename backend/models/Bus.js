const { ObjectId } = require('mongodb');
const dbConnection = require('../config/db');

class Bus {
    constructor(busData) {
        this.busName = busData.busName;
        this.busType = busData.busType; // AC Seater, AC Sleeper, Non-AC Seater, etc.
        this.totalSeats = busData.totalSeats;
        this.amenities = busData.amenities || []; // WiFi, Charging Point, Water Bottle, etc.
        this.operatorName = busData.operatorName;
        this.busNumber = busData.busNumber;
        this.rating = busData.rating || 4.0;

        // Seat Configuration
        this.seatConfig = busData.seatConfig || {
            layout: 'Seater',
            seaterSeats: busData.totalSeats,
            sleeperLowerSeats: 0,
            sleeperUpperSeats: 0,
            berthType: 'Double Berth'
        };

        this.ownerId = busData.ownerId ? new ObjectId(busData.ownerId) : null;
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Create new bus
    static async create(busData) {
        try {
            const db = dbConnection.getDb();
            const bus = new Bus(busData);
            const result = await db.collection('buses').insertOne(bus);
            return { ...bus, _id: result.insertedId };
        } catch (error) {
            throw new Error(`Error creating bus: ${error.message}`);
        }
    }

    // Get all active buses
    static async findAll() {
        try {
            const db = dbConnection.getDb();
            const buses = await db.collection('buses').find({ isActive: true }).sort({ createdAt: -1 }).toArray();
            return buses;
        } catch (error) {
            throw new Error(`Error fetching buses: ${error.message}`);
        }
    }

    // Find bus by ID
    static async findById(busId) {
        try {
            const db = dbConnection.getDb();
            const bus = await db.collection('buses').findOne({
                _id: new ObjectId(busId),
                isActive: true
            });
            return bus;
        } catch (error) {
            throw new Error(`Error finding bus: ${error.message}`);
        }
    }

    // Find buses by owner ID
    static async findByOwnerId(ownerId) {
        try {
            const db = dbConnection.getDb();
            const buses = await db.collection('buses')
                .find({ ownerId: new ObjectId(ownerId), isActive: true })
                .sort({ createdAt: -1 })
                .toArray();
            return buses;
        } catch (error) {
            throw new Error(`Error finding buses by owner: ${error.message}`);
        }
    }

    // Update bus
    static async updateById(busId, updateData) {
        try {
            const db = dbConnection.getDb();
            updateData.updatedAt = new Date();

            // Re-wrap in Bus constructor logic if needed or just update fields
            const result = await db.collection('buses').updateOne(
                { _id: new ObjectId(busId) },
                { $set: updateData }
            );
            return result;
        } catch (error) {
            throw new Error(`Error updating bus: ${error.message}`);
        }
    }

    // Soft delete bus
    static async deleteById(busId) {
        try {
            const db = dbConnection.getDb();
            const result = await db.collection('buses').updateOne(
                { _id: new ObjectId(busId) },
                { $set: { isActive: false, updatedAt: new Date() } }
            );
            return result;
        } catch (error) {
            throw new Error(`Error deleting bus: ${error.message}`);
        }
    }

    // Search buses by operator or type
    static async search(query) {
        try {
            const db = dbConnection.getDb();
            const searchQuery = {
                isActive: true,
                $or: [
                    { busName: { $regex: query, $options: 'i' } },
                    { operatorName: { $regex: query, $options: 'i' } },
                    { busType: { $regex: query, $options: 'i' } }
                ]
            };
            const buses = await db.collection('buses').find(searchQuery).toArray();
            return buses;
        } catch (error) {
            throw new Error(`Error searching buses: ${error.message}`);
        }
    }

    // Validate bus data
    static validate(busData) {
        const errors = [];

        if (!busData.busName || busData.busName.trim().length < 2) {
            errors.push('Bus name must be at least 2 characters long');
        }

        if (!busData.busType) {
            errors.push('Bus type is required');
        }

        if (!busData.totalSeats || busData.totalSeats < 1 || busData.totalSeats > 100) {
            errors.push('Total seats must be between 1 and 100');
        }

        if (!busData.operatorName || busData.operatorName.trim().length < 2) {
            errors.push('Operator name is required');
        }

        if (!busData.busNumber || busData.busNumber.trim().length < 4) {
            errors.push('Valid bus number is required');
        }

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    }
}

module.exports = Bus;

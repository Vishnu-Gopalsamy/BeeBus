// Route Model for MongoDB
const dbConnection = require('../config/db');
const { ObjectId } = require('mongodb');

const COLLECTION = 'routes';

// Helper function to get db
const getDb = () => dbConnection.getDb();

// Create a new route
const create = async (routeData) => {
    const db = getDb();

    const route = {
        source: routeData.source,
        destination: routeData.destination,
        distance: routeData.distance || 0,
        duration: routeData.duration || 0, // in hours
        isActive: true,
        createdAt: new Date(),
        updatedAt: new Date()
    };

    const result = await db.collection(COLLECTION).insertOne(route);
    return { ...route, _id: result.insertedId };
};

// Find route by ID
const findById = async (id) => {
    const db = getDb();
    return await db.collection(COLLECTION).findOne({
        _id: new ObjectId(id),
        isActive: true
    });
};

// Find route by source and destination
const findByRoute = async (source, destination) => {
    const db = getDb();
    return await db.collection(COLLECTION).findOne({
        source: { $regex: new RegExp(source, 'i') },
        destination: { $regex: new RegExp(destination, 'i') },
        isActive: true
    });
};

// Get all routes
const findAll = async () => {
    const db = getDb();
    return await db.collection(COLLECTION).find({ isActive: true }).toArray();
};

// Get popular routes
const getPopularRoutes = async (limit = 5) => {
    const db = getDb();

    // Aggregate to get popular routes based on booking count
    const routes = await db.collection('schedules').aggregate([
        { $match: { isActive: true } },
        {
            $group: {
                _id: { source: "$route.source", destination: "$route.destination" },
                count: { $sum: 1 },
                minPrice: { $min: "$price" }
            }
        },
        { $sort: { count: -1 } },
        { $limit: limit },
        {
            $project: {
                _id: 0,
                source: "$_id.source",
                destination: "$_id.destination",
                count: 1,
                minPrice: 1
            }
        }
    ]).toArray();

    return routes;
};

// Update route
const update = async (id, updateData) => {
    const db = getDb();

    updateData.updatedAt = new Date();

    return await db.collection(COLLECTION).updateOne(
        { _id: new ObjectId(id) },
        { $set: updateData }
    );
};

// Delete route (soft delete)
const deleteById = async (id) => {
    const db = getDb();
    return await db.collection(COLLECTION).updateOne(
        { _id: new ObjectId(id) },
        { $set: { isActive: false, updatedAt: new Date() } }
    );
};

// Search routes
const search = async (query) => {
    const db = getDb();

    const filter = {
        isActive: true,
        $or: [
            { source: { $regex: new RegExp(query, 'i') } },
            { destination: { $regex: new RegExp(query, 'i') } }
        ]
    };

    return await db.collection(COLLECTION).find(filter).toArray();
};

// Validate route data
const validate = (routeData) => {
    const errors = [];

    if (!routeData.source || routeData.source.trim().length < 2) {
        errors.push('Source city is required (min 2 characters)');
    }

    if (!routeData.destination || routeData.destination.trim().length < 2) {
        errors.push('Destination city is required (min 2 characters)');
    }

    if (routeData.source && routeData.destination &&
        routeData.source.toLowerCase() === routeData.destination.toLowerCase()) {
        errors.push('Source and destination cannot be the same');
    }

    return {
        isValid: errors.length === 0,
        errors: errors
    };
};

module.exports = {
    create,
    findById,
    findByRoute,
    findBySourceAndDestination: findByRoute,
    findAll,
    getPopularRoutes,
    update,
    deleteById,
    delete: deleteById,
    search,
    validate
};

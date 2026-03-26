const Route = require('../models/Route');
const dbConnection = require('../config/db');

// Default cities list
const DEFAULT_CITIES = [
    'Chennai', 'Bangalore', 'Mumbai', 'Delhi', 'Hyderabad',
    'Coimbatore', 'Madurai', 'Trichy', 'Salem', 'Erode',
    'Tirunelveli', 'Puducherry', 'Vellore', 'Thanjavur',
    'Kochi', 'Thiruvananthapuram', 'Mysore', 'Mangalore',
    'Vizag', 'Vijayawada', 'Pune', 'Goa', 'Kolkata', 'Jaipur',
    'Ahmedabad', 'Lucknow', 'Patna', 'Bhopal', 'Chandigarh',
    'Agra', 'Nagpur', 'Surat', 'Indore', 'Varanasi'
];

// Get all routes
const getAllRoutes = async (req, res) => {
    try {
        const routes = await Route.findAll();
        res.status(200).json({
            success: true,
            message: 'Routes retrieved successfully',
            data: {
                routes: routes,
                count: routes.length
            }
        });
    } catch (error) {
        console.error('Get all routes error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve routes',
            error: error.message
        });
    }
};

// Get route by ID
const getRouteById = async (req, res) => {
    try {
        const { id } = req.params;
        const route = await Route.findById(id);

        if (!route) {
            return res.status(404).json({
                success: false,
                message: 'Route not found'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Route retrieved successfully',
            data: { route }
        });
    } catch (error) {
        console.error('Get route error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve route',
            error: error.message
        });
    }
};

// Create new route
const createRoute = async (req, res) => {
    try {
        const { source, destination, distance, duration } = req.body;

        if (!source || !destination) {
            return res.status(400).json({
                success: false,
                message: 'Source and destination are required'
            });
        }

        // Check for existing route
        const existingRoute = await Route.findBySourceAndDestination(source, destination);
        if (existingRoute) {
            return res.status(400).json({
                success: false,
                message: 'Route already exists'
            });
        }

        const routeData = {
            source: source.trim(),
            destination: destination.trim(),
            distance: distance || 0,
            duration: duration || 0,
            isActive: true,
            createdAt: new Date(),
            updatedAt: new Date()
        };

        const newRoute = await Route.create(routeData);

        res.status(201).json({
            success: true,
            message: 'Route created successfully',
            data: { route: newRoute }
        });
    } catch (error) {
        console.error('Create route error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to create route',
            error: error.message
        });
    }
};

// Update route
const updateRoute = async (req, res) => {
    try {
        const { id } = req.params;
        const updates = req.body;
        updates.updatedAt = new Date();

        const updatedRoute = await Route.update(id, updates);

        if (!updatedRoute) {
            return res.status(404).json({
                success: false,
                message: 'Route not found'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Route updated successfully',
            data: { route: updatedRoute }
        });
    } catch (error) {
        console.error('Update route error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to update route',
            error: error.message
        });
    }
};

// Delete route
const deleteRoute = async (req, res) => {
    try {
        const { id } = req.params;
        const deleted = await Route.delete(id);

        if (!deleted) {
            return res.status(404).json({
                success: false,
                message: 'Route not found'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Route deleted successfully'
        });
    } catch (error) {
        console.error('Delete route error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to delete route',
            error: error.message
        });
    }
};

// Get all unique cities
const getAllCities = async (req, res) => {
    try {
        const citiesSet = new Set(DEFAULT_CITIES);

        // Get cities from routes collection
        const routes = await Route.findAll();
        routes.forEach(route => {
            if (route.source) citiesSet.add(route.source);
            if (route.destination) citiesSet.add(route.destination);
        });

        // Get cities from schedules collection
        try {
            const db = dbConnection.getDb();
            const schedules = await db.collection('schedules').find({ isActive: true }).toArray();
            schedules.forEach(schedule => {
                if (schedule.source) citiesSet.add(schedule.source);
                if (schedule.destination) citiesSet.add(schedule.destination);
                if (schedule.route) {
                    if (schedule.route.source) citiesSet.add(schedule.route.source);
                    if (schedule.route.destination) citiesSet.add(schedule.route.destination);
                }
            });
        } catch (scheduleError) {
            console.log('Could not fetch schedules for cities:', scheduleError.message);
        }

        const cities = Array.from(citiesSet).sort();

        res.status(200).json({
            success: true,
            message: 'Cities retrieved successfully',
            data: {
                cities: cities,
                count: cities.length
            }
        });
    } catch (error) {
        console.error('Get all cities error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve cities',
            error: error.message
        });
    }
};

// Search cities by query
const searchCities = async (req, res) => {
    try {
        const { q } = req.query;
        const citiesSet = new Set(DEFAULT_CITIES);

        // Get cities from routes
        const routes = await Route.findAll();
        routes.forEach(route => {
            if (route.source) citiesSet.add(route.source);
            if (route.destination) citiesSet.add(route.destination);
        });

        // Get cities from schedules
        try {
            const db = dbConnection.getDb();
            const schedules = await db.collection('schedules').find({ isActive: true }).toArray();
            schedules.forEach(schedule => {
                if (schedule.source) citiesSet.add(schedule.source);
                if (schedule.destination) citiesSet.add(schedule.destination);
                if (schedule.route) {
                    if (schedule.route.source) citiesSet.add(schedule.route.source);
                    if (schedule.route.destination) citiesSet.add(schedule.route.destination);
                }
            });
        } catch (scheduleError) {
            console.log('Could not fetch schedules for cities:', scheduleError.message);
        }

        let cities = Array.from(citiesSet).sort();

        // Filter cities by query if provided
        if (q && q.trim()) {
            const query = q.trim().toLowerCase();
            cities = cities.filter(city =>
                city.toLowerCase().includes(query)
            );
        }

        res.status(200).json({
            success: true,
            message: 'Cities retrieved successfully',
            data: {
                cities: cities,
                count: cities.length
            }
        });
    } catch (error) {
        console.error('Search cities error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to search cities',
            error: error.message
        });
    }
};

module.exports = {
    getAllRoutes,
    getRouteById,
    createRoute,
    updateRoute,
    deleteRoute,
    getAllCities,
    searchCities
};


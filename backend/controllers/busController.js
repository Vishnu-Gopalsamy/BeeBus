const Bus = require('../models/Bus');
const Schedule = require('../models/Schedule');
const Seat = require('../models/Seat');
const Route = require('../models/Route');

// Get all schedules (Admin)
const getAllSchedules = async (req, res) => {
    try {
        const schedules = await Schedule.findAll();

        res.status(200).json({
            success: true,
            message: 'Schedules retrieved successfully',
            data: {
                schedules: schedules,
                count: schedules.length
            }
        });

    } catch (error) {
        console.error('Get all schedules error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve schedules',
            error: error.message
        });
    }
};

// Get all buses
const getAllBuses = async (req, res) => {
    try {
        const buses = await Bus.findAll();

        res.status(200).json({
            success: true,
            message: 'Buses retrieved successfully',
            data: {
                buses: buses,
                count: buses.length
            }
        });

    } catch (error) {
        console.error('Get all buses error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve buses',
            error: error.message
        });
    }
};

// Get buses for logged-in owner
const getOwnerBuses = async (req, res) => {
    try {
        const ownerId = req.user._id;
        const buses = await Bus.findByOwnerId(ownerId);

        res.status(200).json({
            success: true,
            message: 'Owner buses retrieved successfully',
            data: {
                buses: buses,
                count: buses.length
            }
        });

    } catch (error) {
        console.error('Get owner buses error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve owner buses',
            error: error.message
        });
    }
};

// Get bus by ID
const getBusById = async (req, res) => {
    try {
        const { id } = req.params;

        const bus = await Bus.findById(id);
        if (!bus) {
            return res.status(404).json({
                success: false,
                message: 'Bus not found'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Bus retrieved successfully',
            data: bus
        });

    } catch (error) {
        console.error('Get bus by ID error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve bus',
            error: error.message
        });
    }
};

// Search buses (schedules) by route and date
const searchBuses = async (req, res) => {
    try {
        const { source, destination, date } = req.query;

        // Validate input
        if (!source || !destination || !date) {
            return res.status(400).json({
                success: false,
                message: 'Source, destination, and date are required'
            });
        }

        // Validate date format
        const searchDate = new Date(date);
        if (isNaN(searchDate.getTime())) {
            return res.status(400).json({
                success: false,
                message: 'Invalid date format. Use YYYY-MM-DD'
            });
        }

        const schedules = await Schedule.search(source, destination, date);

        res.status(200).json({
            success: true,
            message: 'Bus schedules retrieved successfully',
            data: {
                schedules: schedules,
                count: schedules.length,
                searchCriteria: {
                    source: source,
                    destination: destination,
                    date: date
                }
            }
        });

    } catch (error) {
        console.error('Search buses error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to search buses',
            error: error.message
        });
    }
};

// Get popular routes
const getPopularRoutes = async (req, res) => {
    try {
        const { limit } = req.query;
        const routeLimit = parseInt(limit) || 5;

        const routes = await Schedule.getPopularRoutes(routeLimit);

        res.status(200).json({
            success: true,
            message: 'Popular routes retrieved successfully',
            data: {
                routes: routes,
                count: routes.length
            }
        });

    } catch (error) {
        console.error('Get popular routes error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve popular routes',
            error: error.message
        });
    }
};

// Get schedule details by ID
const getScheduleById = async (req, res) => {
    try {
        const { scheduleId } = req.params;

        const schedule = await Schedule.findById(scheduleId);
        if (!schedule) {
            return res.status(404).json({
                success: false,
                message: 'Schedule not found'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Schedule retrieved successfully',
            data: schedule
        });

    } catch (error) {
        console.error('Get schedule by ID error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve schedule',
            error: error.message
        });
    }
};

// Get seats for a schedule
const getSeats = async (req, res) => {
    try {
        const { scheduleId } = req.params;

        // First verify schedule exists
        const schedule = await Schedule.findById(scheduleId);
        if (!schedule) {
            return res.status(404).json({
                success: false,
                message: 'Schedule not found'
            });
        }

        // Get seats for the schedule
        const seats = await Seat.getByScheduleId(scheduleId);

        // If no seats exist, initialize them
        if (seats.length === 0) {
            await Seat.initializeSeats(scheduleId, schedule.bus.totalSeats);
            const newSeats = await Seat.getByScheduleId(scheduleId);

            return res.status(200).json({
                success: true,
                message: 'Seats initialized and retrieved successfully',
                data: {
                    seats: newSeats,
                    totalSeats: schedule.bus.totalSeats,
                    availableSeats: newSeats.filter(seat => seat.status === 'available').length
                }
            });
        }

        // Clean up expired blocked seats
        await Seat.cleanupExpiredBlocks();

        res.status(200).json({
            success: true,
            message: 'Seats retrieved successfully',
            data: {
                seats: seats,
                totalSeats: seats.length,
                availableSeats: seats.filter(seat => seat.status === 'available').length
            }
        });

    } catch (error) {
        console.error('Get seats error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve seats',
            error: error.message
        });
    }
};

// Block seats temporarily (during booking process)
const blockSeats = async (req, res) => {
    try {
        const { scheduleId } = req.params;
        const { seatNumbers } = req.body;
        const userId = req.user._id;

        // Validate input
        if (!seatNumbers || !Array.isArray(seatNumbers) || seatNumbers.length === 0) {
            return res.status(400).json({
                success: false,
                message: 'Seat numbers are required'
            });
        }

        // Verify schedule exists
        const schedule = await Schedule.findById(scheduleId);
        if (!schedule) {
            return res.status(404).json({
                success: false,
                message: 'Schedule not found'
            });
        }

        // Block seats for 5 minutes
        await Seat.blockSeats(scheduleId, seatNumbers, userId, 300000);

        res.status(200).json({
            success: true,
            message: 'Seats blocked successfully',
            data: {
                scheduleId: scheduleId,
                blockedSeats: seatNumbers,
                expiresIn: 300 // seconds
            }
        });

    } catch (error) {
        console.error('Block seats error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to block seats',
            error: error.message
        });
    }
};

// Create new bus (Admin or Owner)
const createBus = async (req, res) => {
    try {
        const busData = req.body;

        // If the creator is an owner, automatically assign the bus to them
        if (req.user.role === 'owner') {
            busData.ownerId = req.user._id;
        }

        // Validate input
        const validation = Bus.validate(busData);
        if (!validation.isValid) {
            return res.status(400).json({
                success: false,
                message: 'Validation failed',
                errors: validation.errors
            });
        }

        const bus = await Bus.create(busData);

        res.status(201).json({
            success: true,
            message: 'Bus created successfully',
            data: bus
        });

    } catch (error) {
        console.error('Create bus error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to create bus',
            error: error.message
        });
    }
};

// Create new schedule (Admin only)
const createSchedule = async (req, res) => {
    try {
        const scheduleData = req.body;

        // If routeId is provided but source/destination are missing, fetch them
        if (scheduleData.routeId && (!scheduleData.source || !scheduleData.destination)) {
            const route = await Route.findById(scheduleData.routeId);
            if (route) {
                scheduleData.source = route.source;
                scheduleData.destination = route.destination;
                scheduleData.distance = route.distance;
                scheduleData.duration = route.duration;
            } else {
                return res.status(404).json({
                    success: false,
                    message: 'Route not found'
                });
            }
        }

        // Validate input
        const validation = Schedule.validate(scheduleData);
        if (!validation.isValid) {
            return res.status(400).json({
                success: false,
                message: 'Validation failed',
                errors: validation.errors
            });
        }

        // Verify bus exists
        const bus = await Bus.findById(scheduleData.busId);
        if (!bus) {
            return res.status(404).json({
                success: false,
                message: 'Bus not found'
            });
        }

        // Verify ownership for owners
        if (req.user.role === 'owner' && bus.ownerId?.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'You are not authorized to create a schedule for this bus'
            });
        }

        // Set available seats to bus capacity if not provided
        if (!scheduleData.availableSeats) {
            scheduleData.availableSeats = bus.totalSeats;
        }

        const schedule = await Schedule.create(scheduleData);

        // Initialize seats for the new schedule
        await Seat.initializeSeats(schedule._id, bus.totalSeats);

        res.status(201).json({
            success: true,
            message: 'Schedule created successfully',
            data: schedule
        });

    } catch (error) {
        console.error('Create schedule error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to create schedule',
            error: error.message
        });
    }
};

// Update bus (Admin only)
const updateBus = async (req, res) => {
    try {
        const { id } = req.params;
        const updateData = req.body;

        const bus = await Bus.findById(id);
        if (!bus) {
            return res.status(404).json({
                success: false,
                message: 'Bus not found'
            });
        }

        if (req.user.role === 'owner' && bus.ownerId?.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'You are not authorized to update this bus'
            });
        }

        await Bus.updateById(id, updateData);
        const updatedBus = await Bus.findById(id);

        res.status(200).json({
            success: true,
            message: 'Bus updated successfully',
            data: updatedBus
        });

    } catch (error) {
        console.error('Update bus error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to update bus',
            error: error.message
        });
    }
};

// Delete bus (Admin only)
const deleteBus = async (req, res) => {
    try {
        const { id } = req.params;

        const bus = await Bus.findById(id);
        if (!bus) {
            return res.status(404).json({
                success: false,
                message: 'Bus not found'
            });
        }

        if (req.user.role === 'owner' && bus.ownerId?.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'You are not authorized to delete this bus'
            });
        }

        await Bus.deleteById(id);

        res.status(200).json({
            success: true,
            message: 'Bus deleted successfully'
        });

    } catch (error) {
        console.error('Delete bus error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to delete bus',
            error: error.message
        });
    }
};

// Delete schedule (Admin only)
const deleteSchedule = async (req, res) => {
    try {
        const { scheduleId } = req.params;

        const schedule = await Schedule.findById(scheduleId);
        if (!schedule) {
            return res.status(404).json({
                success: false,
                message: 'Schedule not found'
            });
        }

        if (req.user.role === 'owner' && schedule.bus?.ownerId?.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'You are not authorized to delete this schedule'
            });
        }

        await Schedule.deleteById(scheduleId);

        res.status(200).json({
            success: true,
            message: 'Schedule deleted successfully'
        });

    } catch (error) {
        console.error('Delete schedule error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to delete schedule',
            error: error.message
        });
    }
};

module.exports = {
    getAllBuses,
    getOwnerBuses,
    getAllSchedules,
    getBusById,
    searchBuses,
    getPopularRoutes,
    getScheduleById,
    getSeats,
    blockSeats,
    createBus,
    updateBus,
    deleteBus,
    createSchedule,
    deleteSchedule
};

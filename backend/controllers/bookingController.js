const Booking = require('../models/Booking');
const Schedule = require('../models/Schedule');
const Seat = require('../models/Seat');
const Payment = require('../models/Payment');

// Create new booking
const createBooking = async (req, res) => {
    try {
        const bookingData = {
            ...req.body,
            userId: req.user._id
        };

        // Validate booking data
        const validation = Booking.validate(bookingData);
        if (!validation.isValid) {
            return res.status(400).json({
                success: false,
                message: 'Validation failed',
                errors: validation.errors
            });
        }

        // Verify schedule exists and get details
        const schedule = await Schedule.findById(bookingData.scheduleId);
        if (!schedule) {
            return res.status(404).json({
                success: false,
                message: 'Schedule not found'
            });
        }

        // Check if enough seats are available
        if (schedule.availableSeats < bookingData.seats.length) {
            return res.status(400).json({
                success: false,
                message: 'Not enough seats available'
            });
        }

        // Check if all requested seats are available
        const seats = await Seat.getByScheduleId(bookingData.scheduleId);
        const requestedSeats = seats.filter(seat =>
            bookingData.seats.includes(seat.seatNumber) &&
            seat.status !== 'available'
        );

        if (requestedSeats.length > 0) {
            return res.status(400).json({
                success: false,
                message: 'Some requested seats are not available',
                unavailableSeats: requestedSeats.map(s => s.seatNumber)
            });
        }

        // Add schedule and bus details to booking
        bookingData.source = schedule.route.source;
        bookingData.destination = schedule.route.destination;
        bookingData.travelDate = schedule.travelDate;
        bookingData.departureTime = schedule.departureTime;
        bookingData.arrivalTime = schedule.arrivalTime;
        bookingData.busName = schedule.bus.busName;
        bookingData.busType = schedule.bus.busType;
        bookingData.contactEmail = req.user.email;
        bookingData.contactPhone = req.user.phone;

        // Create booking
        const booking = await Booking.create(bookingData);

        // Book the seats
        await Seat.bookSeats(bookingData.scheduleId, bookingData.seats, req.user._id);

        // Update available seats count in schedule
        await Schedule.updateAvailableSeats(bookingData.scheduleId, bookingData.seats.length);

        res.status(201).json({
            success: true,
            message: 'Booking created successfully',
            data: booking
        });

    } catch (error) {
        console.error('Create booking error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to create booking',
            error: error.message
        });
    }
};

// Get all bookings (Admin only)
const getAllBookings = async (req, res) => {
    try {
        const bookings = await Booking.findAll();

        res.status(200).json({
            success: true,
            message: 'All bookings retrieved successfully',
            data: {
                bookings: bookings,
                count: bookings.length
            }
        });

    } catch (error) {
        console.error('Get all bookings error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve bookings',
            error: error.message
        });
    }
};

// Get owner bookings
const getOwnerBookings = async (req, res) => {
    try {
        const ownerId = req.user._id;
        const bookings = await Booking.findByOwnerId(ownerId);

        res.status(200).json({
            success: true,
            message: 'Owner bookings retrieved successfully',
            data: {
                bookings: bookings,
                count: bookings.length
            }
        });

    } catch (error) {
        console.error('Get owner bookings error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve owner bookings',
            error: error.message
        });
    }
};

// Get booking by ID
const getBookingById = async (req, res) => {
    try {
        const { bookingId } = req.params;

        const booking = await Booking.findById(bookingId);
        if (!booking) {
            return res.status(404).json({
                success: false,
                message: 'Booking not found'
            });
        }

        // Check if user owns this booking (unless admin)
        if (req.user.role !== 'admin' && booking.userId.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Booking retrieved successfully',
            data: booking
        });

    } catch (error) {
        console.error('Get booking error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve booking',
            error: error.message
        });
    }
};

// Get user bookings
const getUserBookings = async (req, res) => {
    try {
        const userId = req.params.userId || req.user._id;

        // Check if user is accessing their own bookings (unless admin)
        if (req.user.role !== 'admin' && userId !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        const bookings = await Booking.findByUserId(userId);

        res.status(200).json({
            success: true,
            message: 'User bookings retrieved successfully',
            data: {
                bookings: bookings,
                count: bookings.length
            }
        });

    } catch (error) {
        console.error('Get user bookings error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve bookings',
            error: error.message
        });
    }
};

// Get booking by PNR
const getBookingByPNR = async (req, res) => {
    try {
        const { pnr } = req.params;

        const booking = await Booking.findByPNR(pnr);
        if (!booking) {
            return res.status(404).json({
                success: false,
                message: 'Booking not found with this PNR'
            });
        }

        // Check if user owns this booking (unless admin)
        if (req.user.role !== 'admin' && booking.userId.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Booking retrieved successfully',
            data: booking
        });

    } catch (error) {
        console.error('Get booking by PNR error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve booking',
            error: error.message
        });
    }
};

// Get detailed booking information
const getBookingDetails = async (req, res) => {
    try {
        const { bookingId } = req.params;

        const bookingDetails = await Booking.getBookingDetails(bookingId);
        if (!bookingDetails) {
            return res.status(404).json({
                success: false,
                message: 'Booking not found'
            });
        }

        // Check if user owns this booking (unless admin)
        if (req.user.role !== 'admin' && bookingDetails.userId.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Booking details retrieved successfully',
            data: bookingDetails
        });

    } catch (error) {
        console.error('Get booking details error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve booking details',
            error: error.message
        });
    }
};

// Cancel booking
const cancelBooking = async (req, res) => {
    try {
        const { bookingId } = req.params;
        const { reason } = req.body;

        // Get booking details
        const booking = await Booking.findById(bookingId);
        if (!booking) {
            return res.status(404).json({
                success: false,
                message: 'Booking not found'
            });
        }

        // Check if user owns this booking (unless admin)
        if (req.user.role !== 'admin' && booking.userId.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        // Check if booking can be cancelled
        if (booking.bookingStatus !== 'confirmed') {
            return res.status(400).json({
                success: false,
                message: 'Booking cannot be cancelled'
            });
        }

        // Check cancellation time (example: 2 hours before departure)
        const schedule = await Schedule.findById(booking.scheduleId);
        const travelDateTime = new Date(schedule.travelDate);
        // Parse time like "06:00 AM" or "02:30 PM"
        const timeMatch = schedule.departureTime.match(/(\d+):(\d+)\s*(AM|PM)?/i);
        if (timeMatch) {
            let hrs = parseInt(timeMatch[1]);
            const mins = parseInt(timeMatch[2]);
            const period = timeMatch[3];
            if (period) {
                if (period.toUpperCase() === 'PM' && hrs !== 12) hrs += 12;
                if (period.toUpperCase() === 'AM' && hrs === 12) hrs = 0;
            }
            travelDateTime.setHours(hrs, mins);
        }

        const currentTime = new Date();
        const timeDifference = travelDateTime.getTime() - currentTime.getTime();
        const hoursUntilDeparture = timeDifference / (1000 * 60 * 60);

        if (hoursUntilDeparture < 2) {
            return res.status(400).json({
                success: false,
                message: 'Booking cannot be cancelled less than 2 hours before departure'
            });
        }

        // Cancel booking
        await Booking.cancel(bookingId, reason);

        // Release seats
        await Seat.releaseBlockedSeats(booking.scheduleId, booking.seats);

        // Update available seats in schedule
        await Schedule.updateAvailableSeats(booking.scheduleId, -booking.seats.length);

        // Initiate refund if payment was completed
        if (booking.paymentStatus === 'completed') {
            const payment = await Payment.findByBookingId(bookingId);
            if (payment) {
                await Payment.initiateRefund(
                    payment.transactionId,
                    booking.totalAmount * 0.9, // 90% refund (10% cancellation fee)
                    'Booking cancellation'
                );
            }
        }

        res.status(200).json({
            success: true,
            message: 'Booking cancelled successfully',
            data: {
                bookingId: bookingId,
                refundAmount: booking.totalAmount * 0.9
            }
        });

    } catch (error) {
        console.error('Cancel booking error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to cancel booking',
            error: error.message
        });
    }
};

// Update booking status (Admin only)
const updateBookingStatus = async (req, res) => {
    try {
        const { bookingId } = req.params;
        const { status } = req.body;

        const validStatuses = ['confirmed', 'cancelled', 'completed'];
        if (!validStatuses.includes(status)) {
            return res.status(400).json({
                success: false,
                message: 'Invalid booking status'
            });
        }

        const result = await Booking.updateStatus(bookingId, status);

        if (result.matchedCount === 0) {
            return res.status(404).json({
                success: false,
                message: 'Booking not found'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Booking status updated successfully'
        });

    } catch (error) {
        console.error('Update booking status error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to update booking status',
            error: error.message
        });
    }
};

// Get booking statistics (Admin only)
const getBookingStats = async (req, res) => {
    try {
        const stats = await Booking.getBookingStats();

        res.status(200).json({
            success: true,
            message: 'Booking statistics retrieved successfully',
            data: {
                stats: stats
            }
        });

    } catch (error) {
        console.error('Get booking stats error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve booking statistics',
            error: error.message
        });
    }
};

module.exports = {
    createBooking,
    getAllBookings,
    getOwnerBookings,
    getBookingById,
    getUserBookings,
    getBookingByPNR,
    getBookingDetails,
    cancelBooking,
    updateBookingStatus,
    getBookingStats
};

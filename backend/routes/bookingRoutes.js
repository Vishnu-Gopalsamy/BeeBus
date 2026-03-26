const express = require('express');
const router = express.Router();
const bookingController = require('../controllers/bookingController');
const { authenticateToken, authenticateAdmin, authenticateOwner } = require('../middleware/authMiddleware');

// All booking routes require authentication
router.use(authenticateToken);

// Admin: get ALL bookings (must be before parameterized routes)
router.get('/', authenticateAdmin, bookingController.getAllBookings);

// Owner: get bookings for their buses
router.get('/owner/all', authenticateOwner, bookingController.getOwnerBookings);

// User booking routes
router.post('/', bookingController.createBooking);
router.get('/user/:userId?', bookingController.getUserBookings);

// Static paths must come before parameterized routes
router.get('/pnr/:pnr', bookingController.getBookingByPNR);

// Admin booking routes (static paths before :bookingId)
router.get('/admin/stats', authenticateAdmin, bookingController.getBookingStats);

// Parameterized routes last
router.get('/:bookingId', bookingController.getBookingById);
router.get('/:bookingId/details', bookingController.getBookingDetails);
router.post('/:bookingId/cancel', bookingController.cancelBooking);
router.put('/:bookingId/status', authenticateAdmin, bookingController.updateBookingStatus);

module.exports = router;

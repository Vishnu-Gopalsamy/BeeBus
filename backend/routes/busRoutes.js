const express = require('express');
const router = express.Router();
const busController = require('../controllers/busController');
const { authenticateToken, authenticateAdmin, authenticateOwner, authenticateAdminOrOwner, optionalAuth } = require('../middleware/authMiddleware');

// Public routes (no authentication required)
router.get('/', busController.getAllBuses);
router.get('/search', busController.searchBuses);
router.get('/popular-routes', busController.getPopularRoutes);

// Owner routes (must be before /:id)
router.get('/owner/all', authenticateOwner, busController.getOwnerBuses);

// Admin/Owner routes for schedules (must be before /:id to avoid matching)
router.get('/schedules', authenticateAdmin, busController.getAllSchedules);
router.post('/schedules', authenticateAdminOrOwner, busController.createSchedule);
router.delete('/schedules/:scheduleId', authenticateAdminOrOwner, busController.deleteSchedule);

// Semi-protected routes (optional authentication)
router.get('/schedules/:scheduleId', optionalAuth, busController.getScheduleById);

// Bus by ID (must be after /search, /schedules, /popular-routes)
router.get('/:id', busController.getBusById);

// Protected routes (authentication required)
router.get('/:scheduleId/seats', authenticateToken, busController.getSeats);
router.post('/:scheduleId/block-seats', authenticateToken, busController.blockSeats);

// Admin/Owner routes for buses
router.post('/', authenticateAdminOrOwner, busController.createBus);
router.put('/:id', authenticateAdminOrOwner, busController.updateBus);
router.delete('/:id', authenticateAdminOrOwner, busController.deleteBus);

module.exports = router;

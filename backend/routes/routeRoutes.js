const express = require('express');
const router = express.Router();
const routeController = require('../controllers/routeController');
const { authenticateToken, authenticateAdmin } = require('../middleware/authMiddleware');

// Public routes
router.get('/', routeController.getAllRoutes);
router.get('/cities', routeController.getAllCities);
router.get('/cities/search', routeController.searchCities);
router.get('/:id', routeController.getRouteById);

// Admin routes
router.post('/', authenticateAdmin, routeController.createRoute);
router.put('/:id', authenticateAdmin, routeController.updateRoute);
router.delete('/:id', authenticateAdmin, routeController.deleteRoute);

module.exports = router;


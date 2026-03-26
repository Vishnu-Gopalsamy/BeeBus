const jwt = require('jsonwebtoken');
const User = require('../models/User');

// Authentication middleware
const authenticateToken = async (req, res, next) => {
    try {
        const authHeader = req.headers['authorization'];
        const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

        if (!token) {
            return res.status(401).json({
                success: false,
                message: 'Access token is required'
            });
        }

        // Verify JWT token
        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        // Get user details (excluding password)
        const user = await User.getProfile(decoded.userId);
        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'User not found'
            });
        }

        if (!user.isActive) {
            return res.status(401).json({
                success: false,
                message: 'User account is deactivated'
            });
        }

        // Attach user to request object
        req.user = user;
        next();
    } catch (error) {
        if (error.name === 'JsonWebTokenError') {
            return res.status(401).json({
                success: false,
                message: 'Invalid access token'
            });
        } else if (error.name === 'TokenExpiredError') {
            return res.status(401).json({
                success: false,
                message: 'Access token has expired'
            });
        } else {
            console.error('Authentication error:', error);
            return res.status(500).json({
                success: false,
                message: 'Authentication failed'
            });
        }
    }
};

// Optional authentication (user might or might not be logged in)
const optionalAuth = async (req, res, next) => {
    try {
        const authHeader = req.headers['authorization'];
        const token = authHeader && authHeader.split(' ')[1];

        if (token) {
            const decoded = jwt.verify(token, process.env.JWT_SECRET);
            const user = await User.getProfile(decoded.userId);
            if (user && user.isActive) {
                req.user = user;
            }
        }
        next();
    } catch (error) {
        // Ignore authentication errors for optional auth
        next();
    }
};

// Admin authentication middleware
const authenticateAdmin = async (req, res, next) => {
    try {
        // First check if user is authenticated
        await authenticateToken(req, res, async () => {
            // Check if user has admin role
            if (req.user.role !== 'admin') {
                return res.status(403).json({
                    success: false,
                    message: 'Admin access required'
                });
            }

            next();
        });
    } catch (error) {
        return res.status(500).json({
            success: false,
            message: 'Authorization failed'
        });
    }
};

// Owner authentication middleware
const authenticateOwner = async (req, res, next) => {
    try {
        await authenticateToken(req, res, async () => {
            if (req.user.role !== 'owner') {
                return res.status(403).json({
                    success: false,
                    message: 'Bus Owner access required'
                });
            }
            next();
        });
    } catch (error) {
        return res.status(500).json({
            success: false,
            message: 'Authorization failed'
        });
    }
};

// Admin OR Owner authentication middleware
const authenticateAdminOrOwner = async (req, res, next) => {
    try {
        await authenticateToken(req, res, async () => {
            if (req.user.role !== 'admin' && req.user.role !== 'owner') {
                return res.status(403).json({
                    success: false,
                    message: 'Admin or Owner access required'
                });
            }
            next();
        });
    } catch (error) {
        return res.status(500).json({
            success: false,
            message: 'Authorization failed'
        });
    }
};

// Generate JWT token
const generateToken = (userId) => {
    return jwt.sign(
        { userId: userId },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRE || '7d' }
    );
};

// Validate token without middleware
const validateToken = (token) => {
    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        return { valid: true, decoded };
    } catch (error) {
        return { valid: false, error: error.message };
    }
};

module.exports = {
    authenticateToken,
    optionalAuth,
    authenticateAdmin,
    authenticateOwner,
    authenticateAdminOrOwner,
    generateToken,
    validateToken
};

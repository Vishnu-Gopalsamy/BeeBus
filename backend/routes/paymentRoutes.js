const express = require('express');
const router = express.Router();
const Payment = require('../models/Payment');
const Booking = require('../models/Booking');
const { authenticateToken } = require('../middleware/authMiddleware');

// All payment routes require authentication
router.use(authenticateToken);

// Process payment
router.post('/', async (req, res) => {
    try {
        const { bookingId, paymentMethod, amount } = req.body;
        const userId = req.user._id;

        // Validate input
        if (!bookingId || !paymentMethod || !amount) {
            return res.status(400).json({
                success: false,
                message: 'Booking ID, payment method, and amount are required'
            });
        }

        // Verify booking exists and belongs to user
        const booking = await Booking.findById(bookingId);
        if (!booking) {
            return res.status(404).json({
                success: false,
                message: 'Booking not found'
            });
        }

        if (booking.userId.toString() !== userId.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        // Check if booking is already paid
        if (booking.paymentStatus === 'completed') {
            return res.status(400).json({
                success: false,
                message: 'Booking is already paid'
            });
        }

        // Validate amount matches booking total
        if (amount !== booking.totalAmount) {
            return res.status(400).json({
                success: false,
                message: 'Amount mismatch'
            });
        }

        // Process payment
        const paymentData = {
            bookingId: bookingId,
            userId: userId,
            amount: amount,
            paymentMethod: paymentMethod,
            userAgent: req.headers['user-agent'],
            ipAddress: req.ip,
            deviceType: 'mobile'
        };

        const paymentResult = await Payment.processPayment(paymentData);

        if (paymentResult.success) {
            // Update booking payment status
            await Booking.updatePaymentStatus(bookingId, 'completed', paymentResult.transactionId);

            res.status(200).json({
                success: true,
                message: 'Payment completed successfully',
                data: {
                    transactionId: paymentResult.transactionId,
                    bookingId: bookingId,
                    amount: amount
                }
            });
        } else {
            // Update booking payment status to failed
            await Booking.updatePaymentStatus(bookingId, 'failed');

            res.status(400).json({
                success: false,
                message: 'Payment failed',
                data: {
                    transactionId: paymentResult.transactionId,
                    reason: paymentResult.message
                }
            });
        }

    } catch (error) {
        console.error('Payment processing error:', error);
        res.status(500).json({
            success: false,
            message: 'Payment processing failed',
            error: error.message
        });
    }
});

// Get payment status
router.get('/:transactionId', async (req, res) => {
    try {
        const { transactionId } = req.params;
        const userId = req.user._id;

        const payment = await Payment.findByTransactionId(transactionId);
        if (!payment) {
            return res.status(404).json({
                success: false,
                message: 'Payment not found'
            });
        }

        // Check if user owns this payment
        if (payment.userId.toString() !== userId.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Payment status retrieved successfully',
            data: {
                payment: payment
            }
        });

    } catch (error) {
        console.error('Get payment status error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve payment status',
            error: error.message
        });
    }
});

// Get user payments
router.get('/user/history', async (req, res) => {
    try {
        const userId = req.user._id;

        const payments = await Payment.findByUserId(userId);

        res.status(200).json({
            success: true,
            message: 'Payment history retrieved successfully',
            data: {
                payments: payments,
                count: payments.length
            }
        });

    } catch (error) {
        console.error('Get payment history error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to retrieve payment history',
            error: error.message
        });
    }
});

// Refund payment (for cancellations)
router.post('/:transactionId/refund', async (req, res) => {
    try {
        const { transactionId } = req.params;
        const { amount, reason } = req.body;
        const userId = req.user._id;

        // Find original payment
        const payment = await Payment.findByTransactionId(transactionId);
        if (!payment) {
            return res.status(404).json({
                success: false,
                message: 'Payment not found'
            });
        }

        // Check if user owns this payment
        if (payment.userId.toString() !== userId.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        // Process refund
        const refund = await Payment.initiateRefund(transactionId, amount, reason);

        res.status(200).json({
            success: true,
            message: 'Refund initiated successfully',
            data: {
                refundTransactionId: refund.transactionId,
                refundAmount: amount,
                originalTransactionId: transactionId
            }
        });

    } catch (error) {
        console.error('Refund processing error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to process refund',
            error: error.message
        });
    }
});

module.exports = router;

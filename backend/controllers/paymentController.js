const Payment = require('../models/Payment');
const Booking = require('../models/Booking');

// Process payment
const processPayment = async (req, res) => {
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

        // Verify booking exists
        const booking = await Booking.findByBookingId(bookingId);
        if (!booking) {
            return res.status(404).json({
                success: false,
                message: 'Booking not found'
            });
        }

        // Check if booking belongs to user
        if (booking.userId.toString() !== userId.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        // Check if amount matches
        if (Math.abs(booking.totalAmount - amount) > 0.01) {
            return res.status(400).json({
                success: false,
                message: 'Amount mismatch'
            });
        }

        // Create payment record
        const paymentData = {
            bookingId: bookingId,
            userId: userId,
            amount: amount,
            paymentMethod: paymentMethod,
            gateway: 'mock_gateway',
            status: 'initiated'
        };

        const payment = await Payment.create(paymentData);

        // Simulate payment processing (in production, integrate with actual gateway)
        const isPaymentSuccessful = Math.random() > 0.1; // 90% success rate for demo

        if (isPaymentSuccessful) {
            // Update payment status
            await Payment.updateStatus(payment.transactionId, 'completed', {
                gatewayTransactionId: 'GTX' + Date.now(),
                completedAt: new Date()
            });

            // Update booking payment status
            await Booking.updatePaymentStatus(bookingId, 'completed');

            res.status(200).json({
                success: true,
                message: 'Payment processed successfully',
                data: {
                    transactionId: payment.transactionId,
                    bookingId: bookingId,
                    amount: amount,
                    paymentMethod: paymentMethod,
                    status: 'completed',
                    gatewayTransactionId: 'GTX' + Date.now()
                }
            });
        } else {
            await Payment.updateStatus(payment.transactionId, 'failed');

            res.status(400).json({
                success: false,
                message: 'Payment failed. Please try again.',
                data: {
                    transactionId: payment.transactionId,
                    status: 'failed'
                }
            });
        }

    } catch (error) {
        console.error('Process payment error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to process payment',
            error: error.message
        });
    }
};

// Get payment status
const getPaymentStatus = async (req, res) => {
    try {
        const { transactionId } = req.params;

        const payment = await Payment.findByTransactionId(transactionId);
        if (!payment) {
            return res.status(404).json({
                success: false,
                message: 'Payment not found'
            });
        }

        res.status(200).json({
            success: true,
            message: 'Payment status retrieved',
            data: payment
        });

    } catch (error) {
        console.error('Get payment status error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to get payment status',
            error: error.message
        });
    }
};

// Get user payment history
const getPaymentHistory = async (req, res) => {
    try {
        const userId = req.user._id;

        const payments = await Payment.findByUserId(userId);

        res.status(200).json({
            success: true,
            message: 'Payment history retrieved',
            data: {
                payments: payments,
                count: payments.length
            }
        });

    } catch (error) {
        console.error('Get payment history error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to get payment history',
            error: error.message
        });
    }
};

// Process refund
const processRefund = async (req, res) => {
    try {
        const { transactionId } = req.params;
        const { amount, reason } = req.body;

        const payment = await Payment.findByTransactionId(transactionId);
        if (!payment) {
            return res.status(404).json({
                success: false,
                message: 'Payment not found'
            });
        }

        if (payment.status !== 'completed') {
            return res.status(400).json({
                success: false,
                message: 'Only completed payments can be refunded'
            });
        }

        const refundAmount = amount || payment.amount;
        if (refundAmount > payment.amount) {
            return res.status(400).json({
                success: false,
                message: 'Refund amount cannot exceed original payment'
            });
        }

        // Process refund (mock)
        const refundTransactionId = 'RFD' + Date.now() + Math.random().toString(36).substr(2, 6).toUpperCase();

        res.status(200).json({
            success: true,
            message: 'Refund processed successfully',
            data: {
                refundTransactionId: refundTransactionId,
                refundAmount: refundAmount,
                originalTransactionId: transactionId,
                reason: reason || 'Customer requested refund'
            }
        });

    } catch (error) {
        console.error('Process refund error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to process refund',
            error: error.message
        });
    }
};

module.exports = {
    processPayment,
    getPaymentStatus,
    getPaymentHistory,
    processRefund
};

const { ObjectId } = require('mongodb');
const { v4: uuidv4 } = require('uuid');
const dbConnection = require('../config/db');

class Payment {
    constructor(paymentData) {
        this.transactionId = uuidv4().replace(/-/g, '').toUpperCase().substring(0, 12);
        this.bookingId = paymentData.bookingId;
        this.userId = new ObjectId(paymentData.userId);
        this.amount = paymentData.amount;
        this.paymentMethod = paymentData.paymentMethod; // 'upi', 'card', 'netbanking', 'wallet'
        this.status = 'initiated'; // initiated, processing, completed, failed, refunded
        this.gateway = paymentData.gateway || 'test'; // razorpay, stripe, payu, test
        this.gatewayTransactionId = null; // Will be updated by payment gateway
        this.gatewayResponse = null; // Raw response from payment gateway
        this.initiatedAt = new Date();
        this.completedAt = null;
        this.failureReason = null;

        // Additional payment details
        this.metadata = {
            userAgent: paymentData.userAgent || null,
            ipAddress: paymentData.ipAddress || null,
            deviceType: paymentData.deviceType || 'mobile'
        };
    }

    // Create new payment
    static async create(paymentData) {
        try {
            const db = dbConnection.getDb();
            const payment = new Payment(paymentData);
            const result = await db.collection('payments').insertOne(payment);
            return { ...payment, _id: result.insertedId };
        } catch (error) {
            throw new Error(`Error creating payment: ${error.message}`);
        }
    }

    // Find payment by transaction ID
    static async findByTransactionId(transactionId) {
        try {
            const db = dbConnection.getDb();
            const payment = await db.collection('payments').findOne({
                transactionId: transactionId
            });
            return payment;
        } catch (error) {
            throw new Error(`Error finding payment: ${error.message}`);
        }
    }

    // Find payment by booking ID
    static async findByBookingId(bookingId) {
        try {
            const db = dbConnection.getDb();
            const payment = await db.collection('payments').findOne({
                bookingId: bookingId
            });
            return payment;
        } catch (error) {
            throw new Error(`Error finding payment: ${error.message}`);
        }
    }

    // Find payments by user ID
    static async findByUserId(userId) {
        try {
            const db = dbConnection.getDb();
            const payments = await db.collection('payments')
                .find({ userId: new ObjectId(userId) })
                .sort({ initiatedAt: -1 })
                .toArray();
            return payments;
        } catch (error) {
            throw new Error(`Error fetching user payments: ${error.message}`);
        }
    }

    // Update payment status
    static async updateStatus(transactionId, status, additionalData = {}) {
        try {
            const db = dbConnection.getDb();

            const updateData = {
                status: status,
                ...additionalData
            };

            if (status === 'completed') {
                updateData.completedAt = new Date();
            } else if (status === 'failed') {
                updateData.failedAt = new Date();
            }

            const result = await db.collection('payments').updateOne(
                { transactionId: transactionId },
                { $set: updateData }
            );

            return result;
        } catch (error) {
            throw new Error(`Error updating payment status: ${error.message}`);
        }
    }

    // Process payment (mock implementation)
    static async processPayment(paymentData) {
        try {
            // Create payment record
            const payment = await this.create(paymentData);

            // Simulate payment processing
            const success = await this.simulatePaymentGateway(payment.transactionId, paymentData.paymentMethod);

            if (success) {
                await this.updateStatus(payment.transactionId, 'completed', {
                    gatewayTransactionId: 'GW_' + payment.transactionId,
                    gatewayResponse: { status: 'SUCCESS', message: 'Payment completed successfully' }
                });

                return {
                    success: true,
                    transactionId: payment.transactionId,
                    message: 'Payment completed successfully'
                };
            } else {
                await this.updateStatus(payment.transactionId, 'failed', {
                    failureReason: 'Payment declined by gateway',
                    gatewayResponse: { status: 'FAILED', message: 'Insufficient funds' }
                });

                return {
                    success: false,
                    transactionId: payment.transactionId,
                    message: 'Payment failed'
                };
            }
        } catch (error) {
            throw new Error(`Error processing payment: ${error.message}`);
        }
    }

    // Simulate payment gateway (for testing)
    static async simulatePaymentGateway(transactionId, paymentMethod) {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 1000 + Math.random() * 2000));

        // Simulate success rate: 85% success, 15% failure
        const successRate = 0.85;
        const randomValue = Math.random();

        // UPI has higher success rate
        if (paymentMethod === 'upi') {
            return randomValue < 0.9;
        }

        // Cards have slightly lower success rate
        if (paymentMethod === 'card') {
            return randomValue < 0.8;
        }

        return randomValue < successRate;
    }

    // Initiate refund
    static async initiateRefund(transactionId, refundAmount, reason) {
        try {
            const db = dbConnection.getDb();

            // Find original payment
            const originalPayment = await this.findByTransactionId(transactionId);
            if (!originalPayment) {
                throw new Error('Original payment not found');
            }

            if (originalPayment.status !== 'completed') {
                throw new Error('Cannot refund incomplete payment');
            }

            // Create refund payment record
            const refundData = {
                bookingId: originalPayment.bookingId,
                userId: originalPayment.userId,
                amount: -refundAmount, // Negative amount for refund
                paymentMethod: originalPayment.paymentMethod,
                gateway: originalPayment.gateway,
                originalTransactionId: transactionId
            };

            const refundPayment = await this.create(refundData);

            // Simulate refund processing
            await this.updateStatus(refundPayment.transactionId, 'completed', {
                gatewayTransactionId: 'REF_' + refundPayment.transactionId,
                gatewayResponse: { status: 'SUCCESS', message: 'Refund processed successfully' },
                refundReason: reason
            });

            return refundPayment;
        } catch (error) {
            throw new Error(`Error initiating refund: ${error.message}`);
        }
    }

    // Get payment statistics
    static async getPaymentStats() {
        try {
            const db = dbConnection.getDb();
            const pipeline = [
                {
                    $group: {
                        _id: '$status',
                        count: { $sum: 1 },
                        totalAmount: { $sum: '$amount' }
                    }
                }
            ];

            const stats = await db.collection('payments').aggregate(pipeline).toArray();
            return stats;
        } catch (error) {
            throw new Error(`Error getting payment stats: ${error.message}`);
        }
    }

    // Get payment methods usage
    static async getPaymentMethodStats() {
        try {
            const db = dbConnection.getDb();
            const pipeline = [
                {
                    $match: { status: 'completed', amount: { $gt: 0 } }
                },
                {
                    $group: {
                        _id: '$paymentMethod',
                        count: { $sum: 1 },
                        totalAmount: { $sum: '$amount' }
                    }
                }
            ];

            const stats = await db.collection('payments').aggregate(pipeline).toArray();
            return stats;
        } catch (error) {
            throw new Error(`Error getting payment method stats: ${error.message}`);
        }
    }

    // Validate payment data
    static validate(paymentData) {
        const errors = [];

        if (!paymentData.bookingId) {
            errors.push('Booking ID is required');
        }

        if (!paymentData.userId) {
            errors.push('User ID is required');
        }

        if (!paymentData.amount || paymentData.amount <= 0) {
            errors.push('Valid amount is required');
        }

        if (!paymentData.paymentMethod) {
            errors.push('Payment method is required');
        }

        const validMethods = ['upi', 'card', 'netbanking', 'wallet'];
        if (paymentData.paymentMethod && !validMethods.includes(paymentData.paymentMethod)) {
            errors.push('Invalid payment method');
        }

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    }
}

module.exports = Payment;

const { ObjectId } = require('mongodb');
const dbConnection = require('../config/db');

class User {
    constructor(userData) {
        this.name = userData.name;
        this.email = userData.email;
        this.phone = userData.phone;
        this.password = userData.password; // Will be hashed before saving
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
        this.role = userData.role || 'user'; // user, admin, owner - defaults to 'user'
    }

    // Create new user
    static async create(userData) {
        try {
            const db = dbConnection.getDb();
            const user = new User(userData);
            const result = await db.collection('users').insertOne(user);
            return { ...user, _id: result.insertedId };
        } catch (error) {
            throw new Error(`Error creating user: ${error.message}`);
        }
    }

    // Find user by email
    static async findByEmail(email) {
        try {
            const db = dbConnection.getDb();
            const user = await db.collection('users').findOne({ email: email });
            return user;
        } catch (error) {
            throw new Error(`Error finding user: ${error.message}`);
        }
    }

    // Find user by ID
    static async findById(userId) {
        try {
            const db = dbConnection.getDb();
            const user = await db.collection('users').findOne({ _id: new ObjectId(userId) });
            return user;
        } catch (error) {
            throw new Error(`Error finding user: ${error.message}`);
        }
    }

    // Update user
    static async updateById(userId, updateData) {
        try {
            const db = dbConnection.getDb();
            updateData.updatedAt = new Date();
            const result = await db.collection('users').updateOne(
                { _id: new ObjectId(userId) },
                { $set: updateData }
            );
            return result;
        } catch (error) {
            throw new Error(`Error updating user: ${error.message}`);
        }
    }

    // Get user profile (without password)
    static async getProfile(userId) {
        try {
            const db = dbConnection.getDb();
            const user = await db.collection('users').findOne(
                { _id: new ObjectId(userId) },
                { projection: { password: 0 } }
            );
            return user;
        } catch (error) {
            throw new Error(`Error getting profile: ${error.message}`);
        }
    }

    // Check if email exists
    static async emailExists(email) {
        try {
            const db = dbConnection.getDb();
            const count = await db.collection('users').countDocuments({ email: email });
            return count > 0;
        } catch (error) {
            throw new Error(`Error checking email: ${error.message}`);
        }
    }

    // Validate user data
    static validate(userData) {
        const errors = [];

        if (!userData.name || userData.name.trim().length < 2) {
            errors.push('Name must be at least 2 characters long');
        }

        if (!userData.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(userData.email)) {
            errors.push('Valid email is required');
        }

        if (!userData.phone || !/^[0-9]{10}$/.test(userData.phone.replace(/\D/g, ''))) {
            errors.push('Valid 10-digit phone number is required');
        }

        if (!userData.password || userData.password.length < 3) {
            errors.push('Password must be at least 3 characters long');
        }

        return {
            isValid: errors.length === 0,
            errors: errors
        };
    }
}

module.exports = User;

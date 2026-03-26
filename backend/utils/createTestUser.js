const dbConnection = require('../config/db');
const bcrypt = require('bcrypt');

async function createTestUser() {
    try {
        console.log('🔄 Connecting to database...');
        await dbConnection.connect();

        const db = dbConnection.getDb();

        // Check if user already exists
        const existingUser = await db.collection('users').findOne({ email: 'john@example.com' });

        if (existingUser) {
            console.log('ℹ️ User john@example.com already exists. Updating password...');
            const hashedPassword = await bcrypt.hash('password123', 12);
            await db.collection('users').updateOne(
                { email: 'john@example.com' },
                { $set: { password: hashedPassword, isActive: true } }
            );
            console.log('✅ Password updated for john@example.com');
        } else {
            console.log('📝 Creating user john@example.com...');
            const hashedPassword = await bcrypt.hash('password123', 12);
            await db.collection('users').insertOne({
                name: 'John Doe',
                email: 'john@example.com',
                phone: '9876543211',
                password: hashedPassword,
                role: 'user',
                isActive: true,
                createdAt: new Date(),
                updatedAt: new Date()
            });
            console.log('✅ User john@example.com created successfully!');
        }

        // Also ensure admin user exists and has correct role
        const adminUser = await db.collection('users').findOne({ email: 'admin@busbooking.com' });
        if (adminUser) {
            await db.collection('users').updateOne(
                { email: 'admin@busbooking.com' },
                { $set: { role: 'admin', isActive: true } }
            );
            console.log('✅ Admin user role verified');
        } else {
            const hashedAdminPassword = await bcrypt.hash('admin123', 12);
            await db.collection('users').insertOne({
                name: 'Admin User',
                email: 'admin@busbooking.com',
                phone: '9876543210',
                password: hashedAdminPassword,
                role: 'admin',
                isActive: true,
                createdAt: new Date(),
                updatedAt: new Date()
            });
            console.log('✅ Admin user created');
        }

        // Ensure owner user exists
        const ownerUser = await db.collection('users').findOne({ email: 'owner@gmail.com' });
        if (ownerUser) {
            await db.collection('users').updateOne(
                { email: 'owner@gmail.com' },
                { $set: { role: 'owner', isActive: true } }
            );
            console.log('✅ Owner user role verified');
        } else {
            const hashedOwnerPassword = await bcrypt.hash('123', 12);
            await db.collection('users').insertOne({
                name: 'Bus Owner',
                email: 'owner@gmail.com',
                phone: '9876543212',
                password: hashedOwnerPassword,
                role: 'owner',
                isActive: true,
                createdAt: new Date(),
                updatedAt: new Date()
            });
            console.log('✅ Owner user created');
        }

        // List all users
        const users = await db.collection('users').find({}, { projection: { password: 0 } }).toArray();
        console.log('\n📋 All users in database:');
        users.forEach(user => {
            console.log(`  - ${user.email} (${user.role || 'user'})`);
        });

        process.exit(0);
    } catch (error) {
        console.error('❌ Error:', error);
        process.exit(1);
    }
}

createTestUser();


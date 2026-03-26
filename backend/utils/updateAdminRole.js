const dbConnection = require('../config/db');

async function updateAdminRole() {
    try {
        console.log('🔄 Connecting to database...');
        await dbConnection.connect();

        const db = dbConnection.getDb();

        // Update admin user role
        const result = await db.collection('users').updateOne(
            { email: 'admin@busbooking.com' },
            { $set: { role: 'admin' } }
        );

        if (result.modifiedCount > 0) {
            console.log('✅ Admin role updated successfully!');
        } else if (result.matchedCount > 0) {
            console.log('ℹ️ Admin user found but role was already set');
        } else {
            console.log('⚠️ Admin user not found');
        }

        // Verify the update
        const admin = await db.collection('users').findOne({ email: 'admin@busbooking.com' });
        console.log('👤 Admin user details:', {
            email: admin.email,
            role: admin.role,
            name: admin.name
        });

        process.exit(0);
    } catch (error) {
        console.error('❌ Error:', error);
        process.exit(1);
    }
}

updateAdminRole();


const dbConnection = require('../config/db');
const Bus = require('../models/Bus');
const Schedule = require('../models/Schedule');
const Seat = require('../models/Seat');
const bcrypt = require('bcrypt');
const User = require('../models/User');

async function seedDatabase() {
    try {
        console.log('🌱 Starting database seeding...');

        const db = dbConnection.getDb();

        // Clear existing data (optional - comment out if you want to keep existing data)
        console.log('🧹 Clearing existing data...');
        await db.collection('buses').deleteMany({});
        await db.collection('schedules').deleteMany({});
        await db.collection('seats').deleteMany({});
        await db.collection('users').deleteMany({});

        // Create sample admin user
        console.log('👤 Creating sample users...');
        const hashedPassword = await bcrypt.hash('admin123', 12);

        await User.create({
            name: 'Admin User',
            email: 'admin@busbooking.com',
            phone: '9876543210',
            password: hashedPassword,
            role: 'admin'
        });

        await User.create({
            name: 'John Doe',
            email: 'john@example.com',
            phone: '9876543211',
            password: await bcrypt.hash('password123', 12)
        });

        // Create sample buses
        console.log('🚌 Creating sample buses...');
        const buses = [];

        const busData = [
            {
                busName: 'Orange Travels',
                busType: 'AC Seater',
                totalSeats: 40,
                operatorName: 'Orange Tours',
                busNumber: 'TN01AB1234',
                rating: 4.2,
                amenities: ['WiFi', 'Charging Point', 'Water Bottle', 'Reading Light']
            },
            {
                busName: 'VRL Travels',
                busType: 'AC Sleeper',
                totalSeats: 36,
                operatorName: 'VRL Group',
                busNumber: 'KA02CD5678',
                rating: 4.5,
                amenities: ['WiFi', 'AC', 'Blanket', 'Charging Point', 'Entertainment']
            },
            {
                busName: 'SRS Travels',
                busType: 'Non-AC Seater',
                totalSeats: 45,
                operatorName: 'SRS Transport',
                busNumber: 'TN03EF9012',
                rating: 3.8,
                amenities: ['Charging Point', 'Water Bottle', 'Reading Light']
            },
            {
                busName: 'KPN Travels',
                busType: 'AC Seater',
                totalSeats: 42,
                operatorName: 'KPN Tours',
                busNumber: 'TN04GH3456',
                rating: 4.0,
                amenities: ['WiFi', 'AC', 'Charging Point', 'Snacks']
            },
            {
                busName: 'Parveen Travels',
                busType: 'AC Sleeper',
                totalSeats: 32,
                operatorName: 'Parveen Transport',
                busNumber: 'TN05IJ7890',
                rating: 4.3,
                amenities: ['WiFi', 'AC', 'Blanket', 'Pillow', 'Charging Point']
            }
        ];

        for (const data of busData) {
            const bus = await Bus.create(data);
            buses.push(bus);
        }

        // Create sample schedules
        console.log('📅 Creating sample schedules...');
        const routes = [
            {
                source: 'Chennai',
                destination: 'Coimbatore',
                distance: 507,
                duration: 8.5,
                basePrice: 450
            },
            {
                source: 'Chennai',
                destination: 'Bangalore',
                distance: 347,
                duration: 6.5,
                basePrice: 380
            },
            {
                source: 'Chennai',
                destination: 'Madurai',
                distance: 462,
                duration: 7.5,
                basePrice: 420
            },
            {
                source: 'Bangalore',
                destination: 'Coimbatore',
                distance: 363,
                duration: 6.0,
                basePrice: 320
            },
            {
                source: 'Coimbatore',
                destination: 'Kochi',
                distance: 200,
                duration: 4.0,
                basePrice: 250
            }
        ];

        const timeSlots = [
            { departure: '06:00 AM', arrival: '02:30 PM' },
            { departure: '10:30 AM', arrival: '07:00 PM' },
            { departure: '03:00 PM', arrival: '11:30 PM' },
            { departure: '08:00 PM', arrival: '04:30 AM' },
            { departure: '11:30 PM', arrival: '07:00 AM' }
        ];

        // Create schedules for next 7 days
        for (let day = 0; day < 7; day++) {
            const travelDate = new Date();
            travelDate.setDate(travelDate.getDate() + day);

            for (const route of routes) {
                for (let i = 0; i < Math.min(buses.length, 3); i++) { // Max 3 buses per route per day
                    const bus = buses[i];
                    const timeSlot = timeSlots[i % timeSlots.length];

                    const scheduleData = {
                        busId: bus._id,
                        source: route.source,
                        destination: route.destination,
                        distance: route.distance,
                        duration: route.duration,
                        travelDate: travelDate,
                        departureTime: timeSlot.departure,
                        arrivalTime: timeSlot.arrival,
                        price: route.basePrice + (Math.random() * 100), // Add some price variation
                        availableSeats: bus.totalSeats,
                        boardingPoints: [
                            `${route.source} Central Bus Stand`,
                            `${route.source} Railway Station`
                        ],
                        droppingPoints: [
                            `${route.destination} Bus Terminal`,
                            `${route.destination} Railway Station`
                        ]
                    };

                    const schedule = await Schedule.create(scheduleData);

                    // Initialize seats for this schedule
                    await Seat.initializeSeats(schedule._id, bus.totalSeats);
                }
            }
        }

        console.log('✅ Database seeding completed successfully!');
        console.log(`📊 Created ${buses.length} buses with schedules for the next 7 days`);

        // Print some sample data for testing
        console.log('\n🧪 Sample data for testing:');
        console.log('Admin Login: admin@busbooking.com / admin123');
        console.log('User Login: john@example.com / password123');
        console.log('Sample Routes:');
        routes.forEach(route => {
            console.log(`  - ${route.source} → ${route.destination}`);
        });

    } catch (error) {
        console.error('❌ Database seeding failed:', error);
        throw error;
    }
}

// Run seeding if this file is executed directly
if (require.main === module) {
    const dbConnection = require('../config/db');

    dbConnection.connect().then(() => {
        return seedDatabase();
    }).then(() => {
        console.log('🎉 Seeding process completed!');
        process.exit(0);
    }).catch((error) => {
        console.error('💥 Seeding process failed:', error);
        process.exit(1);
    });
}

module.exports = { seedDatabase };

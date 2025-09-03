# Hotel Reservation System - UI Development Prompt

## Project Context
I'm building a Hotel Reservation System with Spring Boot backend that includes:
- **Hotel & Room Management**: CRUD operations for hotels and rooms
- **Booking System**: Room reservations with availability checking
- **Spring Security**: JWT authentication with role-based access
- **Microservices**: Room-service + Payment-service + Gateway-service
- **Payment Integration**: Mock payment processing

## UI Requirements

### Design System
Create a modern, luxurious hotel booking website using **React + TypeScript + Tailwind CSS + shadcn/ui** with these design principles:

**Visual Style:**
- Elegant hospitality/hotel aesthetic with premium feel
- Sophisticated color palette (dark navy, gold accents, crisp whites)
- Clean, luxury design with subtle shadows and gradients
- High-quality hotel imagery placeholders
- Card-based layouts for hotels and rooms
- Smooth animations and micro-interactions
- Responsive design optimized for all devices

**shadcn/ui Components to Use:**
- Button, Card, Badge, Dialog, Sheet, Tabs, Drawer
- Form, Input, Select, Textarea, Checkbox, Switch
- Table, Pagination, Avatar, Separator, Progress
- Alert, Toast notifications, Skeleton loaders
- Calendar, DatePicker, DateRangePicker
- Command palette, Popover, Tooltip
- Accordion, Collapsible for room details

### Core Pages & Features

#### 1. Public Pages (No Authentication Required)
```
🏨 Landing Page:
- Hero section with luxury hotel imagery and booking widget
- Featured hotels carousel with premium visuals
- Quick search (location, check-in/out, guests)
- Hotel categories (Business, Luxury, Budget, Resort)
- Guest testimonials with star ratings
- Newsletter signup and footer

🔍 Hotels Browse Page:
- Advanced filter sidebar:
  * Location/city
  * Price range slider
  * Star rating filter
  * Amenities checklist (WiFi, Pool, Gym, Spa)
  * Room type preferences
- Hotels grid with elegant cards showing:
  * Hotel images carousel
  * Hotel name, location, star rating
  * Starting room price per night
  * Key amenities icons
  * "View Rooms" and "Book Now" buttons
- Map view toggle
- Sort options (price, rating, popularity)
- Pagination with smooth transitions

📋 Hotel Details Page:
- Image gallery with lightbox
- Hotel information section (description, amenities, policies)
- Room types grid with:
  * Room images and descriptions
  * Pricing per night
  * Occupancy details
  * Available amenities
  * "Book This Room" buttons
- Location map integration
- Guest reviews and ratings section
- Nearby attractions
- Booking sidebar with date picker and guest selector
```

#### 2. Authentication Pages
```
🔐 Login Page:
- Elegant centered form with hotel background
- Email/password fields with floating labels
- "Remember me" toggle
- Social login options (placeholder)
- "Forgot password?" link
- "New guest? Create account" link

📝 Register Page:
- Step-by-step registration:
  * Personal Information
  * Contact Details
  * Preferences (room type, amenities)
- Progress indicator with elegant stepper
- Form validation with smooth error states
- Welcome email confirmation step
```

#### 3. Guest Dashboard (Protected)
```
🎯 Dashboard Home:
- Personalized welcome with guest name
- Current/upcoming reservations cards
- Quick stats (total stays, loyalty points)
- Recommended hotels based on history
- Quick actions (new booking, manage reservations)
- Recent activity timeline

📅 My Reservations:
- Reservations list with status indicators
- Filter by status (upcoming, completed, cancelled)
- Reservation cards showing:
  * Hotel image and name
  * Check-in/out dates
  * Room type and number
  * Total amount paid
  * QR code for check-in
- Action buttons: "Modify", "Cancel", "Download Voucher"
- Reservation timeline modal with status updates

👤 Profile Management:
- Personal information form
- Contact details and preferences
- Password change section
- Loyalty program status
- Payment methods management
- Notification preferences
```

#### 4. Hotel Admin Dashboard (Protected - Admin Role)
```
📊 Admin Overview:
- Key metrics dashboard:
  * Daily/monthly revenue charts
  * Occupancy rates
  * Average daily rate (ADR)
  * Revenue per available room (RevPAR)
- Recent bookings table
- Room availability status
- Quick actions panel

🏨 Hotel Management:
- Hotels data table with search/filter
- Create/Edit hotel modal with:
  * Basic information form
  * Amenities checklist
  * Image upload section
  * Location mapping
- Hotel status controls (active/inactive)
- Hotel performance analytics

🛏️ Room Management:
- Rooms data table grouped by hotel
- Room creation/editing with:
  * Room type selection
  * Pricing management
  * Amenities configuration
  * Image gallery upload
  * Availability calendar
- Bulk room operations
- Room maintenance scheduling

📋 Booking Management:
- All bookings with advanced filters:
  * Date range picker
  * Hotel/room filters
  * Status filters
  * Guest search
- Booking details view with:
  * Guest information
  * Payment details
  * Special requests
  * Check-in/out status
- Booking modification capabilities
- Revenue reporting and analytics

💳 Payment Management:
- Payment transactions table
- Payment status tracking
- Refund processing interface
- Revenue analytics
- Payment method statistics
```

### Technical Implementation Requirements

#### State Management
```typescript
// Global state management
interface AppState {
  user: User | null;
  hotels: Hotel[];
  rooms: Room[];
  reservations: Reservation[];
  bookingFlow: BookingState;
  filters: FilterState;
  loading: boolean;
}

// API queries with React Query
const useHotels = (filters?: HotelFilters) => 
  useQuery(['hotels', filters], () => fetchHotels(filters));

const useRoomAvailability = (hotelId: number, dates: DateRange) =>
  useQuery(['availability', hotelId, dates], () => checkAvailability(hotelId, dates));
```

#### API Integration
```typescript
// API service layer
class HotelAPIService {
  // Authentication
  static login(credentials: LoginRequest): Promise<JwtResponse>
  static register(userData: RegisterRequest): Promise<User>
  
  // Hotels & Rooms
  static getHotels(filters?: HotelFilters): Promise<Hotel[]>
  static getHotelById(id: number): Promise<HotelDetails>
  static getRoomsByHotel(hotelId: number): Promise<Room[]>
  static checkRoomAvailability(roomId: number, dates: DateRange): Promise<boolean>
  
  // Bookings
  static createReservation(booking: ReservationRequest): Promise<ReservationResponse>
  static getUserReservations(): Promise<Reservation[]>
  static getReservationDetails(id: number): Promise<ReservationDetails>
  static cancelReservation(id: number, reason: string): Promise<void>
  
  // Payments
  static processPayment(payment: PaymentRequest): Promise<PaymentResponse>
  static getPaymentStatus(paymentId: string): Promise<PaymentStatus>
  
  // Admin endpoints
  static createHotel(hotel: HotelCreateRequest): Promise<Hotel>
  static updateRoom(id: number, room: RoomUpdateRequest): Promise<Room>
  static getAllBookings(filters: BookingFilters): Promise<Booking[]>
}
```

#### Component Structure
```
src/
├── components/
│   ├── ui/ (shadcn/ui components)
│   ├── common/ (Header, Footer, Layout, SearchWidget)
│   ├── hotels/ (HotelCard, HotelDetails, HotelFilters, HotelGallery)
│   ├── rooms/ (RoomCard, RoomSelector, RoomDetails, AvailabilityCalendar)
│   ├── bookings/ (BookingForm, ReservationCard, BookingWizard, PaymentForm)
│   ├── admin/ (AdminSidebar, MetricsCard, DataTable, AdminCharts)
│   └── auth/ (LoginForm, RegisterForm, ProtectedRoute)
├── pages/
├── hooks/ (custom hooks for API calls, booking flow)
├── services/ (API layer, payment service)
├── types/ (TypeScript interfaces)
├── utils/ (date helpers, currency formatters)
└── stores/ (Zustand/Context stores)
```

### Key Features to Implement

#### Beautiful UI Elements
```typescript
// Hotel cards with luxury feel
<Card className="group hover:shadow-2xl transition-all duration-500 overflow-hidden">
  <div className="relative overflow-hidden">
    <img 
      className="group-hover:scale-110 transition-transform duration-700" 
      src={hotel.imageUrl} 
    />
    <Badge className="absolute top-4 left-4 bg-gold text-black">
      {hotel.starRating} ⭐
    </Badge>
  </div>
  <CardContent className="p-6">
    <h3 className="font-bold text-xl mb-2">{hotel.name}</h3>
    <p className="text-muted-foreground flex items-center gap-2">
      <MapPin className="w-4 h-4" />
      {hotel.location}
    </p>
    <div className="flex justify-between items-center mt-4">
      <div>
        <span className="text-2xl font-bold text-primary">₹{hotel.startingPrice}</span>
        <span className="text-sm text-muted-foreground">/night</span>
      </div>
      <Button variant="default" size="sm">View Rooms</Button>
    </div>
  </CardContent>
</Card>

// Booking wizard with steps
<div className="w-full max-w-4xl mx-auto">
  <div className="flex items-center justify-between mb-8">
    {steps.map((step, index) => (
      <div className="flex items-center">
        <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
          currentStep >= index ? 'bg-primary text-white' : 'bg-muted'
        }`}>
          {index + 1}
        </div>
        <span className="ml-2 text-sm font-medium">{step.title}</span>
      </div>
    ))}
  </div>
  <Card>
    <CardContent className="p-8">
      {renderCurrentStep()}
    </CardContent>
  </Card>
</div>
```

#### Interactive Features
- **Smart Search**: Autocomplete for locations, intelligent date suggestions
- **Availability Calendar**: Real-time room availability with pricing
- **Room Comparison**: Side-by-side room comparison tool
- **Virtual Tours**: 360° room views (placeholder)
- **Instant Booking**: One-click booking for return guests
- **Price Alerts**: Notify when prices drop
- **Multi-step Booking**: Wizard-style reservation process
- **Payment Integration**: Secure payment forms with validation
- **QR Codes**: Digital check-in/out codes

#### Mobile Responsiveness
- Touch-optimized interfaces
- Swipe gestures for image galleries
- Mobile-first booking flow
- Responsive navigation with drawer
- Touch-friendly date pickers
- Optimized for tablet booking experiences

### Sample Request Prompts

**For Landing Page:**
```
Create a luxury hotel booking landing page using React + TypeScript + Tailwind + shadcn/ui. Include:
- Hero section with elegant hotel imagery and prominent booking widget
- Featured hotels carousel with smooth animations
- Advanced search form with location autocomplete, date range picker, guest selector
- Hotel categories grid with hover effects
- Guest testimonials section with star ratings
- Modern typography with luxury feel and premium color scheme
- Make it fully responsive and visually stunning
```

**For Hotel Browse Page:**
```
Create a hotel listing page with sophisticated filtering using React + shadcn/ui:
- Left sidebar with advanced filters (location, price range, star rating, amenities)
- Hotels grid with elegant cards showing images, pricing, ratings, amenities
- Map view toggle with hotel markers
- Search bar with instant results and location suggestions
- Sort dropdown (price, rating, distance, popularity)
- Pagination with smooth transitions
- Loading skeletons and empty states
- Each hotel card should be clickable with hover animations
```

**For Booking Flow:**
```
Create a multi-step hotel booking wizard using React + shadcn/ui:
- Step 1: Room selection with availability calendar
- Step 2: Guest details form with validation
- Step 3: Payment information with secure form
- Step 4: Confirmation with booking summary
- Progress indicator with elegant stepper design
- Form validation with beautiful error states
- Loading states during payment processing
- Success page with booking confirmation and QR code
```

**For Admin Dashboard:**
```
Create a comprehensive hotel admin dashboard using React + shadcn/ui:
- Sidebar navigation with hotel management icons
- Dashboard overview with revenue charts and occupancy metrics (use recharts)
- Hotels and rooms management tables with CRUD operations
- Booking management with status updates and filtering
- Payment processing interface with transaction history
- Modal forms for creating/editing hotels and rooms
- Confirmation dialogs for important actions
- Real-time notifications for new bookings
```

### Advanced UI Components

#### Booking Calendar
```typescript
// Advanced availability calendar
<Calendar 
  mode="range"
  selected={dateRange}
  onSelect={setDateRange}
  className="rounded-md border"
  components={{
    Day: ({ date, ...props }) => (
      <div className="relative">
        <CalendarDay {...props} />
        {getAvailabilityStatus(date) === 'unavailable' && (
          <div className="absolute inset-0 bg-red-500 opacity-20 rounded" />
        )}
        {getRoomPrice(date) && (
          <div className="absolute -bottom-1 left-0 right-0 text-xs text-center">
            ₹{getRoomPrice(date)}
          </div>
        )}
      </div>
    )
  }}
/>
```

#### Room Selector
```typescript
// Interactive room selection with pricing
<div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
  {rooms.map(room => (
    <Card className={`cursor-pointer transition-all ${
      selectedRoom?.id === room.id ? 'ring-2 ring-primary' : ''
    }`} onClick={() => selectRoom(room)}>
      <div className="aspect-video relative overflow-hidden">
        <img src={room.images[0]} className="object-cover w-full h-full" />
        <Badge className="absolute top-2 right-2">
          {room.occupancy} guests
        </Badge>
      </div>
      <CardContent className="p-4">
        <h3 className="font-semibold text-lg">{room.name}</h3>
        <p className="text-sm text-muted-foreground">{room.description}</p>
        <div className="flex justify-between items-center mt-3">
          <div className="flex gap-2">
            {room.amenities.slice(0, 3).map(amenity => (
              <Badge variant="outline" key={amenity}>
                {amenity}
              </Badge>
            ))}
          </div>
          <div className="text-right">
            <div className="text-2xl font-bold">₹{room.pricePerNight}</div>
            <div className="text-xs text-muted-foreground">per night</div>
          </div>
        </div>
      </CardContent>
    </Card>
  ))}
</div>
```

### Development Tips
1. **Start with**: "Create a [specific page] for Hotel Reservation System using React, TypeScript, Tailwind CSS, and shadcn/ui components"
2. **Request luxury design**: Always mention premium/luxury hotel aesthetic
3. **Include booking flow**: Multi-step wizards for complex reservations
4. **Request real-time features**: Availability updates, pricing changes
5. **Mobile-first**: Emphasize responsive design for mobile bookings
6. **Payment UI**: Request secure, professional payment interfaces
7. **Admin features**: Comprehensive dashboard for hotel management

### Success Criteria
- ✅ Elegant, luxury hotel booking website
- ✅ Fully responsive across all devices
- ✅ Smooth booking flow with payment integration
- ✅ Real-time room availability checking
- ✅ Role-based UI (Guest vs Admin views)
- ✅ Comprehensive admin dashboard
- ✅ Mobile-optimized for on-the-go bookings
- ✅ Professional payment processing UI
- ✅ Accessible and user-friendly interface
- ✅ Premium visual design matching hospitality standards
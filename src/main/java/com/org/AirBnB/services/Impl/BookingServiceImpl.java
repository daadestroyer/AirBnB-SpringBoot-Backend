package com.org.AirBnB.services.Impl;

import com.org.AirBnB.dto.BookingDTO;
import com.org.AirBnB.dto.BookingRequest;
import com.org.AirBnB.dto.GuestDTO;
import com.org.AirBnB.entities.*;
import com.org.AirBnB.entities.enums.BookingStatus;
import com.org.AirBnB.exception.customexceptions.*;
import com.org.AirBnB.repository.BookingRepository;
import com.org.AirBnB.repository.HotelRepository;
import com.org.AirBnB.repository.InventoryRepository;
import com.org.AirBnB.repository.RoomRepository;
import com.org.AirBnB.services.BookingService;
import com.org.AirBnB.services.CheckoutService;
import com.org.AirBnB.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    @Autowired
    private GuestRepository guestRepository;
    private final PricingService pricingService;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;

    @Value("${frontEnd.url}")
    private String frontEndUrl;

    @Override
    @Transactional
    public BookingDTO createMyBooking(BookingRequest bookingRequest) {

        // 1. Fetch the hotel first
        Hotel hotel = hotelRepository
                .findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id = " + bookingRequest.getHotelId() + " not found"));

        // 2. Fetch the rooms
        Room room = roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room with id = " + bookingRequest.getRoomId() + " not found"));

        // 3. Check if inventory is available in user provided checkInDate and checkOutDate
        List<Inventory> lockedAvailableInventory = inventoryRepository
                .findAndLockAvailableInventory(
                        bookingRequest.getRoomId(),
                        bookingRequest.getCheckInDate(),
                        bookingRequest.getCheckOutDate(),
                        bookingRequest.getRoomsCount()
                );

        // 4. check if room count is available in particular date range
        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (lockedAvailableInventory.size() != daysCount) {
            throw new InventoryNotAvailableException("Room is not available with your provided date range");
        }

        // 5. Reserve room for 3 minutes, so no other person can book also hold the price for 3 minutes
        for (Inventory inventory : lockedAvailableInventory) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(lockedAvailableInventory);

        // 6. Create the booking


        User user = getLoggedInUser();
        // calculate dynamic price
        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(lockedAvailableInventory);
        log.info("Price for one room is {}",priceForOneRoom);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));
        Booking booking = Booking
                .builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(user)
                .roomCount(bookingRequest.getRoomsCount())
                .amount(totalPrice).build();

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDTO.class);
    }

    @Override
    public String deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
        return "Booking " + bookingId + " deleted...";
    }

    @Override
    @Transactional
    public BookingDTO addGuestToBooking(Long bookingId, List<GuestDTO> guestDTOList) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id = " + bookingId + " not found"));

        // can not proceed if booking expired
        if (isBookingExpired(booking)) {
            throw new BookingExpiredException("Booking has expired");
        }
        // can not proceed if booking is not in reserved state
        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new BookingAtIllegalState("Booking is not under reserved state, can not add guest");
        }

        // check if this booking belong to the user or not who is going to add guest in his booking
        User loggedInUser = getLoggedInUser();
        if (!loggedInUser.equals(booking.getUser())) {
            throw new UnAuthorizedException("Booking " + bookingId + " does not belong to this user " + loggedInUser.getUsername());
        }
        // adding guest
        for (GuestDTO guestDTO : guestDTOList) {
            // adding guest in guest table
            Guest guest = modelMapper.map(guestDTO, Guest.class);

            // TODO : Remove dummy user
            // to add guest we need to add user for which guest belongs
            guest.setUser(loggedInUser);
            guest = guestRepository.save(guest);

            booking.getGuestList().add(guest);
        }
        // update booking status to guest added
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);


        return modelMapper.map(booking, BookingDTO.class);
    }




    public User getLoggedInUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isBookingExpired(Booking booking) {
        // to check if booking is expired or not get the created date of booking and add three minutes to it
        // we are waiting for three minutes and holding the hotel
        return booking.getCreatedAt().plusMinutes(20).isBefore(LocalDateTime.now());
    }

    /**
     * Runs every minute and releases reserved rooms for bookings that were RESERVED
     * and created more than 3 minutes ago.
     */
    @Scheduled(fixedRate = 60_000) // every minute
    @Transactional
    public void releaseExpiredBookings() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(3);
        log.debug("Running releaseExpiredBookings, threshold={}", threshold);

        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedBefore(BookingStatus.RESERVED, threshold);

        if (expiredBookings.isEmpty()) {
            log.debug("No expired bookings found.");
            return;
        }

        for (Booking booking : expiredBookings) {
            try {
                releaseReservedInventory(booking);
                booking.setBookingStatus(BookingStatus.EXPIRED);
            } catch (Exception ex) {
                // log and continue with other bookings to avoid blocking the whole job
                log.error("Failed to release inventory for booking id={} : {}", booking.getBookingId(), ex.getMessage(), ex);
            }
        }

        bookingRepository.saveAll(expiredBookings);
        log.info("Released inventory and updated status to EXPIRED for {} bookings", expiredBookings.size());
    }

    /**
     * Resets reservedCount to 0 for inventory entries belonging to the booking's room
     * between checkIn and checkOut (inclusive).
     */
    private void releaseReservedInventory(Booking booking) {
        if (booking == null) return;

        if (booking.getRoom() == null || booking.getRoom().getRoomId() == null) {
            log.warn("Booking id={} has no room information; skipping inventory release", booking.getBookingId());
            return;
        }
        if (booking.getCheckInDate() == null || booking.getCheckOutDate() == null) {
            log.warn("Booking id={} missing check-in/check-out dates; skipping inventory release", booking.getBookingId());
            return;
        }

        Long roomId = booking.getRoom().getRoomId();
        LocalDate checkIn = booking.getCheckInDate();
        LocalDate checkOut = booking.getCheckOutDate();

        List<Inventory> inventories = inventoryRepository.findByRoomIdAndDateBetween(roomId, checkIn, checkOut);
        if (inventories.isEmpty()) {
            log.debug("No inventory rows found for booking id={}, roomId={}, dateRange {} - {}", booking.getBookingId(), roomId, checkIn, checkOut);
            return;
        }

        for (Inventory inv : inventories) {
            // Option A: set reservedCount to 0 (requested)
            // inv.setReservedCount(0);
            // Option B (alternative): if you want to subtract the booking's reserved amount instead of zeroing,
            // you could do:
            inv.setReservedCount(Math.max(0, inv.getReservedCount() - booking.getRoomCount()));
        }

        inventoryRepository.saveAll(inventories);
        log.debug("Released reserved inventory for booking id={}, updated {} inventory rows", booking.getBookingId(), inventories.size());
    }

    @Transactional
    @Override
    public String initiatePayment(Long bookingId) throws StripeException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getLoggedInUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getUserId());
        }
        if (isBookingExpired(booking)) {
            throw new BookingExpiredException("Booking has already expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking,
                frontEndUrl+"/payments/" +bookingId +"/status",
                frontEndUrl+"/payments/" +bookingId +"/status");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        // so checkout.session.complete is a session, we are checking if payment is completed using this event
        log.info("checking even type");
        //if ("checkout.session.completed".equals(event.getType()) || "charge.updated".equalsIgnoreCase(event.getType())) {
            log.info("event type checked");
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            // getting the sessionId
            String sessionId = session.getId();


            // using sessionId we are getting the booking
            Booking booking = bookingRepository.findByStripePaymentSessionId(sessionId).orElseThrow(() ->
                    new ResourceNotFoundException("Booking not found for session ID: " + sessionId));

            // confirm and save the booking
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            log.info("Booking Confirmed");
            bookingRepository.save(booking);

            // finding the inventory and locking it
            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getRoomId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomCount());


            // confirming the booking
            inventoryRepository.confirmBooking(booking.getRoom().getRoomId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomCount());

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getBookingId());
//        }
//        else {
//            log.warn("Unhandled event type: {}", event.getType());
//        }
    }

    @Override
    @Transactional
    public String cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getLoggedInUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getUserId());
        }

        // if booking is in confirmed state then only we can cancel
        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // first acquire the lock on reserve inventory
        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getRoomId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomCount());

        // then cancel the booking
        inventoryRepository.cancelBooking(booking.getRoom().getRoomId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomCount());

        // handle the refund
        try {
            Session session = Session.retrieve(booking.getStripePaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
            return "Booking "+bookingId+" cancelled";
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BookingStatus getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getLoggedInUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getUserId());
        }
        return booking.getBookingStatus();
    }
}

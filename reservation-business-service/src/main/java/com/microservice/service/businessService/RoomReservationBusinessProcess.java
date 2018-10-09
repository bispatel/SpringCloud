package com.microservice.service.businessService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.service.businessService.client.GuestService;
import com.microservice.service.businessService.client.ReservationService;
import com.microservice.service.businessService.client.RoomService;
import com.microservice.service.businessService.domain.Guest;
import com.microservice.service.businessService.domain.Reservation;
import com.microservice.service.businessService.domain.Room;
import com.microservice.service.businessService.domain.RoomReservation;

/**
 * Created by frankmoley on 5/23/17.
 */
@Service
public class RoomReservationBusinessProcess {

    private GuestService guestService;
    private RoomService roomService;
    private ReservationService reservationService;

    @Autowired
    public RoomReservationBusinessProcess(GuestService guestService, RoomService roomService, ReservationService reservationService) {
        this.guestService = guestService;
        this.roomService = roomService;
        this.reservationService = reservationService;
    }

    public List<RoomReservation> getRoomReservationsForDate(String dateString){
        List<Room> rooms = this.roomService.findAll(null);
        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();
        rooms.forEach(room->{
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getId());
            roomReservation.setRoomName(room.getName());
            roomReservation.setRoomNumber(room.getRoomNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });
        Iterable<Reservation> reservations = this.reservationService.findAll(dateString);
        if(null!=reservations){
            reservations.forEach(reservation -> {
                Guest guest = this.guestService.findOne(reservation.getGuestId());
                if(null!=guest) {
                    RoomReservation roomReservation = roomReservationMap.get(reservation.getRoomId());
                    roomReservation.setDate(reservation.getReservationDate());
                    roomReservation.setFirstName(guest.getFirstName());
                    roomReservation.setLastName(guest.getLastName());
                    roomReservation.setGuestId(guest.getId());
                }

            });
        }
        List<RoomReservation> roomReservations = new ArrayList<>();
        for(Long roomId:roomReservationMap.keySet()){
            roomReservations.add(roomReservationMap.get(roomId));
        }
        return roomReservations;
    }
}

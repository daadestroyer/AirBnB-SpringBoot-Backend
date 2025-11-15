package com.org.AirBnB.repository;

import com.org.AirBnB.entities.Hotel;
import com.org.AirBnB.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByHotelOwner(User user);
}

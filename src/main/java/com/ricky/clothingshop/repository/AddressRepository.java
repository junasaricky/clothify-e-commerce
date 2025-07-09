package com.ricky.clothingshop.repository;

import com.ricky.clothingshop.model.Address;
import com.ricky.clothingshop.model.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
    Optional<Address> findFirstByUser(User user);
}

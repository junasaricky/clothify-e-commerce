package com.ricky.clothingshop.service;

import org.springframework.stereotype.Service;

import com.ricky.clothingshop.model.Address;
import com.ricky.clothingshop.model.User;
import com.ricky.clothingshop.repository.AddressRepository;
import com.ricky.clothingshop.repository.UserRepository;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepo;
    private final UserRepository userRepo;

    public AddressService(UserRepository userRepo, AddressRepository addressRepo) {
        this.addressRepo = addressRepo;
        this.userRepo = userRepo;
    }

    public List<Address> getUserAddresses(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        return addressRepo.findByUser(user);
    }
    
    public Address getAddressById(String username, Long id) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Address address = addressRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        if (!address.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized access to address");
        }

        return address;
    }

    public Address addAddress(String username, Address address) {
        User user = userRepo.findByUsername(username).orElseThrow();
        address.setUser(user);
        return addressRepo.save(address);
    }

    public void deleteAddress(Long id) {
        addressRepo.deleteById(id);
    }
    
    public Address updateAddress(Long id, Address updatedAddress) {
        Address existing = addressRepo.findById(id).orElseThrow();
        existing.setFullName(updatedAddress.getFullName());
        existing.setPhoneNumber(updatedAddress.getPhoneNumber());
        existing.setStreet(updatedAddress.getStreet());
        existing.setCity(updatedAddress.getCity());
        existing.setProvince(updatedAddress.getProvince());
        existing.setZipCode(updatedAddress.getZipCode());
        existing.setCountry(updatedAddress.getCountry());
        return addressRepo.save(existing);
    }

}

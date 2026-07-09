package com.example.orm_exercise.controllers;

import com.example.orm_exercise.models.Address;
import com.example.orm_exercise.models.Contact;
import com.example.orm_exercise.repositories.AddressRepository;
import com.example.orm_exercise.repositories.ContactRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    public ContactController(ContactRepository contactRepository,
                             AddressRepository addressRepository) {
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
    }

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable int id) {
        return contactRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact) {

        if (contact.getAddresses() != null) {
            for (Address address : contact.getAddresses()) {
                address.setContact(contact);
            }
        }

        return contactRepository.save(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable int id,
                                 @RequestBody Contact updatedContact) {

        return contactRepository.findById(id).map(contact -> {

            contact.setName(updatedContact.getName());
            contact.setEmail(updatedContact.getEmail());
            contact.setPhoneNumber(updatedContact.getPhoneNumber());

            return contactRepository.save(contact);

        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable int id) {
        contactRepository.deleteById(id);
    }

    // BONUS: Add Address

    @PostMapping("/{contactId}/addresses")
    public Contact addAddress(@PathVariable int contactId,
                              @RequestBody Address address) {

        Contact contact = contactRepository.findById(contactId).orElse(null);

        if (contact == null) {
            return null;
        }

        address.setContact(contact);

        contact.getAddresses().add(address);

        return contactRepository.save(contact);
    }

    // BONUS: Delete Address

    @DeleteMapping("/{contactId}/addresses/{addressId}")
    public Contact deleteAddress(@PathVariable int contactId,
                                 @PathVariable int addressId) {

        Contact contact = contactRepository.findById(contactId).orElse(null);

        if (contact == null) {
            return null;
        }

        Address address = addressRepository.findById(addressId).orElse(null);

        if (address == null) {
            return null;
        }

        contact.getAddresses().remove(address);

        addressRepository.delete(address);

        return contactRepository.save(contact);
    }
}
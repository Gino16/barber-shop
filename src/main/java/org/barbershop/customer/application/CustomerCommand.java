package org.barbershop.customer.application;

public record CustomerCommand(String name, String phone, String email, String address) {
}

package org.barbershop.item.application;

import org.barbershop.item.domain.Item;

public record ItemCommand(String name, String description, Item.Category category, Boolean active) {

}

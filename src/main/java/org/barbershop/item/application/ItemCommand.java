package org.barbershop.item.application;

import java.math.BigDecimal;
import org.barbershop.item.domain.Item;

public record ItemCommand(String name, String description, Item.Category category,
                          BigDecimal price, Boolean active) {

}

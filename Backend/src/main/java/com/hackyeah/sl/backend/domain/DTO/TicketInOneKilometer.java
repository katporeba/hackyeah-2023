package com.hackyeah.sl.backend.domain.DTO;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class TicketInOneKilometer {
    Double targetLatitude;
    Double targetLongitude;
}

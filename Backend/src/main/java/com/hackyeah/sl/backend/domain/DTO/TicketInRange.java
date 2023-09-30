package com.hackyeah.sl.backend.domain.DTO;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class TicketInRange {
    Double latitude;
    Double longitude;
    Long radius;
}

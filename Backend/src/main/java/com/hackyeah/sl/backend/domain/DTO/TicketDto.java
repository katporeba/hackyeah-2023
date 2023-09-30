package com.hackyeah.sl.backend.domain.DTO;

import com.hackyeah.sl.backend.domain.Position;
import com.hackyeah.sl.backend.domain.PositionDto;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.hackyeah.sl.backend.domain.Ticket}
 */
@Value
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor(force = true)
public class TicketDto implements Serializable {
    String ticketId;
    String category;
    Integer count;
    boolean aggressive;
    String comment;
    Date expirationDate;
    String ticketImageUrl;
    Long latitude;
    Long longitude;
}
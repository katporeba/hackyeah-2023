package com.hackyeah.sl.backend.domain.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.hackyeah.sl.backend.domain.Ticket}
 */
@Value
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor(force = true)
public class TicketDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String ticketId;
    String category;
    Integer count;
    boolean aggressive;
    String comment;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    LocalDateTime expirationDate;
    String ticketImageUrl;
    Long latitude;
    Long longitude;
}
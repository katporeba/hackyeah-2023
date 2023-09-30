package com.hackyeah.sl.backend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String ticketId;

    private String category;

    private Integer count;

    private boolean aggressive;

    private String comment;

    private LocalDateTime expirationDate;

    private String ticketImageUrl;

    @Column(columnDefinition = "DOUBLE PRECISION")
    private Double latitude;
    @Column(columnDefinition = "DOUBLE PRECISION")
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

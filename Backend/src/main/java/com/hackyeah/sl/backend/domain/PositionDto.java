package com.hackyeah.sl.backend.domain;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Position}
 */
@Value
public class PositionDto implements Serializable {
    Long latitude;
    Long longitude;
}
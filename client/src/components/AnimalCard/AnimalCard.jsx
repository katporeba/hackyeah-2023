import React from "react";
import "./AnimalCard.scss";
import { Box, Typography } from "@mui/material";
import cat from "../../assets/cat.svg";

function AnimalCard() {
  return (
    <Box className="animal-card-container">
      <Box className="animal-details">
        <Box className="animal-details-image">
          <img src={cat} alt="cat" />
        </Box>
        <Box className="animal-details-text">
          <Typography className="animal-details-text-title">Kot</Typography>
          <Typography className="animal-details-text-content">Kot ma różową obrożę oraz pomarańczowe futro.</Typography>
        </Box>
      </Box>
      <Box className="animal-location">
        <Typography className="animal-location-text">Lokalizacja</Typography>
      </Box>
    </Box>
  );
}

export default AnimalCard;

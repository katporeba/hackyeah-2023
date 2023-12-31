import "./Reports.scss";
import { Box, Typography } from "@mui/material";
import React from "react";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import AnimalCard from "../../components/AnimalCard/AnimalCard";

function Reports() {
  return (
    <>
      <div className="navbox">
        <div className="navbox-go-back">
          <ArrowBackIcon />
        </div>
      </div>
      <Box className="reports-container">
        <Typography className="reports-title">Aktywne ogłoszenia</Typography>
        <Box className="reports-cards">
          <AnimalCard />
          <AnimalCard />
          <AnimalCard />
          <AnimalCard />
        </Box>
      </Box>
    </>
  );
}

export default Reports;

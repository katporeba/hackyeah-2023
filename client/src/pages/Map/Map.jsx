/* eslint-disable no-undef */
/* eslint-disable no-use-before-define */
import "./Map.scss";
import React, { useEffect, useState } from "react";
import { Box, Button } from "@mui/material";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import MenuIcon from "@mui/icons-material/Menu";
import LocationSearchingIcon from "@mui/icons-material/LocationSearching";
import logo from "../../assets/logo.png";

export function Map() {
  const [latitude, setLatitude] = useState();
  const [longitude, setLongitude] = useState();

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(success, error);
  } else {
    console.log("Geolocation not supported");
  }

  function success(position) {
    const { latitude: latitudeTmp } = position.coords;
    const { longitude: longitudeTmp } = position.coords;
    setLatitude(latitudeTmp);
    setLongitude(longitudeTmp);
  }

  function error() {
    console.log("Unable to retrieve your location");
  }

  useEffect(() => {
    if (latitude && longitude) {
      const { map } = document.getElementById("map1");
      L.marker([latitude, longitude]).addTo(map);
    }
  }, [latitude, longitude]);

  return (
    <>
      {latitude && longitude && (
        <imgp-map
          id="map1"
          zoom="10"
          center={`[${latitude}, ${longitude}]`}
          epsg="2180:G2"
          basemap="G2:MOBILE_TOPO_I"
        />
      )}
      <div className="navbox-container">
        <div className="navbox-go-back">
          <MenuIcon />
        </div>
        <img src={logo} alt="logo" />
      </div>
      <Box className="navigate-locator">
        <LocationSearchingIcon />
      </Box>
      <Box className="add-post">
        <div className="title-add-post">Na twojej drodze znajduje się zwierzę?</div>
        <div className="button-container-add-post">
          <Button className="button-add-post" endIcon={<ArrowForwardIcon />}>
            Dodaj ogłoszenie
          </Button>
        </div>
      </Box>
      <Box className="local-circle" />
      <Box className="map-container" />
    </>
  );
}

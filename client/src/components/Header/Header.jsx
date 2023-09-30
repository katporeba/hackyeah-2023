import "./Header.scss";
import { Box, Typography } from "@mui/material";
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import AuthService from "../../services/authService";

export function Header({ selected = "homepage", theme = "light" }) {
  const [user, setUser] = useState("");
  useEffect(() => {
    setUser(AuthService.getCurrentUser());
  }, []);

  return (
    <Box className="header-container">
      <Box className={`logo-container ${theme}`}>
        <Typography className="name">
          <Link to="/">
            <img src="/images/logo.svg" alt="logo" />
          </Link>
        </Typography>
      </Box>
      <Box className="links-container">
        {user ? (
          <Link className={`${selected === "account" ? "selected" : ""}`} to="/account">
            account
          </Link>
        ) : (
          <Link className={`${selected === "signUp" ? "selected" : ""}`} to="/sign-up">
            sign up
          </Link>
        )}
      </Box>
    </Box>
  );
}

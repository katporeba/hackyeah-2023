import "./SignIn.scss";
import React, { useState, useEffect } from "react";
import { Button, TextField, Typography, Box, InputAdornment, IconButton } from "@mui/material";
import CircularProgress from "@mui/material/CircularProgress";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { Link, Navigate, useNavigate } from "react-router-dom";
import AuthService from "../../services/authService";
import { isEmail, isPasswordStrong, markValidation } from "../../common/validateUtils/validateSignUp";
import googleIcon from "../../assets/google_logo.png";
import facebookIcon from "../../assets/facebook_logo.png";
import appleIcon from "../../assets/apple_logo.png";

export function SignIn() {
  const [post, setPost] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [validatePassword, setValidatePassword] = useState(false);
  const [validateEmail, setValidateEmail] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const handleClickShowPassword = () => setShowPassword(!showPassword);
  const handleMouseDownPassword = () => setShowPassword(!showPassword);
  const navigate = useNavigate();

  useEffect(() => {
    if (AuthService.getCurrentUser()) {
      return navigate("/account");
    }
  }, []);

  const onSubmitSendData = async () => {
    if (validateEmail && validatePassword) {
      setError(null);
      setLoading(true);
      const returnedData = await AuthService.login(email, password, setError);
      setPost(returnedData);
      setLoading(false);
    } else {
      setError("wrong data");
    }
  };

  const onChangeInput = (value, setFun) => {
    setFun(value);
  };

  return (
    // add navigation to homepage
    <>
      <div className="navbox" />
      <Box className="signup-container">
        <Box className="signup-content-right">
          <Typography className="header">Logowanie</Typography>
          <Box className="signup-inputs">
            <Typography className="input-description">Email</Typography>
            <TextField
              className={`input-field ${!validateEmail ? "error" : "success"}`}
              id="email-input"
              placeholder="Podaj e-mail"
              variant="outlined"
              value={email}
              type="email"
              onChange={(e) => {
                markValidation(isEmail(e.target.value), setValidateEmail);
                onChangeInput(e.target.value, setEmail);
              }}
              sx={{
                "& fieldset": {
                  borderRadius: "50px",
                  border: "none",
                },
                input: {
                  "&::placeholder": {
                    padding: "0 .5rem",
                  },
                },
              }}
            />
            <Typography className="input-description">Hasło</Typography>
            <TextField
              className={`input-field ${!validatePassword ? "error" : "success"}`}
              id="password-input"
              placeholder="Podaj hasło"
              variant="outlined"
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={(e) => {
                markValidation(isPasswordStrong(e.target.value), setValidatePassword);
                onChangeInput(e.target.value, setPassword);
              }}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowPassword}
                      onMouseDown={handleMouseDownPassword}
                      className="toggle-visibility-icon"
                    >
                      {showPassword ? <VisibilityIcon /> : <VisibilityOffIcon />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{
                "& fieldset": {
                  borderRadius: "50px",
                  border: "none",
                },
                input: {
                  "&::placeholder": {
                    padding: "0 .5rem",
                  },
                },
              }}
            />
            {error && (
              <Typography className="input-description error-message">{error.response.data.message}</Typography>
            )}
            {post && <Navigate to="/statistics" replace />}
          </Box>
          <Box className="divider" />
          <Typography className="or-signin-with">Lub zaloguj się z</Typography>
          <Box className="social-media-buttons">
            <Button variant="outlined" className="social-media-button">
              <img src={googleIcon} alt="google-icon" />
            </Button>
            <Button variant="outlined" className="social-media-button">
              <img src={facebookIcon} alt="facebook-icon" />
            </Button>
            <Button variant="outlined" className="social-media-button">
              <img src={appleIcon} alt="apple-icon" />
            </Button>
          </Box>
          <Box className="signup-buttons">
            {loading ? (
              <CircularProgress className="progress-circular" />
            ) : (
              <Button
                variant="contained"
                onClick={() => onSubmitSendData()}
                disabled={!(validateEmail && validatePassword)}
                className="signup-button"
              >
                Zaloguj się
              </Button>
            )}
            <Typography className="switch-site">
              Nie masz jeszcze konta?{" "}
              <Link className="switch-site-link" to="/sign-up">
                Zarejestruj się
              </Link>
            </Typography>
          </Box>
        </Box>
      </Box>
    </>
  );
}

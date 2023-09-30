import "./SignIn.scss";
import React, { useState, useEffect } from "react";
import { Button, TextField, Typography, Box, InputAdornment, IconButton } from "@mui/material";
import CircularProgress from "@mui/material/CircularProgress";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { Link, Navigate, useNavigate } from "react-router-dom";
import Header from "../../components/Header";
import AuthService from "../../services/authService";
import { isEmail, isPasswordStrong, markValidation } from "../../common/validateUtils/validateSignUp";

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
    <>
      <Header selected="signUp" theme="light" />
      <Box className="signup-container">
        <Box className="signup-content-left" />
        <Box className="signup-content-right">
          <Typography className="header">Zaloguj się</Typography>
          <Box className="signup-inputs">
            <Typography className="input-description">Email</Typography>
            <TextField
              className={!validateEmail ? "error" : "success"}
              id="email-input"
              placeholder="Podaj e-mail"
              variant="outlined"
              value={email}
              type="email"
              onChange={(e) => {
                markValidation(isEmail(e.target.value), setValidateEmail);
                onChangeInput(e.target.value, setEmail);
              }}
            />
            <Typography className="input-description">Hasło</Typography>
            <TextField
              className={!validatePassword ? "error" : "success"}
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
            />
            {error && (
              <Typography className="input-description error-message">{error.response.data.message}</Typography>
            )}
            {post && <Navigate to="/statistics" replace />}
          </Box>
          <Box className="signup-buttons">
            {loading ? (
              <CircularProgress className="progress-circular" />
            ) : (
              <Button onClick={() => onSubmitSendData()} disabled={!(validateEmail && validatePassword)}>
                Zaloguj się
              </Button>
            )}
            <Typography className="switch-site">
              Nie masz jeszcze konta? <Link to="/sign-up">Zarejestruj się</Link>
            </Typography>
          </Box>
        </Box>
      </Box>
    </>
  );
}

import "./SignUp.scss";
import React, { useState, useEffect } from "react";
import { Button, TextField, Typography, Box, InputAdornment, IconButton, CircularProgress } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import Header from "../../components/Header";
import AuthService from "../../services/authService";
import {
  isEmail,
  isPasswordStrong,
  markValidation,
  markValidationPassword,
} from "../../common/validateUtils/validateSignUp";

export function SignUp() {
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState({
    email: "",
    name: "",
    password: "",
    passwordValidate: "",
  });
  const [validateEmail, setValidateEmail] = useState(false);
  const [validatePassword, setValidatePassword] = useState(false);
  const [validateName, setValidateName] = useState(false);
  const [validatePasswordStrong, setValidatePasswordStrong] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showPasswordSecond, setShowPasswordSecond] = useState(false);
  const handleClickShowPassword = (setPassword, show) => setPassword(!show);
  const handleMouseDownPassword = (setPassword, show) => setPassword(!show);
  const navigate = useNavigate();

  const onSubmitSendData = async () => {
    setError(null);
    setLoading(true);
    await AuthService.register(data.email, data.password, data.name, setError);
    setLoading(false);
    if (!error) {
      return navigate("/sign-in");
    }
  };

  const onChangeAddToState = (key, value) => {
    setData({ ...data, [key]: value });
  };

  useEffect(() => {
    if (AuthService.getCurrentUser()) {
      return navigate("/account");
    }
  }, []);

  return (
    <>
      <Header selected="signUp" theme="light" />
      <Box className="signup-container">
        <Box className="form-container">
          <Typography className="header">Rejestracja</Typography>
          <Box className="signup-inputs">
            <Typography className="input-description">Email</Typography>
            <TextField
              id="email-input"
              className={!validateEmail ? "error" : "success"}
              placeholder="Podaj e-mail"
              variant="outlined"
              onChange={(e) => {
                markValidation(isEmail(e.target.value), setValidateEmail);
                onChangeAddToState("email", e.target.value);
              }}
            />
            <Typography className="input-description">Hasło</Typography>
            <TextField
              id="password-input"
              className={!validatePasswordStrong ? "error" : "success"}
              placeholder="Podaj hasło"
              variant="outlined"
              onChange={(e) => {
                markValidation(isPasswordStrong(e.target.value), setValidatePasswordStrong);
                onChangeAddToState("password", e.target.value);
              }}
              type={showPassword ? "text" : "password"}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={() => handleClickShowPassword(setShowPassword, showPassword)}
                      onMouseDown={() => handleMouseDownPassword(setShowPassword, showPassword)}
                      className="toggle-visibility-icon"
                    >
                      {showPassword ? <VisibilityIcon /> : <VisibilityOffIcon />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
            {data.password && (
              <>
                <Typography className="input-description">Powtórz hasło</Typography>
                <TextField
                  id="password-input-validate"
                  className={!validatePassword ? "error" : "success"}
                  placeholder="Powtórz swoje hasło"
                  variant="outlined"
                  onChange={(e) => {
                    markValidationPassword(data.password, e.target.value, setValidatePassword);
                    onChangeAddToState("passwordValidate", e.target.value);
                  }}
                  type={showPasswordSecond ? "text" : "password"}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          aria-label="toggle password visibility"
                          onClick={() => handleClickShowPassword(setShowPasswordSecond, showPasswordSecond)}
                          onMouseDown={() => handleMouseDownPassword(setShowPasswordSecond, showPasswordSecond)}
                          className="toggle-visibility-icon"
                        >
                          {showPasswordSecond ? <VisibilityIcon /> : <VisibilityOffIcon />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </>
            )}
          </Box>
          {error && <Typography className="input-description error-message">{error.response.data.message}</Typography>}
          <Box className="signup-buttons">
            {loading ? (
              <CircularProgress className="progress-circular" />
            ) : (
              <Button
                onClick={() => onSubmitSendData()}
                disabled={!(validateEmail && validatePassword && validateName && validatePasswordStrong)}
              >
                Zarejestruj
              </Button>
            )}
            <Typography className="switch-site">
              Posiadasz juz konto? <Link to="/sign-in">Zaloguj się</Link>
            </Typography>
          </Box>
        </Box>
      </Box>
    </>
  );
}

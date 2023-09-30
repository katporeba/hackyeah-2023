import axios from "axios";

const API_URL = "/api/auth/";

const register = async (email, password, name, setError) =>
  axios
    .post(`${API_URL}signup`, {
      email,
      password,
      name,
      role: ["user"],
    })
    .catch((error) => {
      setError(error);
    });

const login = async (email, password, setError) =>
  axios
    .post(`${API_URL}signin`, {
      email,
      password,
    })
    .then((response) => {
      if (response.data.accessToken) {
        localStorage.setItem("user", JSON.stringify(response.data));
      }
      return response.data;
    })
    .catch((error) => {
      setError(error);
    });

const logout = () => {
  localStorage.removeItem("user");
};

const getCurrentUser = () => {
  if (localStorage.getItem("user")) {
    return JSON.parse(localStorage.getItem("user"));
  }
  return null;
};

const AuthService = {
  register,
  login,
  logout,
  getCurrentUser,
};

export default AuthService;

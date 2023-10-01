import "./App.scss";
import React from "react";
import { Routes, Route } from "react-router-dom";
import SignUp from "./pages/SignUp";
import SignIn from "./pages/SignIn";
import Reports from "./pages/Reports/Reports";
import Map from "./pages/Map";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<Map />} />
        <Route path="/sign-up" element={<SignUp />} />
        <Route path="/sign-in" element={<SignIn />} />
        <Route path="/reports" element={<Reports />} />
      </Routes>
    </div>
  );
}

export default App;

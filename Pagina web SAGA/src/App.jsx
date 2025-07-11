import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Partidos from "./pages/Partidos";
import Arbitros from "./pages/Arbitros";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/partidos" element={<Partidos />} />
        <Route path="/arbitros" element={<Arbitros />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

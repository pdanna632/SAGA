import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Partidos from "./pages/Partidos";
import Arbitros from "./pages/Arbitros";
import Asignacion from "./pages/Asignacion";
import Modificaaciones from "./pages/Modificaciones";
import Reportes from "./pages/Reportes";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/partidos" element={<Partidos />} />
        <Route path="/arbitros" element={<Arbitros />} />
        <Route path="/asignacion" element={<Asignacion />} />
        <Route path="/modificaciones" element={<Modificaaciones />} />
        <Route path="/reportes" element={<Reportes />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

import { Link } from "react-router-dom";

function Navbar() {
  return (
    <nav className="navbar">
      <ul>
        <li><Link to="/dashboard">Inicio</Link></li>
        <li><Link to="/otra-opcion">Otra opción</Link></li>
        <li><Link to="/">Cerrar sesión</Link></li>
      </ul>
    </nav>
  );
}

export default Navbar;

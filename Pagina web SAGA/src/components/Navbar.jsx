import { Link } from "react-router-dom";
import styles from "../styles/Navbar.module.css";

function Navbar() {
  return (
    <nav className={styles.navbar}>
      <ul className={styles.menu}>
        <li><Link className={styles.link} to="/dashboard">Inicio</Link></li>
        <li><Link className={styles.link} to="/partidos">Partidos</Link></li>
        <li><Link className={styles.link} to="/arbitros">Arbitros</Link></li>
        <li><Link className={styles.link} to="/asignacion">Asignación</Link></li>
        <li><Link className={styles.link} to="/modificaciones">Modificaciones</Link></li>
        <li><Link className={styles.link} to="/reportes">Reportes</Link></li>
        <li><Link className={styles.link} to="/">Cerrar sesión</Link></li>
      </ul>
    </nav>
  );
}

export default Navbar;

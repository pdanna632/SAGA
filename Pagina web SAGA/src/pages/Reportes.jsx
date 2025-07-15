import Navbar from "../components/Navbar";
import styles from "../styles/Reportes.module.css";

function Reporte() {
  return (
    <div className={styles.container}>
      <Navbar />
      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Reporte de Asignaciones</h2>
        </div>
      </main>
    </div>
  );
}

export default Reporte;

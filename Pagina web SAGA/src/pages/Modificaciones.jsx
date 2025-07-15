import Navbar from "../components/Navbar";
import styles from "../styles/Modificaciones.module.css";

function Modificaciones() {
  return (
    <div className={styles.container}>
      <Navbar />
      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Modificación de Partidos</h2>
        </div>
      </main>
    </div>
  );
}

export default Modificaciones;

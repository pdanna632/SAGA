import Navbar from "../components/Navbar";
import styles from "../styles/Partidos.module.css";

function Partidos() {
  return (
    <div className={styles.container}>
      <Navbar />

      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Partidos disponibles</h2>
        </div>
      </main>
    </div>
  );
}

export default Partidos;

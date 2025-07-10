import styles from '../styles/Dashboard.module.css';
import Navbar from '../components/Navbar';

function Dashboard() {
  return (
    <div className={styles.container}>
      <Navbar /> {/* ✅ Tu componente de menú superior */}

      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Bienvenido a SAGA</h2>
          <p>Selecciona una opción del menú para comenzar.</p>
        </div>
      </main>
    </div>
  );
}

export default Dashboard;

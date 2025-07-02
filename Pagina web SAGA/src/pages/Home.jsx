import { useNavigate } from "react-router-dom";
import styles from "../styles/Home.module.css";

function Home() {
  const navigate = useNavigate();

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Bienvenido a SAGA</h1>
      <button className={styles.button} onClick={() => navigate("/login")}>
        Iniciar sesión
      </button>
    </div>
  );
}

export default Home;

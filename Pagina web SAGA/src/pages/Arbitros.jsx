import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import styles from "../styles/Arbitros.module.css";

function Partidos() {
  const [arbitros, setArbitros] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/arbitros")
      .then((res) => res.json())
      .then((data) => setArbitros(data))
      .catch((err) => console.error("Error al cargar árbitros:", err));
  }, []);

  return (
    <div className={styles.container}>
      <Navbar />

      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Árbitros disponibles</h2>
        </div>

        <table className={styles.tabla}>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Categoría</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {arbitros.map((arbitro, index) => (
              <tr key={index}>
                <td>{arbitro.nombre}</td>
                <td>{arbitro.categoria}</td>
                <td>
                  <button
                    onClick={() =>
                      alert(
                        `Tel: ${arbitro.telefono}\nCédula: ${arbitro.cedula}\nActivo: ${
                          arbitro.activo ? "Sí" : "No"
                        }`
                      )
                    }
                  >
                    Ver detalles
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </main>
    </div>
  );
}

export default Partidos;

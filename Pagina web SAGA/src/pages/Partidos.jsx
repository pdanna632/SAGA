import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import styles from "../styles/Partidos.module.css";

function Partidos() {
  const [partidos, setPartidos] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/partidos")
      .then((res) => res.json())
      .then((data) => setPartidos(data))
      .catch((err) => console.error("Error al cargar los partidos:", err));
  }, []);

  return (
    <div className={styles.container}>
      <Navbar />
      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Partidos disponibles</h2>

          <table className={styles.tabla}>
            <thead>
              <tr>
                <th>Categoría</th>
                <th>Municipio</th>
                <th>Local</th>
                <th>Visitante</th>
                <th>Fecha</th>
                <th>Hora</th>
                <th>Escenario</th>
              </tr>
            </thead>
            <tbody>
              {partidos.map((partido) => (
                <tr key={partido.id}>
                  <td>{partido.categoria}</td>
                  <td>{partido.municipio}</td>
                  <td>{partido.equipoLocal}</td>
                  <td>{partido.equipoVisitante}</td>
                  <td>{partido.fecha}</td>
                  <td>{partido.hora}</td>
                  <td>{partido.escenario}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}

export default Partidos;

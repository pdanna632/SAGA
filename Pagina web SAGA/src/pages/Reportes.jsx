import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import styles from "../styles/Reportes.module.css";

function Reporte() {
  const [partidos, setPartidos] = useState([]);
  const [visibleReporte, setVisibleReporte] = useState(null);
  const [pqrs, setPqrs] = useState("");
  const [detallesCliente, setDetallesCliente] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/partidos")
      .then((res) => res.json())
      .then((data) => setPartidos(data))
      .catch((err) => console.error("Error cargando partidos:", err));
  }, []);

  const toggleReporte = (id) => {
    setVisibleReporte((prev) => (prev === id ? null : id));
  };

  return (
    <div className={styles.container}>
      <Navbar />
      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Reportes de Partidos</h2>

          {partidos.map((partido) => (
            <div key={partido.id} className={styles.reporteItem}>
              <button onClick={() => toggleReporte(partido.id)}>
                {partido.equipoLocal} VS {partido.equipoVisitante}
              </button>

              {visibleReporte === partido.id && (
                <div className={styles.detalles}>
                  <details>
                    <summary>Detalles (de parte del cuerpo técnico asignado)</summary>
                    <textarea rows="4" placeholder="Escribe aquí..."></textarea>
                  </details>

                  <details>
                    <summary>Árbitro(s) asignado(s)</summary>
                    <textarea rows="2" placeholder="Escribe aquí árbitros asignados..."></textarea>
                  </details>

                  <details>
                    <summary>Detalles del partido (horario, locación, etc)</summary>
                    <p>Fecha: {partido.fecha}</p>
                    <p>Hora: {partido.hora}</p>
                    <p>Municipio: {partido.municipio}</p>
                    <p>Escenario: {partido.escenario}</p>
                  </details>

                  <details>
                    <summary>PQRS POR PARTE DEL CLIENTE</summary>
                    <textarea
                      rows="4"
                      value={pqrs}
                      onChange={(e) => setPqrs(e.target.value)}
                      placeholder="Petición, Queja, Reclamo o Sugerencia..."
                    />
                  </details>
                </div>
              )}
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}

export default Reporte;

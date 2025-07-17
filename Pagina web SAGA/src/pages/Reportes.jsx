import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import styles from "../styles/Reportes.module.css";

function Reporte() {
  const [partidos, setPartidos] = useState([]);
  const [visibleReporte, setVisibleReporte] = useState(null);
  const [pqrs, setPqrs] = useState("");
  const [detallesCliente, setDetallesCliente] = useState("");
  const [arbitrosAsignados, setArbitrosAsignados] = useState({});

  useEffect(() => {
    fetch("http://localhost:8080/api/partidos")
      .then((res) => res.json())
      .then((data) => {
        setPartidos(data);

        // Precargar árbitro solo para Tigres FC vs Leones Sur
        const inicial = {};
        data.forEach((p) => {
          if (
            p.equipoLocal === "Tigres FC" &&
            p.equipoVisitante === "Leones Sur"
          ) {
            inicial[p.id] = "Juan Perez Gomez";
          } else {
            inicial[p.id] = "";
          }
        });
        setArbitrosAsignados(inicial);
      })
      .catch((err) => console.error("Error cargando partidos:", err));
  }, []);

  const toggleReporte = (id) => {
    setVisibleReporte((prev) => (prev === id ? null : id));
  };

  const handleArbitroChange = (id, value) => {
    setArbitrosAsignados((prev) => ({ ...prev, [id]: value }));
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
                    <textarea 
                      rows="4" 
                      placeholder="Escribe aquí..."
                      value={"Partido realizado: " + partido.fecha}
                    />
                  </details>

                  <details open>
                    <summary>Árbitro(s) asignado(s)</summary>
                    <textarea
                      rows="2"
                      placeholder="Escribe aquí árbitros asignados..."
                      value={arbitrosAsignados[partido.id] || "Juan Perez Gomez, Categoría B, 3124567890"}
                      onChange={(e) =>
                        handleArbitroChange(partido.id, e.target.value)
                      }
                    />
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

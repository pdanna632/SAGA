import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import styles from "../styles/Modificaciones.module.css";

function Modificar() {
  const [partidos, setPartidos] = useState([]);
  const [partidoSeleccionado, setPartidoSeleccionado] = useState(null);
  const [modificaciones, setModificaciones] = useState({});

  useEffect(() => {
    fetch("http://localhost:8080/api/partidos")
      .then((res) => res.json())
      .then((data) => setPartidos(data))
      .catch((err) => console.error("Error al cargar partidos:", err));
  }, []);

  const handleMostrarDetalles = (partido) => {
    setPartidoSeleccionado(partido);
    setModificaciones({
      fecha: partido.fecha,
      hora: partido.hora,
      escenario: partido.escenario,
      municipio: partido.municipio,
    });
  };

  const handleCambio = (campo, valor) => {
    setModificaciones({ ...modificaciones, [campo]: valor });
  };

  const handleGuardar = () => {
    // Aquí iría la lógica para enviar las modificaciones al backend (cuando esté lista la persistencia)
    console.log("Guardando cambios:", modificaciones);
    alert("Cambios guardados (simulado)");
  };

  return (
    <div className={styles.container}>
      <Navbar />
      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Modificar Partidos</h2>

          <table className={styles.tabla}>
            <thead>
              <tr>
                <th>Partido</th>
                <th>Categoría</th>
                <th>Acción</th>
              </tr>
            </thead>
            <tbody>
              {partidos.map((partido) => (
                <tr key={partido.id}>
                  <td>
                    {partido.equipoLocal} vs {partido.equipoVisitante}
                  </td>
                  <td>{partido.categoria}</td>
                  <td>
                    <button
                      onClick={() => handleMostrarDetalles(partido)}
                    >
                      Detalles
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {partidoSeleccionado && (
            <div className={styles.formulario}>
              <h3>Editar Partido: {partidoSeleccionado.equipoLocal} vs {partidoSeleccionado.equipoVisitante}</h3>
              <label>
                Fecha:
                <input
                  type="date"
                  value={modificaciones.fecha}
                  onChange={(e) => handleCambio("fecha", e.target.value)}
                />
              </label>
              <label>
                Hora:
                <input
                  type="time"
                  value={modificaciones.hora}
                  onChange={(e) => handleCambio("hora", e.target.value)}
                />
              </label>
              <label>
                Municipio:
                <input
                  type="text"
                  value={modificaciones.municipio}
                  onChange={(e) => handleCambio("municipio", e.target.value)}
                />
              </label>
              <label>
                Escenario:
                <input
                  type="text"
                  value={modificaciones.escenario}
                  onChange={(e) => handleCambio("escenario", e.target.value)}
                />
              </label>
              <button onClick={handleGuardar}>Guardar Modificaciones</button>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

export default Modificar;

// ✅ COMPONENTE: Asignacion.jsx
import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import styles from "../styles/Asignacion.module.css";

function Asignacion() {
  const [partidos, setPartidos] = useState([]);
  const [arbitros, setArbitros] = useState([]);
  const [asignaciones, setAsignaciones] = useState({});

  useEffect(() => {
    fetch("http://localhost:8080/api/partidos")
      .then((res) => res.json())
      .then((data) => setPartidos(data))
      .catch((err) => console.error("Error al cargar partidos:", err));

    fetch("http://localhost:8080/api/arbitros")
      .then((res) => res.json())
      .then((data) => setArbitros(data))
      .catch((err) => console.error("Error al cargar árbitros:", err));
  }, []);

  const handleAsignar = async (idPartido, arbitroNombre) => {
    setAsignaciones((prev) => ({ ...prev, [idPartido]: arbitroNombre }));

    try {
      const response = await fetch("http://localhost:8080/api/designaciones", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          arbitroNombre,
          partidoId: idPartido,
          rol: "Central",
        }),
      });

      const mensaje = await response.text();
      alert(mensaje);
    } catch (error) {
      console.error("Error al asignar árbitro:", error);
      alert("Error al asignar el árbitro.");
    }
  };

  return (
    <div className={styles.container}>
      <Navbar />
      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Asignación de Árbitros</h2>
        </div>

        <table className={styles.tabla}>
          <thead>
            <tr>
              <th>Partido</th>
              <th>Categoría</th>
              <th>Asignar Árbitro</th>
            </tr>
          </thead>
          <tbody>
            {partidos.map((partido) => (
              <tr key={partido.id}>
                <td>{partido.equipoLocal} vs {partido.equipoVisitante}</td>
                <td>{partido.categoria}</td>
                <td>
                  <select
                    value={asignaciones[partido.id] || ""}
                    onChange={(e) => handleAsignar(partido.id, e.target.value)}
                  >
                    <option value="">Seleccionar Árbitro</option>
                    {arbitros
                      .filter((arb) => arb.nombre === "Juan Pérez Gómez")
                      .map((arb, idx) => (
                        <option key={idx} value={arb.nombre}>{arb.nombre}</option>
                      ))}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </main>
    </div>
  );
}

export default Asignacion;

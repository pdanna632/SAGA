import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import styles from "../styles/Asignacion.module.css";

// Orden de categorías para comparación
const categoriaOrden = {
  D: 1,
  C: 2,
  B: 3,
  A: 4,
  FIFA: 5,
};

// Función auxiliar para verificar compatibilidad
function esCompatible(categoriaArbitro, categoriaPartido) {
  return categoriaOrden[categoriaArbitro] >= categoriaOrden[categoriaPartido];
}

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

  // Función para manejar la asignación (solo visual, sin persistencia)
  const handleAsignar = (idPartido, arbitroNombre) => {
    if (!arbitroNombre) return;

    setAsignaciones((prev) => ({ ...prev, [idPartido]: arbitroNombre }));
    alert("Árbitro designado con éxito ✅");
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
                      .filter((arb) => esCompatible(arb.categoria, partido.categoria))
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

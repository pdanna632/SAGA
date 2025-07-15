import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import styles from "../styles/Partidos.module.css";

function Partidos() {
  const [partidos, setPartidos] = useState([]);
  const [filtros, setFiltros] = useState({
    categoria: "",
    municipio: "",
    fecha: ""
  });

  useEffect(() => {
    fetch("http://localhost:8080/api/partidos")
      .then((res) => res.json())
      .then((data) => setPartidos(data))
      .catch((err) => console.error("Error al cargar los partidos:", err));
  }, []);

  const partidosFiltrados = partidos.filter((p) => {
    const coincideCategoria =
      !filtros.categoria || p.categoria === filtros.categoria;
    const coincideMunicipio =
      !filtros.municipio || p.municipio === filtros.municipio;
    const coincideFecha = !filtros.fecha || p.fecha === filtros.fecha;
    return coincideCategoria && coincideMunicipio && coincideFecha;
  });

  return (
    <div className={styles.container}>
      <Navbar />

      <main className={styles.main}>
        <div className={styles.box}>
          <h2>Partidos disponibles</h2>

          <div className={styles.filtros}>
            <select
              value={filtros.categoria}
              onChange={(e) =>
                setFiltros({ ...filtros, categoria: e.target.value })
              }
            >
              <option value="">Todas las categorías</option>
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
              <option value="FIFA">FIFA</option>
            </select>

            <select
              value={filtros.municipio}
              onChange={(e) =>
                setFiltros({ ...filtros, municipio: e.target.value })
              }
            >
              <option value="">Todos los municipios</option>
              {[...new Set(partidos.map((p) => p.municipio))].map(
                (muni, idx) => (
                  <option key={idx} value={muni}>
                    {muni}
                  </option>
                )
              )}
            </select>

            <input
              type="date"
              value={filtros.fecha}
              onChange={(e) =>
                setFiltros({ ...filtros, fecha: e.target.value })
              }
            />
          </div>

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
              {partidosFiltrados.map((partido) => (
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

import { useState, useEffect } from "react";
import axios from "axios";

const API_BASE = "http://localhost:8080/api/earthquakes";

export default function App() {
  const [earthquakes, setEarthquakes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchEarthquakes = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await axios.get(`${API_BASE}/fetch`);
      setEarthquakes(res.data);
    } catch  {
      setError("Failed to fetch earthquake data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    axios.get(API_BASE).then(res => setEarthquakes(res.data));
  }, []);

  const deleteEarthquake = async (id) => {
    try {
      await axios.delete(`${API_BASE}/${id}`);
      setEarthquakes(prev => prev.filter(eq => eq.id !== id));
    } catch  {
      setError("Failed to delete earthquake.");
    }
  };

  return (
      <div style={{ maxWidth: "1100px", margin: "0 auto", padding: "2rem", fontFamily: "sans-serif" }}>
        <h1 style={{ fontSize: "24px", marginBottom: "0.5rem" }}>Earthquake Monitor</h1>
        <p style={{ color: "#666", marginBottom: "1.5rem" }}>
          Live data from USGS — magnitude &gt; 2.0 only
        </p>

        <button
            onClick={fetchEarthquakes}
            disabled={loading}
            style={{
              padding: "10px 20px",
              background: "#2563eb",
              color: "white",
              border: "none",
              borderRadius: "6px",
              cursor: "pointer",
              marginBottom: "1.5rem",
              fontSize: "14px"
            }}
        >
          {loading ? "Fetching..." : "Fetch Latest Earthquakes"}
        </button>

        {error && <p style={{ color: "red", marginBottom: "1rem" }}>{error}</p>}

        {earthquakes.length === 0 && !loading && (
            <p style={{ color: "#888" }}>No earthquakes loaded yet.</p>
        )}

        {earthquakes.length > 0 && (
            <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "14px" }}>
              <thead>
              <tr style={{ background: "#f1f5f9", textAlign: "left" }}>
                <th style={th}>Magnitude</th>
                <th style={th}>Type</th>
                <th style={th}>Place</th>
                <th style={th}>Time</th>
                <th style={th}>Lat</th>
                <th style={th}>Lng</th>
                <th style={th}>Action</th>
              </tr>
              </thead>
              <tbody>
              {earthquakes.map(eq => (
                  <tr key={eq.id} style={{ borderBottom: "1px solid #e2e8f0" }}>
                    <td style={td}>
                  <span style={{
                    background: eq.magnitude >= 4 ? "#fee2e2" : "#fef9c3",
                    color: eq.magnitude >= 4 ? "#b91c1c" : "#854d0e",
                    padding: "2px 8px",
                    borderRadius: "4px",
                    fontWeight: "500"
                  }}>
                    {eq.magnitude}
                  </span>
                    </td>
                    <td style={td}>{eq.magType}</td>
                    <td style={td}>{eq.place}</td>
                    <td style={td}>{new Date(eq.time).toLocaleString()}</td>
                    <td style={td}>{eq.latitude}</td>
                    <td style={td}>{eq.longitude}</td>
                    <td style={td}>
                      <button
                          onClick={() => deleteEarthquake(eq.id)}
                          style={{
                            padding: "4px 10px",
                            background: "transparent",
                            border: "1px solid #e2e8f0",
                            borderRadius: "4px",
                            cursor: "pointer",
                            fontSize: "12px",
                            color: "#ef4444"
                          }}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
              ))}
              </tbody>
            </table>
        )}
      </div>
  );
}

const th = { padding: "10px 12px", fontWeight: "500", fontSize: "13px" };
const td = { padding: "10px 12px", color: "#334155" };
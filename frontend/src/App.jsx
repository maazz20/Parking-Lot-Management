import {
  Car,
  DoorOpen,
  Loader2,
  MapPin,
  Palette,
  ParkingCircle,
  Plus,
  RefreshCw,
  Search,
  Ticket,
  Warehouse
} from "lucide-react";
import { useEffect, useMemo, useState } from "react";

const API_BASE =
  import.meta.env.VITE_API_BASE_URL ||
  "https://parking-lot-management-25s2.onrender.com/api";

async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers
    },
    ...options
  });

  const contentType = response.headers.get("content-type") || "";
  const payload = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message =
      typeof payload === "string"
        ? payload
        : payload.message || payload.error || Object.values(payload).join(", ");
    throw new Error(message || "Request failed");
  }

  return payload;
}

function App() {
  const [lotForm, setLotForm] = useState({ name: "Main Parking", totalSlots: 12 });
  const [parkForm, setParkForm] = useState({ registrationNumber: "", color: "" });
  const [leaveSlot, setLeaveSlot] = useState("");
  const [registrationQuery, setRegistrationQuery] = useState("");
  const [colorQuery, setColorQuery] = useState("");
  const [status, setStatus] = useState([]);
  const [summary, setSummary] = useState(null);
  const [registrationResult, setRegistrationResult] = useState(null);
  const [colorResult, setColorResult] = useState(null);
  const [notice, setNotice] = useState(null);
  const [loading, setLoading] = useState({ status: false });

  const occupiedSlots = useMemo(
    () => new Map(status.map((slot) => [slot.slotNumber, slot])),
    [status]
  );

  const lotName = summary?.name || "Control Desk";
  const totalSlots = Number(summary?.totalSlots || 0);
  const occupiedCount = Number(summary?.occupiedSlots ?? status.length);
  const availableSlots = Number(summary?.availableSlots ?? Math.max(totalSlots - status.length, 0));
  const hasParkingLot = totalSlots > 0;

  const showNotice = (type, message) => {
    setNotice({ type, message });
  };

  const runAction = async (key, action, successMessage) => {
    setLoading((current) => ({ ...current, [key]: true }));
    try {
      const result = await action();
      if (successMessage) {
        showNotice("success", typeof successMessage === "function" ? successMessage(result) : successMessage);
      }
      return result;
    } catch (error) {
      showNotice("error", error.message);
      return null;
    } finally {
      setLoading((current) => ({ ...current, [key]: false }));
    }
  };

  const refreshStatus = async () => {
    const data = await runAction("status", () => apiRequest("/parking-lots/status"));
    if (data) {
      setStatus(data);
    }
  };

  const refreshSummary = async () => {
    const data = await runAction("summary", () => apiRequest("/parking-lots/summary"));
    if (data) {
      setSummary(data);
    }
  };

  const refreshDashboard = () => {
    refreshSummary();
    refreshStatus();
  };

  useEffect(() => {
    refreshDashboard();
  }, []);

  const createLot = async (event) => {
    event.preventDefault();
    const created = await runAction(
      "create",
      () =>
        apiRequest("/parking-lots", {
          method: "POST",
          body: JSON.stringify({
            name: lotForm.name.trim(),
            totalSlots: Number(lotForm.totalSlots)
          })
        }),
      (result) => `${result.name} is ready with ${result.totalSlots} slots.`
    );

    if (created) {
      refreshDashboard();
    }
  };

  const parkVehicle = async (event) => {
    event.preventDefault();
    const message = await runAction(
      "park",
      () =>
        apiRequest("/parking-lots/park", {
          method: "POST",
          body: JSON.stringify({
            registrationNumber: parkForm.registrationNumber.trim(),
            color: parkForm.color.trim()
          })
        }),
      (result) => result
    );

    if (message) {
      setParkForm({ registrationNumber: "", color: "" });
      refreshDashboard();
    }
  };

  const leaveVehicle = async (event) => {
    event.preventDefault();
    const message = await runAction(
      "leave",
      () => apiRequest(`/parking-lots/leave/${leaveSlot}`, { method: "POST" }),
      (result) => result
    );

    if (message) {
      setLeaveSlot("");
      refreshDashboard();
    }
  };

  const findByRegistration = async (event) => {
    event.preventDefault();
    const result = await runAction("registration", () =>
      apiRequest(`/parking-lots/registration/${encodeURIComponent(registrationQuery.trim())}`)
    );
    if (result) {
      setRegistrationResult(result);
      showNotice("success", `${registrationQuery.toUpperCase()} is parked in slot ${result.slotNumber}.`);
    }
  };

  const findByColor = async (event) => {
    event.preventDefault();
    const color = encodeURIComponent(colorQuery.trim());
    const result = await runAction("color", async () => {
      const [registrations, slots] = await Promise.all([
        apiRequest(`/parking-lots/color/${color}`),
        apiRequest(`/parking-lots/color/${color}/slots`)
      ]);
      return { registrations, slots };
    });

    if (result) {
      setColorResult(result);
      showNotice("success", `Found ${result.registrations.length} registration(s) for ${colorQuery}.`);
    }
  };

  return (
    <main className="app-shell">
      <section className="top-bar">
        <div>
          <p className="eyebrow">Parking Lot System</p>
          <h1>{lotName}</h1>
        </div>
        <button className="icon-button" onClick={refreshDashboard} disabled={loading.status || loading.summary} title="Refresh status">
          {loading.status ? <Loader2 className="spin" size={19} /> : <RefreshCw size={19} />}
        </button>
      </section>

      <section className="metrics-grid" aria-label="Parking summary">
        <Metric icon={Warehouse} label="Total Slots" value={totalSlots} />
        <Metric icon={ParkingCircle} label="Occupied" value={occupiedCount} />
        <Metric icon={MapPin} label="Available" value={availableSlots} />
      </section>

      {notice && <div className={`notice ${notice.type}`}>{notice.message}</div>}

      <section className="workspace">
        <div className="forms-column">
          <Panel title="Create Parking Lot" icon={Plus}>
            <form onSubmit={createLot} className="stacked-form">
              <label>
                Lot name
                <input
                  value={lotForm.name}
                  onChange={(event) => setLotForm({ ...lotForm, name: event.target.value })}
                  placeholder="Main Parking"
                  required
                />
              </label>
              <label>
                Total slots
                <input
                  type="number"
                  min="1"
                  value={lotForm.totalSlots}
                  onChange={(event) => setLotForm({ ...lotForm, totalSlots: event.target.value })}
                  required
                />
              </label>
              <SubmitButton loading={loading.create} icon={Plus}>Create Lot</SubmitButton>
            </form>
          </Panel>

          <Panel title="Park Vehicle" icon={Car}>
            <form onSubmit={parkVehicle} className="stacked-form">
              <label>
                Registration number
                <input
                  value={parkForm.registrationNumber}
                  onChange={(event) => setParkForm({ ...parkForm, registrationNumber: event.target.value })}
                  placeholder="KA-01-HH-1234"
                  required
                />
              </label>
              <label>
                Color
                <input
                  value={parkForm.color}
                  onChange={(event) => setParkForm({ ...parkForm, color: event.target.value })}
                  placeholder="White"
                  required
                />
              </label>
              <SubmitButton loading={loading.park} icon={Ticket} disabled={!hasParkingLot}>
                Park Vehicle
              </SubmitButton>
              {!hasParkingLot && <p className="form-hint">Create a parking lot before adding vehicles.</p>}
            </form>
          </Panel>

          <Panel title="Exit Vehicle" icon={DoorOpen}>
            <form onSubmit={leaveVehicle} className="inline-form">
              <label>
                Slot number
                <input
                  type="number"
                  min="1"
                  value={leaveSlot}
                  onChange={(event) => setLeaveSlot(event.target.value)}
                  placeholder="4"
                  required
                />
              </label>
              <SubmitButton loading={loading.leave} icon={DoorOpen}>Mark Exit</SubmitButton>
            </form>
          </Panel>
        </div>

        <section className="status-column">
          <Panel title="Live Slot Map" icon={ParkingCircle}>
            {hasParkingLot ? (
              <div className="slot-grid">
                {Array.from({ length: totalSlots }, (_, index) => {
                  const slotNumber = index + 1;
                  const vehicle = occupiedSlots.get(slotNumber);
                  return (
                    <div className={`slot-tile ${vehicle ? "occupied" : "empty"}`} key={slotNumber}>
                      <span>Slot {slotNumber}</span>
                      <strong>{vehicle?.registrationNumber || "Available"}</strong>
                      {vehicle && <small>{vehicle.color}</small>}
                    </div>
                  );
                })}
              </div>
            ) : (
              <div className="empty-state">No parking lot exists in this database yet.</div>
            )}
          </Panel>

          <Panel title="Search" icon={Search}>
            <div className="search-grid">
              <form onSubmit={findByRegistration} className="stacked-form">
                <label>
                  Registration number
                  <input
                    value={registrationQuery}
                    onChange={(event) => setRegistrationQuery(event.target.value)}
                    placeholder="KA-01-HH-1234"
                    required
                  />
                </label>
                <SubmitButton loading={loading.registration} icon={Search}>Find Slot</SubmitButton>
                {registrationResult && <ResultPill>Slot {registrationResult.slotNumber}</ResultPill>}
              </form>

              <form onSubmit={findByColor} className="stacked-form">
                <label>
                  Vehicle color
                  <input
                    value={colorQuery}
                    onChange={(event) => setColorQuery(event.target.value)}
                    placeholder="White"
                    required
                  />
                </label>
                <SubmitButton loading={loading.color} icon={Palette}>Find Matches</SubmitButton>
                {colorResult && (
                  <div className="result-box">
                    <strong>Registrations</strong>
                    <span>{colorResult.registrations.join(", ") || "None"}</span>
                    <strong>Slots</strong>
                    <span>{colorResult.slots.join(", ") || "None"}</span>
                  </div>
                )}
              </form>
            </div>
          </Panel>

          <Panel title="Occupied Vehicles" icon={Ticket}>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Slot</th>
                    <th>Registration</th>
                    <th>Color</th>
                  </tr>
                </thead>
                <tbody>
                  {status.length === 0 ? (
                    <tr>
                      <td colSpan="3" className="empty-table">No vehicles parked.</td>
                    </tr>
                  ) : (
                    status.map((vehicle) => (
                      <tr key={`${vehicle.slotNumber}-${vehicle.registrationNumber}`}>
                        <td>{vehicle.slotNumber}</td>
                        <td>{vehicle.registrationNumber}</td>
                        <td>{vehicle.color}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </Panel>
        </section>
      </section>
    </main>
  );
}

function Metric({ icon: Icon, label, value }) {
  return (
    <article className="metric">
      <Icon size={22} />
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function Panel({ title, icon: Icon, children }) {
  return (
    <section className="panel">
      <div className="panel-title">
        <Icon size={19} />
        <h2>{title}</h2>
      </div>
      {children}
    </section>
  );
}

function SubmitButton({ loading, icon: Icon, disabled = false, children }) {
  return (
    <button type="submit" className="primary-button" disabled={loading || disabled}>
      {loading ? <Loader2 className="spin" size={18} /> : <Icon size={18} />}
      <span>{children}</span>
    </button>
  );
}

function ResultPill({ children }) {
  return <div className="result-pill">{children}</div>;
}

export default App;

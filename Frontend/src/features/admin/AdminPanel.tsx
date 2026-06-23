import React, { useState } from "react";
import AdminLogin from "./AdminLogin";
import { initiateAuthorization } from "../../services/PlaylistService";
import "./AdminPanel.css";

const AdminPanel: React.FC = () => {
  const [status, setStatus] = useState("");

  const handleAdminSubmit = async (username: string, password: string) => {
    setStatus("Requesting Spotify authorization…");
    try {
      const url = await initiateAuthorization(username, password);
      window.location.href = url; // Spotify consent; the callback stores the token
    } catch {
      setStatus("Authorization failed. Check your credentials and try again.");
    }
  };

  return (
    <div className="admin-panel">
      <div className="admin-card">
        <h2>Authorize Spotify</h2>
        <p>Sign in with the admin credentials to re-authorize the service account.</p>
        <AdminLogin onAdminSubmit={handleAdminSubmit} />
        {status && <p className="admin-status">{status}</p>}
      </div>
    </div>
  );
};

export default AdminPanel;

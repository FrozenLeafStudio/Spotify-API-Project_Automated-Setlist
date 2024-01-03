import React, { useState } from "react";

interface AdminLoginProps {
  onAdminSubmit: (username: string, password: string) => void;
}

const AdminLogin: React.FC<AdminLoginProps> = ({ onAdminSubmit }) => {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    onAdminSubmit(username, password);
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label htmlFor="admin-username">Username:</label>
        <input
          id="admin-username"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Username"
        />
      </div>
      <div>
        <label htmlFor="admin-password">Password:</label>
        <input
          id="admin-password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
        />
      </div>
      <button type="submit">Login</button>
    </form>
  );
};

export default AdminLogin;

import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./App.css";
import api from "./api/axiosConfig";
import { useState, useEffect } from "react";

function App() {
  const [artist, setArtist] = useState();

  const getArtist = async () => {
    try {
      const response = await api.get("/api/v1/artists");
      console.log(response.data);
      setArtist(response.data);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    getArtist();
  }, []);

  return <></>;
}

export default App;

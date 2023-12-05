import React, { useState } from "react";
import "./App.css";
import SearchBar from "./features/artist/SearchBar";
import { searchArtists } from "./services/ArtistService";
import { Artist } from "./models/Artist";
import ArtistSearchResults from "./features/artist/ArtistSearchResults";

function App() {
  const [artist, setArtist] = useState<Artist | null>(null);
  const handleSearch = async (searchTerm: string) => {
    try {
      const apiResponse = await searchArtists(searchTerm);
      const artistData = apiResponse;

      const newArtist = new Artist({
        mbid: artistData.mbid,
        tmid: artistData.tmid,
        name: artistData.name,
        sortName: artistData.sortName,
        disambiguation: artistData.disambiguation,
        url: artistData.url,
        setlists: artistData.setlists,
      });
      setArtist(newArtist); //update artist state
    } catch (error) {
      console.error("Unable to search for Artist: ", error);
    }
  };
  return (
    <>
      <div className="App">
        <div className="main-container">
          <SearchBar onSearch={handleSearch} />
          <div>
            <ArtistSearchResults artist={artist} />
          </div>
        </div>
      </div>
    </>
  );
}

export default App;

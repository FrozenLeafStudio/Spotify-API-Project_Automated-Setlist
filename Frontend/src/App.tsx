import React, { useState } from "react";
import "./App.css";
import SearchBar from "./features/artist/SearchBar";
import { searchArtists } from "./services/ArtistService";
import { Artist } from "./models/Artist";
import ArtistSearchResults from "./features/artist/ArtistSearchResults";
import { Setlist } from "./models/Setlist";
import SetlistDisplay from "./features/setlist/SetlistDisplay";
import { searchSetlists } from "./services/SetlistService";

function App() {
  const [artist, setArtist] = useState<Artist | null>(null);
  const [setlists, setSetlists] = useState<Setlist[]>([]);
  const handleSearchSubmit = async (searchTerm: string) => {
    try {
      const artistData = await searchArtists(searchTerm);
      const newArtist = new Artist(artistData);
      setArtist(newArtist);

      const setlistData = await searchSetlists(newArtist.mbid);
      setSetlists(setlistData);
    } catch (error) {
      console.error("Unable to search for Artist: ", error);
    }
  };
  return (
    <>
      <div className="App">
        <div className="main-container">
          <SearchBar onSearchSubmit={handleSearchSubmit} />
          <div className="main-content">
            <ArtistSearchResults artist={artist} />
            <SetlistDisplay setlists={setlists} />
          </div>
        </div>
      </div>
    </>
  );
}

export default App;

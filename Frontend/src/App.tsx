import React, { useState } from "react";
import "./App.css";
import SearchBar from "./features/artist/SearchBar";
import { searchArtists } from "./services/ArtistService";
import { Artist } from "./models/Artist";
import ArtistSearchResults from "./features/artist/ArtistSearchResults";
import { Setlist } from "./models/Setlist";
import SetlistDisplay from "./features/setlist/SetlistDisplay";
import { searchSetlists } from "./services/SetlistService";
import { searchPlaylists } from "./services/PlaylistService";
import { Playlist } from "./models/Playlist";
//import PlaylistDisplay from "./features/playlist/PlaylistDisplay";
//import { Playlist } from "./models/Playlist";

function App() {
  const [artist, setArtist] = useState<Artist | null>(null);
  const [setlists, setSetlists] = useState<Setlist[]>([]);
  const [playlist, setPlaylist] = useState<Playlist>();
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
  const handlePlaylistSearch = async (e: string) => {
    try {
      if (!artist?.name) {
        return null;
      }
      const playlistData = await searchPlaylists(e, artist.name);
      const newPlaylist = new Playlist(playlistData);
      setPlaylist(newPlaylist);
      console.log(playlist);
    } catch (error) {
      console.error("Unable to search for Artist: ", error);
    }
  };
  //const handlePlayistCreation = async (playlistId) => {};
  //<PlaylistDisplay playlist={null} />
  return (
    <>
      <div className="App">
        <div className="main-container">
          <SearchBar onSearchSubmit={handleSearchSubmit} />
          {artist ? (
            <div className="main-content">
              <ArtistSearchResults artist={artist} />
              <SetlistDisplay
                setlists={setlists}
                handleClick={handlePlaylistSearch}
              />
            </div>
          ) : null}
        </div>
      </div>
    </>
  );
}

export default App;

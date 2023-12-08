import React, { useState } from "react";
import { searchArtists } from "./services/ArtistService";
import { Artist } from "./models/Artist";
import { Setlist } from "./models/Setlist";
import { searchSetlists } from "./services/SetlistService";
import { createPlaylists, searchPlaylists } from "./services/PlaylistService";
import { Playlist } from "./models/Playlist";
import "./App.css";
import SearchBar from "./features/artist/SearchBar";
import ArtistSearchResults from "./features/artist/ArtistSearchResults";
import SetlistDisplay from "./features/setlist/SetlistDisplay";
import PlaylistDisplay from "./features/playlist/PlaylistDisplay";

function App() {
  const [artist, setArtist] = useState<Artist | null>(null);
  const [setlists, setSetlists] = useState<Setlist[] | null>([]);
  const [playlist, setPlaylist] = useState<Playlist | null>(null);

  const handleSearchSubmit = async (searchTerm: string) => {
    if (setlists != null) setSetlists(null); //need to clear setlists if a user searches for another artist after original
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
  const handlePlaylistSearch = async (setlistId: string) => {
    try {
      if (!artist?.name) {
        return null;
      }
      const playlistData = await searchPlaylists(setlistId, artist.name);
      const newPlaylist = new Playlist(playlistData);
      setPlaylist(newPlaylist);
      console.log(playlist);
    } catch (error) {
      console.error("Unable to search for Artist: ", error);
    }
  };
  const PlayistCreation = async (playlistId: string) => {
    try {
      const playlistData = await createPlaylists(playlistId);
      const newPlaylist = playlistData;
      setPlaylist(newPlaylist);
      console.log(playlist);
    } catch (error) {
      console.error("Unable to search for Artist: ", error);
    }
  };
  return (
    <>
      <div className="App">
        <div className="main-container">
          <SearchBar onSearchSubmit={handleSearchSubmit} />
          {artist ? (
            <div className="main-content">
              <ArtistSearchResults artistSearch={artist} />
              <SetlistDisplay
                setlists={setlists}
                handleClick={handlePlaylistSearch}
              />
            </div>
          ) : null}
          {playlist ? (
            <PlaylistDisplay
              spotifyPlaylist={playlist}
              createSpotifyPlaylist={PlayistCreation}
            />
          ) : null}
        </div>
      </div>
    </>
  );
}

export default App;

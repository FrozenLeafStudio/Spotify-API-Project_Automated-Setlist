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
  const [setlistsExist, setSetlistsExist] = useState(false);
  const [playlist, setPlaylist] = useState<Playlist | null>(null);
  const [playlistExist, setPlaylistExist] = useState(false);
  const [includeCovers, setIncludeCovers] = useState<boolean>(false);

  const handleSearchSubmit = async (searchTerm: string) => {
    if (setlistsExist) {
      setSetlists(null);
      setSetlistsExist(false);
      setPlaylist(null);
      setPlaylistExist(false);
    }
    try {
      const artistData = await searchArtists(searchTerm);
      const newArtist = new Artist(artistData);
      setArtist(newArtist);

      const setlistData = await searchSetlists(newArtist.mbid);
      setSetlists(setlistData);
      setSetlistsExist(true);
    } catch (error) {
      console.error("Unable to search for Artist: ", error);
    }
  };
  const handlePlaylistSearch = async (setlistId: string) => {
    if (playlistExist) {
      setPlaylist(null);
      setPlaylistExist(false);
    }
    try {
      if (!artist?.name) {
        return null;
      }
      const playlistData = await searchPlaylists(setlistId, artist.name);
      const newPlaylist = new Playlist(playlistData);

      setPlaylist(newPlaylist);
      setPlaylistExist(true);
    } catch (error) {
      console.error("Unable to search for playlists: ", error);
    }
  };
  const PlayistCreation = async (
    playlistId: string,
    includeCovers: boolean
  ) => {
    try {
      const playlistData = await createPlaylists(playlistId, includeCovers);
      const newPlaylist = new Playlist(playlistData);
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
          <div className="main-content">
            {setlistsExist ? (
              <>
                <ArtistSearchResults artistSearch={artist} />
                <SetlistDisplay
                  setlists={setlists}
                  handleClick={handlePlaylistSearch}
                />
              </>
            ) : null}
            {playlistExist ? (
              <PlaylistDisplay
                spotifyPlaylist={playlist}
                createSpotifyPlaylist={PlayistCreation}
              />
            ) : null}
          </div>
        </div>
      </div>
    </>
  );
}

export default App;

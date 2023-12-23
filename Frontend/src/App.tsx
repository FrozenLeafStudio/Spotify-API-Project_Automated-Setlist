import { useState } from "react";
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
  const [searchSubmitted, setSearchSubmitted] = useState(false);
  const [setlists, setSetlists] = useState<Setlist[] | null>([]);
  const [selectedSetlist, setSelectedSetlist] = useState<Setlist | null>(null);
  const [setlistsExist, setSetlistsExist] = useState(false);
  const [playlist, setPlaylist] = useState<Playlist | null>(null);
  const [playlistExist, setPlaylistExist] = useState(false);

  const handleSearchSubmit = async (searchTerm: string) => {
    if (setlistsExist) {
      setSetlists(null);
      setSetlistsExist(false);
      setPlaylist(null);
      setPlaylistExist(false);
      setSearchSubmitted(true);
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
  const handlePlaylistSearch = async (returnedSetlist: Setlist) => {
    if (playlistExist) {
      setPlaylist(null);
      setPlaylistExist(false);
    }
    try {
      if (!artist?.name) {
        return null;
      }
      setSelectedSetlist(returnedSetlist);
      const playlistData = await searchPlaylists(
        returnedSetlist.setlistID,
        artist.name
      );
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
        <div
          className={`main-container ${
            searchSubmitted ? "search-submitted" : ""
          }`}
        >
          <SearchBar onSearchSubmit={handleSearchSubmit} />
          {setlistsExist && (
            <div className="main-content">
              <ArtistSearchResults artistSearch={artist} />
              <div
                className={`setlist-playlist-container ${
                  playlistExist ? "active" : ""
                }`}
              >
                <SetlistDisplay
                  setlists={setlists}
                  handleClick={handlePlaylistSearch}
                  className={playlistExist ? "active" : ""}
                />
                {playlistExist && selectedSetlist && (
                  <PlaylistDisplay
                    spotifyPlaylist={playlist}
                    setlist={selectedSetlist}
                    createSpotifyPlaylist={PlayistCreation}
                    className="active"
                  />
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  );
}

export default App;

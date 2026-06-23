import { useState, useRef } from "react";
import { Artist } from "./models/Artist";
import { Setlist } from "./models/Setlist";
import { searchSetlists } from "./services/SetlistService";
import { createPlaylists, searchPlaylists } from "./services/PlaylistService";
import { Playlist } from "./models/Playlist";
import SearchBar from "./features/artist/SearchBar";
import ArtistSearchResults from "./features/artist/ArtistSearchResults";
import SetlistDisplay from "./features/setlist/SetlistDisplay";
import PlaylistDisplay from "./features/playlist/PlaylistDisplay";
import AdminPanel from "./features/admin/AdminPanel";
import "./App.css";
import { Loading } from "./features/style/Loading";

function App() {
  const [artist, setArtist] = useState<Artist | null>(null);
  const prevArtistRef = useRef<Artist | null>(null);
  const [searchSubmitted, setSearchSubmitted] = useState(false);
  const [setlists, setSetlists] = useState<Setlist[] | null>([]);
  const [selectedSetlist, setSelectedSetlist] = useState<Setlist | null>(null);
  const [setlistsExist, setSetlistsExist] = useState(false);
  const [playlist, setPlaylist] = useState<Playlist | null>(null);
  const [playlistExist, setPlaylistExist] = useState(false);
  const [isPlaylistLoading, setIsPlaylistLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState<number>(1);

  const handleArtistSelect = async (selectedArtist: Artist) => {
    setSearchSubmitted(true);

    if (selectedArtist.name !== prevArtistRef.current?.name) {
      resetStates();
    }

    setArtist(selectedArtist);
    prevArtistRef.current = selectedArtist;

    try {
      const setlistData = await searchSetlists(selectedArtist.mbid, 1);
      setSetlists(setlistData && setlistData.length > 0 ? setlistData : []);
    } catch (error) {
      console.error("Error fetching setlists: ", error);
      setSetlists([]);
    }
    setSetlistsExist(true);
  };
  const fetchMoreSetlists = async () => {
    if (artist && artist.mbid && setlists) {
      try {
        const newPage = currentPage + 1;
        const additionalSetlists = await searchSetlists(artist.mbid, newPage);
        if (additionalSetlists.length > 0) {
          setSetlists([...setlists, ...additionalSetlists]);
          setCurrentPage(newPage);
        }
      } catch (error) {
        console.error("Error fetching more setlists: ", error);
      }
    }
  };
  const handlePlaylistSearch = async (returnedSetlist: Setlist) => {
    if (playlistExist) {
      setPlaylist(null);
      setPlaylistExist(false);
    }
    setIsPlaylistLoading(true);

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
      setIsPlaylistLoading(false);
    } catch (error) {
      console.error("Unable to search for playlists: ", error);
      setIsPlaylistLoading(false);
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
  const resetStates = () => {
    setSetlists([]);
    setSetlistsExist(false);
    setPlaylist(null);
    setPlaylistExist(false);
    setSelectedSetlist(null);
    setCurrentPage(1);
    setIsPlaylistLoading(false);
  };

  if (window.location.hash === "#admin") {
    return <AdminPanel />;
  }

  return (
    <>
      <div className="App">
        <div
          className={`main-container ${
            searchSubmitted ? "search-submitted" : ""
          }`}
        >
          <SearchBar onArtistSelect={handleArtistSelect} />
          {setlistsExist && (
            <div className="main-content">
              <ArtistSearchResults artistSearch={artist} />
              <div
                className={`setlist-playlist-container ${
                  playlistExist || isPlaylistLoading ? "active" : ""
                }`}
              >
                <SetlistDisplay
                  setlists={setlists}
                  artistUrl={artist ? artist.url : null}
                  handleClick={handlePlaylistSearch}
                  fetchMoreSetlists={fetchMoreSetlists}
                  className={playlistExist || isPlaylistLoading ? "active" : ""}
                />
                {isPlaylistLoading ? (
                  <Loading />
                ) : (
                  selectedSetlist && (
                    <PlaylistDisplay
                      spotifyPlaylist={playlist}
                      setlist={selectedSetlist}
                      createSpotifyPlaylist={PlayistCreation}
                      className={playlistExist ? "active" : ""}
                    />
                  )
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

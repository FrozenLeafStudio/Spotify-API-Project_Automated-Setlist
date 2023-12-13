import React, { useState } from "react";
import "./playlist.css";
import { Playlist } from "../../models/Playlist";

type playlistResults = {
  spotifyPlaylist: Playlist | null;
  createSpotifyPlaylist: (e: string) => void;
};
const PlaylistDisplay: React.FC<playlistResults> = ({
  spotifyPlaylist,
  createSpotifyPlaylist,
}) => {
  const [showMissingTracks, setShowMissingTracks] = useState(false);
  if (!spotifyPlaylist) {
    return <div>No Playlist Available</div>;
  }
  const handlePlayistCreation = (
    event: React.MouseEvent<HTMLButtonElement>,
    setlist: string
  ) => {
    event.stopPropagation();
    spotifyPlaylist;
    createSpotifyPlaylist(setlist);
  };
  return (
    <div className="playlist-container">
      <div>
        <div className="playlist-info">
          <h3>{spotifyPlaylist.name}</h3>
          <h4>{spotifyPlaylist.description}</h4>
          <a href={spotifyPlaylist.spotifyUrl}>Open Spotify Playlist</a>
          <ul>
            {spotifyPlaylist.tracks.map((apptrack, index) => {
              let trackClass = apptrack.trackFound
                ? "normal-track"
                : "missing-track";
              if (!showMissingTracks && !apptrack.trackFound) {
                return null;
              }
              return <li className={trackClass}>{apptrack.songName}</li>;
            })}
          </ul>
        </div>
        <button
          onClick={(e) => handlePlayistCreation(e, spotifyPlaylist.setlistID)}
        >
          Create Playlist
        </button>
        <button onClick={() => setShowMissingTracks(!showMissingTracks)}>
          {showMissingTracks ? "Hide Missing Tracks" : "Show Missing Tracks"}
        </button>
      </div>
    </div>
  );
};
export default PlaylistDisplay;

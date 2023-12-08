import React, { useState } from "react";
import { Setlist } from "../../models/Setlist";
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
        <button
          onClick={(e) => handlePlayistCreation(e, spotifyPlaylist.setlistID)}
        ></button>
      </div>
    </div>
  );
};
export default PlaylistDisplay;

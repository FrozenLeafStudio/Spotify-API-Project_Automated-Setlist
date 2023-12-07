import React, { useState } from "react";
import { Setlist } from "../../models/Setlist";
import "./playlist.css";
import { Playlist } from "../../models/Playlist";

type playlistResults = {
  playlist: Playlist | null;
  handleClick: (e: string) => void;
};
const PlaylistDisplay: React.FC<playlistResults> = ({
  playlist,
  handleClick,
}) => {
  if (!playlist) {
    return <div>No Setlists Available</div>;
  }
  const [setlistID, setSetlistID] = useState("");
  const handlePlaylistSearch = (
    event: React.MouseEvent<HTMLDivElement>,
    setID: string
  ) => {
    event.stopPropagation();
    handleClick(setID);
  };
  return (
    <div className="playlist-container">
      <div
        onClick={(e) => {
          handlePlaylistSearch(e, setlistID);
        }}
      ></div>
    </div>
  );
};
export default PlaylistDisplay;

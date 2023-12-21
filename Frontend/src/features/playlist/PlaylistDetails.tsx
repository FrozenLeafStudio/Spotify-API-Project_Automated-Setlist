import React from "react";
import "./playlist.css";

type PlaylistDetailsProps = {
  name: string;
  description: string;
  onOpenModal: () => void;
  //spotifyURL: string;
};

export const PlaylistDetails: React.FC<PlaylistDetailsProps> = ({
  name,
  description,
  onOpenModal,
  //spotifyURL
}) => {
  return (
    <div className="playlist-info">
      <h3>{name}</h3>
      <p>{description}</p>
      {/*spotifyURL ? <a href={spotifyURL}>Open Spotify Playlist</a> : null*/}
      <button onClick={onOpenModal}>View Setlist Structure</button>
    </div>
  );
};

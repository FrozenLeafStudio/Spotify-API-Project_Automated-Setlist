import React from "react";
import "./playlist.css";

type PlaylistActionsProps = {
  onPlaylistCreation: (event: React.MouseEvent<HTMLButtonElement>) => void;
  onToggleMissingTracks: () => void;
  includeCovers: boolean;
  setIncludeCovers: (include: boolean) => void;
  showMissingTracks: boolean;
};

export const PlaylistActions: React.FC<PlaylistActionsProps> = ({
  onPlaylistCreation,
  onToggleMissingTracks,
  includeCovers,
  showMissingTracks,
  setIncludeCovers,
}) => {
  const handleIncludeCoversChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setIncludeCovers(e.target.checked);
  };

  return (
    <div className="playlist-actions">
      <label>
        Include Cover Songs
        <input
          type="checkbox"
          checked={includeCovers}
          onChange={handleIncludeCoversChange}
        />
      </label>
      <button className="toggle-not-found" onClick={onToggleMissingTracks}>
        {showMissingTracks ? "Hide Missing Tracks" : "Show Missing Tracks"}
      </button>
      <button onClick={onPlaylistCreation}>Create Playlist</button>
    </div>
  );
};

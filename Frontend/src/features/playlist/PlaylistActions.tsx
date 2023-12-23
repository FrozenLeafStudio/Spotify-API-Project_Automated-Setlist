import React from "react";
import "./PlaylistActions.css";

type PlaylistActionsProps = {
  onPlaylistCreation: (event: React.MouseEvent<HTMLButtonElement>) => void;
  includeCovers: boolean;
  setIncludeCovers: (include: boolean) => void;
};

export const PlaylistActions: React.FC<PlaylistActionsProps> = ({
  onPlaylistCreation,
  includeCovers,
  setIncludeCovers,
}) => {
  const handleIncludeCoversChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setIncludeCovers(e.target.checked);
  };

  return (
    <div className="playlist-actions">
      <div className="checkbox-container">
        <label className="checkbox-label">Include Cover Songs?</label>
        <input
          type="checkbox"
          checked={includeCovers}
          onChange={handleIncludeCoversChange}
        />
      </div>
      <button className="button create-playlist" onClick={onPlaylistCreation}>
        Create Playlist
      </button>
    </div>
  );
};

import React from "react";
import { AppTrack } from "../../models/AppTrack";
import "./playlist.css";

type PlaylistItemsProps = {
  tracks: AppTrack[];
  showMissingTracks: boolean;
  includeCovers: boolean;
};

export const PlaylistItems: React.FC<PlaylistItemsProps> = ({
  tracks,
  showMissingTracks,
  includeCovers,
}) => {
  return (
    <ul>
      {tracks.map((track, index) => {
        const shouldDisplay =
          (track.trackFound || showMissingTracks) &&
          (includeCovers || !track.cover);
        return shouldDisplay ? (
          <li
            key={index}
            className={`track ${
              track.trackFound ? "found-track" : "missing-track"
            }`}
          >
            {track.trackFound && (
              <img
                src={track.albumImageUrl}
                alt={track.albumName}
                className="album-image"
              />
            )}
            <div className="track-details">
              <div className="track-name">{track.songName}</div>
              {track.details && (
                <div className="track-info">
                  <em>{track.details}</em>
                </div>
              )}
            </div>
          </li>
        ) : null;
      })}
    </ul>
  );
};

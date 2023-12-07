import React, { useState } from "react";
import { Setlist } from "../../models/Setlist";
import { MdTag, MdMusicNote, MdArrowForwardIos } from "react-icons/md";
import "./setlist.css";
type setListResults = {
  setlists: Setlist[] | null;
  handleClick: (e: string) => void;
};
const SetlistDisplay: React.FC<setListResults> = ({
  setlists,
  handleClick,
}) => {
  if (!setlists) {
    return <div>No Setlists Available</div>;
  }
  const handlePlaylistSearch = (
    event: React.MouseEvent<HTMLDivElement>,
    setID: string
  ) => {
    event.stopPropagation();
    handleClick(setID);
  };

  return (
    <div className="setlist-container">
      {setlists.map((setlist, index) => (
        <div
          className="setlist-item"
          key={setlist.setlistID}
          onClick={(e) => {
            handlePlaylistSearch(e, setlist.setlistID);
          }}
        >
          <div className="setlist-date">{setlist.eventDate}</div>
          <div className="setlist-info">
            {setlist.venueName} in {setlist.venueLocation}
            <div className="setlist-songs">
              <MdTag className="icon-tag" />
              <a
                href={setlist.url}
                target="_blank"
                rel="noopener noreferrer"
                className="setlist-link"
              >
                Setlist.fm
              </a>
              <MdMusicNote className="icon-music-note" />
              <span>{setlist.songs.length.toString()} songs</span>
            </div>
          </div>
          <h2 className="selectArrow">
            <MdArrowForwardIos />
          </h2>
        </div>
      ))}
    </div>
  );
};
export default SetlistDisplay;

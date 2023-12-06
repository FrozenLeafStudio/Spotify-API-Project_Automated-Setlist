import React from "react";
import { Setlist } from "../../models/Setlist";
import { MdTag, MdMusicNote } from "react-icons/md";
import "./setlist.css";
type setListResults = {
  setlists: Setlist[] | null;
};
const SetlistDisplay: React.FC<setListResults> = ({ setlists }) => {
  if (!setlists) {
    return <div>No Setlists Available</div>;
  }
  return (
    <div className="setlist-container">
      {setlists.map((setlist, index) => (
        <div className="setlist-item" key={setlist.setlistID}>
          <div className="setlist-date">{setlist.eventDate}</div>
          <div className="setlist-info">
            {setlist.venueName} in {setlist.venueLocation}
            <div className="setlist-songs">
              <MdMusicNote className="icon-music-note" />
              <span>{setlist.songs.length.toString()} songs</span>
              <MdTag className="icon-tag" />
              <a href={setlist.url} className="setlist-link">
                Setlist.fm
              </a>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};
export default SetlistDisplay;

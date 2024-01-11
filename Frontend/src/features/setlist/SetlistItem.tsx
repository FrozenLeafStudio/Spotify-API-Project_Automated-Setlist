import React from "react";
import { Setlist } from "../../models/Setlist";
import { MdTag, MdMusicNote, MdArrowForwardIos } from "react-icons/md";
import { animated } from "react-spring";
import { useBoop } from "../style/useBoop";

type SetlistItemProps = {
  setlist: Setlist;
  handleClick: (setlist: Setlist) => void;
};

const SetlistItem: React.FC<SetlistItemProps> = ({ setlist, handleClick }) => {
  const [style, trigger] = useBoop({ y: -10 });

  const formatDate = (dateString: string): JSX.Element[] => {
    const [day, month, year] = dateString.split("-");
    const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));

    const monthStr = date.toLocaleString("en-US", { month: "short" });
    const dayStr = date.toLocaleString("en-US", { day: "numeric" });
    const yearStr = date.toLocaleString("en-US", { year: "numeric" });

    return [
      <span key={`${dateString}-month`}>{monthStr}</span>,
      <span key={`${dateString}-day`}>{dayStr}</span>,
      <span key={`${dateString}-year`}>{yearStr}</span>,
    ];
  };

  return (
    <animated.div
      className="setlist-item"
      onMouseEnter={trigger}
      onMouseLeave={trigger}
      style={style}
      onClick={() => handleClick(setlist)}
    >
      <div className="setlist-date">{formatDate(setlist.eventDate)}</div>
      <div className="setlist-info">
        {setlist.venue.name} in {setlist.venue.city.name},{" "}
        {setlist.venue.city.state}
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
          <span>
            {setlist.sets.set.reduce(
              (totalSongs, currentSet) => totalSongs + currentSet.song.length,
              0
            )}{" "}
            songs
          </span>
        </div>
      </div>
      <h2 className="selectArrow">
        <MdArrowForwardIos />
      </h2>
    </animated.div>
  );
};

export default SetlistItem;

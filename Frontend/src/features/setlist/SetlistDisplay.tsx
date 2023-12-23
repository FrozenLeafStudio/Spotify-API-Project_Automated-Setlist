import React, { useEffect, useState } from "react";
import { Setlist } from "../../models/Setlist";
import { MdTag, MdMusicNote, MdArrowForwardIos } from "react-icons/md";
import { useSpring, animated } from "react-spring";
import "./setlist.css";
import { useBoop } from "../style/useBoop";
type setListResults = {
  setlists: Setlist[] | null;
  handleClick: (selectedSetlist: Setlist) => void;
  className?: string;
};
interface WindowSize {
  width?: number; // '?' makes the property optional
  height?: number;
}

function useWindowSize() {
  const [windowSize, setWindowSize] = useState<WindowSize>({
    width: undefined,
    height: undefined,
  });

  useEffect(() => {
    function handleResize() {
      // Update window size
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight,
      });
    }

    // Set up event listener
    window.addEventListener("resize", handleResize);
    handleResize(); // Initialize the size at the start

    // Clean up
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return windowSize;
}

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
const SetlistDisplay: React.FC<setListResults> = ({
  setlists,
  handleClick,
  className,
}) => {
  if (!setlists) {
    return <div>No Setlists Available</div>;
  }
  const handlePlaylistSearch = (
    event: React.MouseEvent<HTMLDivElement>,
    setlist: Setlist
  ) => {
    event.stopPropagation();
    handleClick(setlist);
  };

  const { width } = useWindowSize();

  return (
    <div className={`setlist-container ${className}`}>
      <div className="setlist-scroll-wrapper">
        {setlists.map((setlist) => {
          const [style, trigger] = useBoop({ y: -10 });

          return (
            <animated.div
              className="setlist-item"
              key={setlist.setlistID}
              onMouseEnter={trigger}
              onMouseLeave={trigger}
              style={style} // Apply style here
              onClick={(e) => handlePlaylistSearch(e, setlist)}
            >
              <div className="setlist-date">
                {formatDate(setlist.eventDate)}
              </div>
              <div className="setlist-info">
                {width && width <= 850 ? (
                  <>
                    {setlist.venue.name} in {setlist.venue.city.name}
                  </>
                ) : (
                  <>
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
                          (totalSongs, currentSet) =>
                            totalSongs + currentSet.song.length,
                          0
                        )}{" "}
                        songs
                      </span>
                    </div>
                  </>
                )}
              </div>
              <h2 className="selectArrow">
                <MdArrowForwardIos />
              </h2>
            </animated.div>
          );
        })}
      </div>
    </div>
  );
};
export default SetlistDisplay;

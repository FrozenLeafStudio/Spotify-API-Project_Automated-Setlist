import React, { useEffect, useRef, useCallback } from "react";
import { Setlist } from "../../models/Setlist";
import SetlistItem from "./SetlistItem";
import "./setlist.css";

type SetListResults = {
  setlists: Setlist[] | null;
  artistUrl: string | null;
  handleClick: (selectedSetlist: Setlist) => void;
  fetchMoreSetlists: () => void;
  className?: string;
};

const SetlistDisplay: React.FC<SetListResults> = ({
  setlists,
  artistUrl,
  handleClick,
  fetchMoreSetlists,
  className,
}) => {
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  const checkScrollBottom = useCallback(() => {
    if (!scrollContainerRef.current) return;

    const { scrollTop, scrollHeight, clientHeight } =
      scrollContainerRef.current;
    if (scrollTop + clientHeight >= scrollHeight - 5) {
      fetchMoreSetlists();
    }
  }, [fetchMoreSetlists]);

  useEffect(() => {
    const scrollContainer = scrollContainerRef.current;
    if (scrollContainer) {
      scrollContainer.addEventListener("scroll", checkScrollBottom);
    }

    return () => {
      if (scrollContainer) {
        scrollContainer.removeEventListener("scroll", checkScrollBottom);
      }
    };
  }, [checkScrollBottom]);

  if (!setlists || setlists.length === 0) {
    return (
      <div>
        {artistUrl && (
          <p>
            No setlists found. If you have a setlist, please add it{" "}
            <a href={artistUrl} target="_blank" rel="noopener noreferrer">
              here on setlist.fm
            </a>
          </p>
        )}
      </div>
    );
  }

  return (
    <div className={`setlist-container ${className}`}>
      <div className="setlist-scroll-wrapper" ref={scrollContainerRef}>
        {setlists.map((setlist) => (
          <SetlistItem
            key={setlist.setlistID}
            setlist={setlist}
            handleClick={handleClick}
          />
        ))}
      </div>
    </div>
  );
};

export default SetlistDisplay;

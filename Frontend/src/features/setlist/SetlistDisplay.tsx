import React, { useEffect, useRef, useCallback } from "react";
import { Setlist } from "../../models/Setlist";
import SetlistItem from "./SetlistItem"; // Adjust the path as necessary
import "./setlist.css";

type SetListResults = {
  setlists: Setlist[] | null;
  handleClick: (selectedSetlist: Setlist) => void;
  fetchMoreSetlists: () => void;
  className?: string;
};

const SetlistDisplay: React.FC<SetListResults> = ({
  setlists,
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
      // If scrolled to the bottom, fetch more setlists
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

  if (!setlists) {
    return <div>No Setlists Available</div>;
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

import React, { useEffect } from "react";
import { FcProcess } from "react-icons/fc";
import { useBoop } from "./useBoop";
import { animated } from "react-spring";
import "./Loading.css";

export const Loading: React.FC = () => {
  const [style, trigger] = useBoop({
    rotation: 360,
    timing: 4000,
    springConfig: {
      tension: 100,
      friction: 20,
    },
  });

  useEffect(() => {
    trigger();
    const interval = setInterval(trigger, 2000);

    return () => clearInterval(interval); // Cleanup the interval when component unmounts
  }, [trigger]);

  return (
    <div className="loading-overlay">
      <animated.div style={{ ...style, transformOrigin: "center" }}>
        <FcProcess className="loading-icon" />
      </animated.div>
    </div>
  );
};

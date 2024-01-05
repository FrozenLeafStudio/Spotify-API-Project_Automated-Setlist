import React from "react";
import { FcProcess } from "react-icons/fc";
import { useBoop } from "./useBoop";
import { animated } from "react-spring";
import "./Loading.css";

export const Loading: React.FC = () => {
  const [style, trigger] = useBoop({
    rotation: 360,
    timing: 1500,
    springConfig: {
      tension: 180,
      friction: 12,
    },
  });

  React.useEffect(() => {
    const intervalId = setInterval(trigger, 1500);
    return () => clearInterval(intervalId);
  }, [trigger]);

  return (
    <div className="loading-overlay">
      <animated.div style={style}>
        <FcProcess size={50} className="loading-icon" />
      </animated.div>
    </div>
  );
};

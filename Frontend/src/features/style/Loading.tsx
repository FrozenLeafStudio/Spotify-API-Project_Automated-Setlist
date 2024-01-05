import React from "react";
import { FcProcess } from "react-icons/fc";
import { useBoop } from "./useBoop";
import { animated } from "react-spring";
import "./Loading.css";

export const Loading: React.FC = () => {
  const [style, trigger] = useBoop({
    rotation: 90,
    timing: 1200,
    springConfig: {
      tension: 180,
      friction: 12,
    },
  });

  React.useEffect(() => {
    trigger();
    const intervalId = setInterval(trigger, 1200);
    return () => clearInterval(intervalId);
  }, [trigger]);

  return (
    <div className="loading-overlay">
      <animated.div style={{ ...style, transformOrigin: "center" }}>
        <FcProcess size={50} className="loading-icon" />
      </animated.div>
    </div>
  );
};

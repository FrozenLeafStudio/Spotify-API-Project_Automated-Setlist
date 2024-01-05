import React, { useEffect } from "react";
import { FcProcess } from "react-icons/fc";
import { useBoop } from "./useBoop";
import { animated } from "react-spring";
import "./Loading.css";

export const Loading: React.FC = () => {
  const [style, trigger] = useBoop({
    rotation: 360,
    timing: 4500,
    springConfig: {
      tension: 180,
      friction: 12,
    },
  });

  useEffect(() => {
    trigger(); // Trigger the animation initially
  }, [trigger]);

  return (
    <div className="loading-overlay">
      <animated.div style={{ ...style, transformOrigin: "center" }}>
        <FcProcess className="loading-icon" />
      </animated.div>
    </div>
  );
};

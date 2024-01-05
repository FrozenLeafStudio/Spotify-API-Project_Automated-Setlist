import React from "react";
import { FcProcess } from "react-icons/fc";
import { useBoop } from "./useBoop";
import { animated } from "react-spring";
import "./Loading.css";

export const Loading = () => {
  const [style] = useBoop({
    rotation: 360, // Full rotation
    y: -10, // Translate up by 10px
    timing: 1000, // Duration for one bounce and rotation cycle
    continuous: true, // Enable continuous motion
  });

  return (
    <div className="loading-overlay">
      <animated.div style={{ ...style, transformOrigin: "center" }}>
        <FcProcess size={50} className="loading-icon" />
      </animated.div>
    </div>
  );
};

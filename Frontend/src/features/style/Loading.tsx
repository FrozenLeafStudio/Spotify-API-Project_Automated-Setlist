import React from "react";
import { FcProcess } from "react-icons/fc";
import { useBoop } from "./useBoop";
import { animated } from "react-spring";
import "./Loading.css";

export const Loading: React.FC = () => {
  const [style] = useBoop({
    rotation: 0, // Start rotation
    timing: 1000, // Time for one full rotation
    continuous: true, // Enable continuous rotation
  });

  return (
    <div className="loading-overlay">
      <animated.div style={{ ...style, transformOrigin: "center" }}>
        <FcProcess size={50} className="loading-icon" />
      </animated.div>
    </div>
  );
};

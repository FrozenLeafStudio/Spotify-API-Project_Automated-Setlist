import React from "react";
import { FcProcess } from "react-icons/fc";
import { animated, useSpring } from "react-spring";
import "./Loading.css";

export const Loading: React.FC = () => {
  const rotatingStyle = useSpring({
    from: { rotateZ: 0 },
    to: { rotateZ: -360 },
    config: { duration: 6000 }, // Duration for a full 360-degree rotation
    loop: true, // Loop the animation indefinitely
    reset: true, // Reset the animation on each iteration to smoothly transition
  });

  return (
    <div className="loading-overlay">
      <animated.div style={{ ...rotatingStyle, transformOrigin: "center" }}>
        <FcProcess className="loading-icon" />
      </animated.div>
    </div>
  );
};

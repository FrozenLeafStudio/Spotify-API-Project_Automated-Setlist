import React from "react";
import { useSpring, animated } from "react-spring";
import { FcProcess } from "react-icons/fc";
import "./Loading.css";

export const Loading: React.FC = () => {
  const spin = useSpring({
    from: { transform: "rotate(0deg)" },
    to: { transform: "rotate(-360deg)" },
    config: { duration: 2000 },
    reset: true,
    loop: true,
  });

  return (
    <div className="loading-overlay">
      <animated.div style={spin}>
        <FcProcess className="loading-icon" />
      </animated.div>
    </div>
  );
};

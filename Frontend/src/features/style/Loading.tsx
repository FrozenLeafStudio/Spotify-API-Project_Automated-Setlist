import React from "react";
import { useSpring, animated } from "react-spring";
import { FcProcess } from "react-icons/fc";
import "./Loading.css";

export const Loading: React.FC = () => {
  const spin = useSpring({
    from: { transform: "rotate(0deg)" },
    to: { transform: "rotate(-360deg)" },
    config: { duration: 4000 },
    reset: true,
    loop: true,
  });

  return (
    <div className="loading-overlay">
      <animated.div style={spin}>
        <FcProcess className="loading-icon" />
      </animated.div>
      <div className="loading-text">
        Searching for setlist songs in Spotify
        {[...Array(3)].map((_, index) => (
          <AnimatedPeriod key={index} index={index} />
        ))}
      </div>
    </div>
  );
};

const AnimatedPeriod: React.FC<{ index: number }> = ({ index }) => {
  const style = useSpring({
    loop: { reverse: true },
    from: { transform: "translateY(0px) scale(1.0)" },
    to: async (next) => {
      while (1) {
        await next({ transform: "translateY(-10px) scale(1.5)" });
        await next({ transform: "translateY(0px) scale(1.0)" });
      }
    },
    config: { duration: 600 },
    delay: index * 200,
  });

  return (
    <animated.span className="animated-period" style={style}>
      .
    </animated.span>
  );
};

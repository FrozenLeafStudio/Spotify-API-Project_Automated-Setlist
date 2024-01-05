import React from "react";
import { FcProcess } from "react-icons/fc";
import { useBoop } from "../style/useBoop";
import { animated } from "react-spring";

export const Loading: React.FC = () => {
  const [style, trigger] = useBoop({
    rotation: 90,
    timing: 1000,
    springConfig: {
      tension: 200,
      friction: 10,
    },
  });

  React.useEffect(() => {
    const intervalId = setInterval(trigger, 1000);
    return () => clearInterval(intervalId);
  }, [trigger]);

  return (
    <animated.div style={style}>
      <FcProcess size={50} />
    </animated.div>
  );
};

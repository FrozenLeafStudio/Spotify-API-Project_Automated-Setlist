import { useState, useEffect, useCallback } from "react";
import { useSpring, SpringValue } from "react-spring";

type BoopConfig = {
  x?: number;
  y?: number;
  rotation?: number;
  scale?: number;
  timing?: number;
  springConfig?: object;
  continuous?: boolean;
};

export const useBoop = ({
  x = 0,
  y = 0,
  rotation = 0,
  scale = 1,
  timing = 150,
  springConfig = {
    tension: 300,
    friction: 10,
  },
  continuous = false,
}: BoopConfig): [{ transform: SpringValue<string> }, () => void] => {
  const [isBooped, setIsBooped] = useState(false);

  const style = useSpring({
    transform: `translate(${x}px, ${y}px) rotate(${rotation}deg) scale(${scale})`,
    config: springConfig,
    loop: continuous, // Loop the animation if continuous
  });

  useEffect(() => {
    if (!isBooped || continuous) {
      return;
    }

    const timeoutId = window.setTimeout(() => {
      setIsBooped(false);
    }, timing);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [isBooped, timing, continuous]);

  const trigger = useCallback(() => {
    setIsBooped(true);
  }, []);

  return [style, trigger];
};

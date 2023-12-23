//copied from https://www.joshwcomeau.com/react/boop/ and enhanced by chatGPT
import { useState, useEffect, useCallback } from "react";
import { useSpring, SpringValue } from "react-spring";

type BoopConfig = {
  x?: number;
  y?: number;
  rotation?: number;
  scale?: number;
  timing?: number;
  springConfig?: object;
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
}: BoopConfig): [{ transform: SpringValue<string> }, () => void] => {
  const [isBooped, setIsBooped] = useState(false);

  const style = useSpring({
    transform: isBooped
      ? `translate(${x}px, ${y}px) rotate(${rotation}deg) scale(${scale})`
      : "translate(0px, 0px) rotate(0deg) scale(1)",
    config: springConfig,
  });

  useEffect(() => {
    if (!isBooped) {
      return;
    }

    const timeoutId = window.setTimeout(() => {
      setIsBooped(false);
    }, timing);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [isBooped, timing]);

  const trigger = useCallback(() => {
    setIsBooped(true);
  }, []);

  return [style, trigger];
};

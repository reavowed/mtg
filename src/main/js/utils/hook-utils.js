import {useRef} from "preact/compat";
import {useEffect, useState} from "preact/hooks";

export function useRefWithEffect(handler, dependencies) {
    const [currentRef, setCurrentRef] = useState(null);
    useEffect(() => {
        if (currentRef) {
            return handler(currentRef);
        }
    }, [currentRef, ...dependencies]);
    return setCurrentRef;
}

export function useRefWithEventHandler(addHandler, removeHandler, dependencies) {
    return useRefWithEffect(currentRef => {
        addHandler(currentRef);
        if (removeHandler) {
            return () => removeHandler(currentRef);
        }
    }, [...dependencies, addHandler, removeHandler]);
}

export function useInterval(callback, delay) {
  const savedCallback = useRef();

  // Remember the latest callback.
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  // Set up the interval.
  useEffect(() => {
    function tick() {
      savedCallback.current();
    }
    if (delay !== null) {
      let id = setInterval(tick, delay);
      return () => clearInterval(id);
    }
  }, [delay]);
}

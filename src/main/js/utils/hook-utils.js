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

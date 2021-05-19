const {useEffect} = require("preact/hooks");
const {useState} = require("preact/hooks");

export function useRefWithEventHandler(addHandler, removeHandler, dependencies) {
    const [currentRef, setCurrentRef] = useState(null);
    useEffect(() => {
        if (currentRef) {
            addHandler(currentRef);
            if (removeHandler) {
                return () => removeHandler(currentRef);
            }
        }
    }, [currentRef, ...dependencies]);
    return setCurrentRef;
}

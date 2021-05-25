import {createContext} from "preact";
import {useCallback, useState} from "preact/hooks";

const ActionManager = createContext(null);
const InternalProvider = ActionManager.Provider;

ActionManager.Provider = function({children}) {
    const [rawActionHandler, setActionHandler] = useState(null);
    const [rawClassGetter, setClassGetter] = useState(null);
    const actionHandler = useCallback((objectId, event) => {
        rawActionHandler && rawActionHandler(objectId, event);
    }, [rawActionHandler])
    const getClasses = useCallback((objectId) => {
        return rawClassGetter ? rawClassGetter(objectId) : [];
    }, [rawClassGetter])
    return <InternalProvider value={{actionHandler, setActionHandler, getClasses, setClassGetter}}>{children}</InternalProvider>;
}

export default ActionManager;



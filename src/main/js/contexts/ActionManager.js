import {createContext} from "preact";
import {useCallback, useState} from "preact/hooks";

const ActionManager = createContext(null);
const InternalProvider = ActionManager.Provider;

ActionManager.Provider = function({children}) {
    const [rawActionHandler, setActionHandler] = useState(null);
    const actionHandler = useCallback((objectId, event) => {
        rawActionHandler && rawActionHandler(objectId, event);
    }, [rawActionHandler])
    return <InternalProvider value={{actionHandler, setActionHandler}}>{children}</InternalProvider>;
}

export default ActionManager;



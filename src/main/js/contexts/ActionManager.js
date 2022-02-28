import {createContext} from "preact";
import {useCallback, useState} from "preact/hooks";

const ActionManager = createContext(null);
const InternalProvider = ActionManager.Provider;

function useActionHandler() {
    const [rawActionHandler, setActionHandler] = useState(null);
    const actionHandler = useCallback((objectId, event) => {
        rawActionHandler && rawActionHandler(objectId, event);
    }, [rawActionHandler])
    return [actionHandler, setActionHandler];
}

ActionManager.Provider = function({children}) {
    const [actionHandler, setActionHandler] = useActionHandler();
    const [manaActionHandler, setManaActionHandler] = useActionHandler();
    const [rawClassGetter, setClassGetter] = useState(null);
    const getClasses = useCallback((objectId) => {
        return rawClassGetter ? rawClassGetter(objectId) : [];
    }, [rawClassGetter])
    return <InternalProvider value={{actionHandler, setActionHandler, manaActionHandler, setManaActionHandler, getClasses, setClassGetter}}>{children}</InternalProvider>;
}

export default ActionManager;



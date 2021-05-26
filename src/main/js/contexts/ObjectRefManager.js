import {createContext} from "preact";
import {useCallback, useState} from "preact/hooks";

const ObjectRefManager = createContext(null);
const InternalProvider = ObjectRefManager.Provider;

ObjectRefManager.Provider = function({children}) {
    const [objectRefsById, setObjectRefsById] = useState({});
    const getObjectRef = useCallback((objectId) => objectRefsById[objectId], [objectRefsById]);
    const setObjectRef = (objectId, objectRef) => setObjectRefsById(o => {return {...o, [objectId]: objectRef}});
    return <InternalProvider value={{getObjectRef, setObjectRef}}>{children}</InternalProvider>;
}

export default ObjectRefManager;

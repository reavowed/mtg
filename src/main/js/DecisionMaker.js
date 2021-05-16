import {useCallback, useState} from "preact/hooks";
import {post} from "./utils/fetch-helpers";

const {createContext} = require("preact");

const DecisionMaker = createContext(null);
const InternalProvider = DecisionMaker.Provider;
DecisionMaker.Provider = function({children}) {
    const [requestInProgress, setRequestInProgress] = useState(false);
    const makeDecision = useCallback((decision) => {
        if (requestInProgress) return;
        setRequestInProgress(true);
        post("$currentPath/decision", {body: decision})
            .finally(() => setRequestInProgress(false));
    }, [requestInProgress]);
    return <InternalProvider value={{makeDecision, requestInProgress}}>{children}</InternalProvider>
}
export default DecisionMaker;

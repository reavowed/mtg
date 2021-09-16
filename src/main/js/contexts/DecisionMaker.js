import {useCallback, useState} from "preact/hooks";
import {post} from "../utils/fetch-helpers";

const {createContext} = require("preact");

const DecisionMaker = createContext(null);
const InternalProvider = DecisionMaker.Provider;
DecisionMaker.Provider = function({children}) {
    const [requestInProgress, setRequestInProgress] = useState(false);

    const makeRequest = useCallback((path, args) => {
        if (requestInProgress) return;
        setRequestInProgress(true);
        post("$currentPath/" + (path || ""), args)
            .finally(() => setRequestInProgress(false));
    }, [requestInProgress]);


    const makeDecision = (decision) => makeRequest("decision", {body: decision});
    const requestUndo = () => makeRequest("requestUndo");

    return <InternalProvider value={{makeDecision, requestUndo, requestInProgress}}>{children}</InternalProvider>
}
export default DecisionMaker;

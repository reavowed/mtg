import _ from "lodash";
import {createContext} from "preact";
import {useCallback, useEffect, useState} from "preact/hooks";
import {fetch, getJson, parseJsonResponse} from "../utils/fetch-helpers";

const StopsManager = createContext(null);
const InternalProvider = StopsManager.Provider;

StopsManager.Provider = function({children}) {
    const [stops, setStops] = useState(null);
    useEffect(() => {
        getJson('$currentPath/stops').then(setStops);
    }, []);
    const isStopSet = useCallback((player, stepOrPhase) => stops && _.includes(stops[player], stepOrPhase), [stops]);
    const setStop = useCallback((player, stepOrPhase, isSet) => {
        stops && fetch('$currentPath/stops/' + player + '/' + stepOrPhase, {method: isSet ? "POST" : "DELETE"})
            .then(parseJsonResponse)
            .then(setStops);
    }, [stops]);
    return <InternalProvider value={{isStopSet, setStop}}>{children}</InternalProvider>;
}

export default StopsManager;
